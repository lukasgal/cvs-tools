package org.gal.tools.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.gal.tools.cvs.model.ChangeSetVO;
import org.gal.tools.cvs.model.WorkspaceVO;
import org.gal.tools.model.Command;
import org.gal.tools.model.SQLCommand;
import org.gal.tools.model.SQLSelectCommand;
import org.sqlite.Function;

/**
 *
 * @author oem
 */
public final class DBUtils {

    private static final String SQL_CREATE = " CREATE TABLE IF NOT EXISTS WORKSPACE "
            + "("
            + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " Name TEXT NOT NULL, "
            + " PATH TEXT NOT NULL "
            + ");"    		
    		+ " CREATE TABLE IF NOT EXISTS CHANGESET "
            + "("
            + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " NAME TEXT NOT NULL, "
            + " COMMIT_DATE_TIME INTEGER NULL, "
            + " WORKSPACE_ID INTEGER NOT NULL,"
            + " COMMITED INTEGER DEFAULT 0,"
            + " FOREIGN KEY(WORKSPACE_ID) REFERENCES WORKSPACE(ID)"
            + "); "
            + " CREATE TABLE IF NOT EXISTS RESOURCE "
            + "("
            + " ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " NAME TEXT NOT NULL,"
            + " RELATIVE_PATH TEXT NOT NULL, "
            + " ABSOLUTE_PATH TEXT NOT NULL, "
            + " CHANGESET_ID INTEGER NOT NULL,"
            + " OPERATION_TYPE INTEGER DEFAULT 1,"
            + " REVISION TEXT,"
            + " FOREIGN KEY(CHANGESET_ID) REFERENCES CHANGESET(ID)"
            + ");"
            +"CREATE INDEX IF NOT EXISTS R_INDEX ON RESOURCE (NAME, RELATIVE_PATH ASC);"
            +"CREATE INDEX IF NOT EXISTS CHS_INDEX ON CHANGESET (NAME ASC,COMMIT_DATE_TIME ASC);";

    
    private final static String SQL_UPDATE = " CREATE TABLE IF NOT EXISTS JIRACOMMENT "
            + "("
            + " ID INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " ISSUE_NUMBER TEXT NOT NULL,"
            + " TEXT TEXT NOT NULL, "
            + " CREATED INTEGER DEFAULT 0,"
            + " RESPONSE_TEXT TEXT"
            + ");" ;
    
    private Connection connection;
	
	private final static String KEY_DB_PATH = "database";
	private final static String KEY_DB_USERNAME = "user";
	private final static String KEY_DB_PASSWORD = "password";
	
    private static final String DB_NAME = "cvstools.db";
    private static Config m_config;
    
    private static DBUtils instance = null;
    
    public static DBUtils getInstance() {
    	if(instance==null){
    		instance = new DBUtils();
    	}
        return instance;
    }
    
    public static String getDBPath(){
   		return m_config.getProperty(KEY_DB_PATH, m_config.getPath()+DB_NAME);
    }
    
    private DBUtils() {
    	m_config = Config.getInstance();
        //this.init();
        //createDatabaseSchema(getConnection());
    }

    private static Connection getConnection(){
        try {
        	DBUtils.getInstance();
        	String dbPath = getDBPath();
            Class.forName("org.sqlite.JDBC");
            boolean l_exists = new File(dbPath).exists(); 
            String l_user = m_config.getProperty(KEY_DB_USERNAME, "cvstools");
            String l_psw = m_config.getProperty(KEY_DB_PASSWORD, "cvstools");
            
            Connection con = DriverManager.getConnection("jdbc:sqlite:" +  dbPath, l_user, l_psw);
            if(!l_exists){
            	createDatabaseSchema();
            }
            Function.create(con, "REGEXP", new Function() {
                @Override
                protected void xFunc() throws SQLException {
                    String expression = value_text(0);
                    String value = value_text(1);
                    if (value == null)
                        value = "";

                    Pattern pattern=Pattern.compile(expression);
                    
                    result(pattern.matcher(value).find() ? 1 : 0);
                }
            });

            
            Logger.addMessage("Connected to "+dbPath);
            return con;
        } catch (ClassNotFoundException | SQLException e) {
        	Logger.addStackTrace(e);
        }
		return null;
    }
    
    public static <T> List<T> executeSelectCommand(SQLSelectCommand cmd){
    	Connection con = getConnection();
    	
    	try{
    		if(con==null){
    			throw new RuntimeException("Connection is not available.");
    		}
    		List<T> exec = cmd.execute(con);
    		Logger.addMessage("Select command "+cmd+" has been executed.");
    		return exec;
    	}catch(Exception e){
    		Logger.addStackTrace(e);
    	}
    	
    	finally{
    		closeConnection(con);
    	}
    	return null;
    }
    
    
    
    public static void executeCommand(SQLCommand cmd){
    	Connection con = getConnection();
    	try{
    		if(con==null){
    			throw new RuntimeException("Connection is not available.");
    		}
    		cmd.execute(con);
    		Logger.addMessage("Command "+cmd+" has been executed.");
    	}catch(Exception e){
    		Logger.addStackTrace(e);
    	}
    	
    	finally{
    		closeConnection(con);
    	}
    }
    
    private static void closeConnection(Connection con){
    	if(con==null) return;
    	try{
    		con.close();
    		Logger.addMessage("Disconnected from "+getDBPath());
    	}catch(SQLException ex){
    		Logger.addStackTrace(ex);
    	}
    	
    }
        
    private void init() {
        if (this.connection != null) {
            return;
        }
        try {
            Class.forName("org.sqlite.JDBC");
            Connection con = DriverManager.getConnection("jdbc:sqlite:" + getDBPath(), "cvstools", "cvstools");
            this.connection = con;
        } catch (ClassNotFoundException | SQLException e) {
        	Logger.addStackTrace(e);
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
    //        super.finalize();
            connection.close();
        } catch (SQLException ex) {
        	Logger.addStackTrace(ex);
        } finally {

        }
    }

    public void executeUpdate(String sql) {
        Statement stm;
        try {
            stm = getConnection().createStatement();
            stm.executeUpdate(sql);
            stm.close();
        } catch (SQLException ex) {
        	Logger.addStackTrace(ex);
        }
    }

    private static void createDatabaseSchema() throws SQLException {
        Statement stm;
        Connection c = getConnection();
        try{
        	String[] sqls = DBUtils.SQL_CREATE.split(";");
            for (String sql : sqls) {
                
            	Logger.addMessage(sql);
                stm = c.createStatement();
                stm.executeUpdate(sql);
            
            }
            Logger.addMessage("DB schema has been created.");
            m_config.setProperty(KEY_DB_PATH, getDBPath());
            m_config.setProperty(KEY_DB_USERNAME, "cvstools");
            m_config.setProperty(KEY_DB_PASSWORD, "cvstools");
	
        }catch(Exception e){
        	Logger.addStackTrace(e);
        }
        
        finally{
        	closeConnection(c);
        }
    }
    
    public static void updateDatabaseSchema() {
        Statement stm;
        Connection c = getConnection();
        try{
        	String[] sqls = DBUtils.SQL_UPDATE.split(";");
            for (String sql : sqls) {
                
            	//Logger.addMessage(sql);
                stm = c.createStatement();
                stm.executeUpdate(sql);
            
            }
        }catch(Exception e){
        	Logger.addStackTrace(e);
        }
        
        finally{
        	closeConnection(c);
        }
    }

    public static void test(){
    	WorkspaceVO ws = new WorkspaceVO();
    	ws.setName("MY WS");
    	ws.setPath("local");
    	
    	ChangeSetVO chs = new ChangeSetVO();
    	chs.setName("test VO");
    	chs.setWorkspace(ws);
    	ArrayList<IResource> res= new ArrayList<>();
    		
    }
    
    public static void main(String[] args) {    
    	//createDatabaseSchema(DBUtils.getInstance().getConnection());
    	//DBUtils.test();
    	DBUtils.getConnection();
    }
}
