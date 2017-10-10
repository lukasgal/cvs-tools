package org.gal.tools.cvs.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.gal.tools.cvs.controllers.CVSHistoryController;
import org.gal.tools.model.SQLCommand;
import org.gal.tools.model.SQLSelectCommand;
import org.gal.tools.utils.DBUtils;
import org.gal.tools.utils.Logger;


public class ChangeSetDAOImpl implements ChangeSetDAO{
	
	private static final String SELECT_ALL = "SELECT ch.ID as CH_ID,ch.NAME as CH_NAME, ch.COMMIT_DATE_TIME as CH_DATE, ch.COMMITED as CH_COMMITED,"
			+"ws.ID as WS_ID, ws.NAME as WS_NAME, ws.PATH as WS_PATH,"
			+"r.ID as R_ID, r.NAME as R_NAME, r.RELATIVE_PATH as R_REL_PATH, r.ABSOLUTE_PATH as R_ABS_PATH, r.OPERATION_TYPE as R_OP_TYPE, r.REVISION as R_REVISION"
				+" FROM CHANGESET ch "
				+" JOIN WORKSPACE ws ON(ws.ID=ch.Workspace_ID) "
				+" LEFT JOIN RESOURCE r ON (ch.ID=r.CHANGESET_ID AND r.RELATIVE_PATH<>'')";
	
	private static final String SELECT_WITH_LIMIT = "SELECT ch.ID as CH_ID,ch.NAME as CH_NAME, ch.COMMIT_DATE_TIME as CH_DATE, ch.COMMITED as CH_COMMITED, ws.ID as WS_ID, ws.NAME as WS_NAME, ws.PATH as WS_PATH,r.ID as R_ID, r.NAME as R_NAME, r.RELATIVE_PATH as R_REL_PATH, r.ABSOLUTE_PATH as R_ABS_PATH, r.OPERATION_TYPE as R_OP_TYPE, r.REVISION as R_REVISION "
			+ " FROM " 
			+" (SELECT * FROM CHANGESET ORDER BY COMMIT_DATE_TIME DESC LIMIT ?) ch"
			+" JOIN WORKSPACE ws ON(ws.ID=ch.Workspace_ID) "
			+" JOIN RESOURCE r ON (ch.ID=r.CHANGESET_ID AND r.RELATIVE_PATH<>'')";


	public ChangeSetDAOImpl(){
		
	}
	
	
	public List<AbstractTreeNode> filterByName(String chName, String rName, Integer workspaceId, Long dateFrom, Long dateTo, boolean useRegexp) {
		return DBUtils.executeSelectCommand(filterByNameCmd(chName, rName, workspaceId, dateFrom, dateTo, useRegexp));
	}

	
	public List<AbstractTreeNode> getSimilarChangeSet(ChangeSet chs) {
		return DBUtils.executeSelectCommand(getSimilarChangeSetCmd(chs));
	}

	
	@Override
	public List<AbstractTreeNode> getAllChangeSets(Integer wsId, Integer limit,  Long dateFrom, Long dateTo) {
		return DBUtils.executeSelectCommand(getAllChangeSetsCmd(wsId, limit, dateFrom, dateTo));
	}

	@Override
	public ChangeSet getChangeSet(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void insertChangeSet(ChangeSet changeSet){
		DBUtils.executeCommand(insertChangeSetCmd(changeSet));
	}
	

	@Override
	public void updateChangeSet(ChangeSet changeSet){
		DBUtils.executeCommand(updateChangeSetCmd(changeSet));
	}
	

	@Override
	public void deleteChangeSet(ChangeSet changeSet) {
		// TODO Auto-generated method stub
		
	}

	public ChangeSet getChangeSetFromDB(ChangeSet chs){
        List<ChangeSet> list = DBUtils.executeSelectCommand(getChangeSetFromDBCmd(chs));
        if(list!=null && list.size()==1) {
        	return list.get(0);
        }
        return null;
	}

	
	private String prepareString(String name){
		if(name==null){
			return "%";
		}
		name = "*"+name+"*";
		return name.replace("*", "%");
	}
	
	/**
	 * ChS name, ChS datetime, WS name, RES relative_path
	 */
	private List<ChangeSet> getRecords(ResultSet rs) throws SQLException{
		List<ChangeSet> changeSets = new ArrayList<>();
            Integer oldChId = 0, oldWsId = 0;
        	WorkspaceVO ws = null;
        	ChangeSetVO changeSet = null;
        	
            while (rs.next()) {
            	Integer chID = rs.getInt("CH_ID");
            	Integer wsID = rs.getInt("WS_ID");
            	if(!oldWsId.equals(wsID)){
            		ws = new WorkspaceVO();
            		ws.setId(wsID);
            		ws.setName(rs.getString("WS_NAME"));
            		ws.setPath(rs.getString("WS_PATH"));
            		/*if(CVSHistoryController.getCurrentWorkspace().getId()==wsID){
            			ws.setHome(true);
            		}*/
            	}
            	if(!oldChId.equals(chID)){
            		changeSet = new ChangeSetVO();
            		changeSet.setId(chID);
                	changeSet.setName(rs.getString("CH_NAME"));
                    changeSet.setCommitDateTime(rs.getLong("CH_DATE"));
                    changeSet.setWorkspace(ws);
                    changeSet.setCommited(rs.getBoolean("CH_COMMITED"));
                    changeSets.add(changeSet);
            	}
            	ResourceVO res = new ResourceVO();
            	res.setId(rs.getInt("R_ID"));
            	res.setAbsolutePath(rs.getString("R_ABS_PATH"));
            	res.setRelativePath(rs.getString("R_REL_PATH"));
            	res.setName(rs.getString("R_NAME"));
            	res.setOperationType(rs.getInt("R_OP_TYPE"));
            	res.setRevision(rs.getString("R_REVISION"));
            	res.setParent(changeSet);
            	changeSet.addRow(res);
            	oldChId=chID;
            	oldWsId=wsID;
            }
        return changeSets;
	}
	
	private SQLCommand insertChangeSetCmd(ChangeSet changeSet){
		
		SQLCommand cmd = new SQLCommand() {

			@Override
			public String toString(){
				return "insertChangeSetCmd";
			}
			
			@Override
			public void execute(Connection conn) throws SQLException {
				PreparedStatement stm;
				Connection connection = (Connection)conn;
				connection.setAutoCommit(false);		        	
		        stm = connection.prepareStatement("INSERT INTO CHANGESET (NAME,COMMIT_DATE_TIME,WORKSPACE_ID,COMMITED) VALUES(?,?,?,?)");
		        stm.setString(1, changeSet.getName());
		        stm.setLong(2, changeSet.getCommitDateTime().getTimeStamp());
		        stm.setInt(3, changeSet.getWorkspace().getId());
		        stm.setBoolean(4,changeSet.isCommited());
		        stm.executeUpdate();
		        ResultSet rs = stm.getGeneratedKeys();
		        int last_id = 0;
		        if(rs.next()){
		        	last_id = rs.getInt(1);
		        }
		        stm = connection.prepareStatement("INSERT INTO Resource (RELATIVE_PATH,ABSOLUTE_PATH, CHANGESET_ID, NAME, OPERATION_TYPE,REVISION) VALUES(?,?,?,?,?,?)"); 
		        for(AbstractTreeNode res : changeSet.getRows()){
		        	ResourceVO r = ((ResourceVO)res);
		        	stm.setString(1, r.getRelativePath());
		        	stm.setString(2, r.getAbsolutePath());
		        	stm.setInt(3, last_id);
		        	stm.setString(4, r.getName());
		        	stm.setInt(5, r.getOperationType());		        	
		        	stm.setString(6, r.getRevision());
		        	stm.addBatch();
		        }
		        stm.executeBatch();
		        connection.commit();
		        changeSet.setId(last_id);
		        changeSet.setSaved(true);
			}
		};
		return cmd;
	}

	
	private SQLCommand updateChangeSetCmd(ChangeSet changeSet){
		
		SQLCommand cmd = new SQLCommand() {

			@Override
			public String toString(){
				return "updateChangeSetCmd";
			}
			
			@Override
			public void execute(Connection connection) throws SQLException {
				connection.setAutoCommit(false);
				PreparedStatement stm;
		        stm = connection.prepareStatement("UPDATE CHANGESET SET NAME=?, COMMIT_DATE_TIME=?, COMMITED=? WHERE ID=?");
		        stm.setString(1, changeSet.getName());
		        stm.setLong(2, changeSet.getCommitDateTime().getTimeStamp());
		        stm.setBoolean(3, changeSet.isCommited());
		        stm.setInt(4, changeSet.getId());
		        
		        stm.executeUpdate();
	
		        stm = connection.prepareStatement("DELETE FROM RESOURCE WHERE CHANGESET_ID=?");
		        stm.setInt(1, changeSet.getId());
		        stm.executeUpdate();
		        
		        stm = connection.prepareStatement("INSERT INTO Resource (RELATIVE_PATH,ABSOLUTE_PATH, CHANGESET_ID, NAME, OPERATION_TYPE, REVISION) VALUES(?,?,?,?,?,?)"); 
		        for(AbstractTreeNode res : changeSet.getRows()){
		        	ResourceVO r = ((ResourceVO)res);
		        	stm.setString(1, r.getRelativePath());
		        	stm.setString(2, r.getAbsolutePath());
		        	stm.setInt(3, changeSet.getId());
		        	stm.setString(4, r.getName());
		        	stm.setInt(5, r.getOperationType());
		        	stm.setString(6, r.getRevision());
		        	stm.addBatch();
		        }
		        stm.executeBatch();
		        connection.commit();
			}
		};
		return cmd;
	}
	
	private SQLSelectCommand getAllChangeSetsCmd(Integer wsId, Integer limit, Long dateFrom, Long dateTo) {
		SQLSelectCommand cmd = new SQLSelectCommand() {
			
			@Override
			public String toString(){
				return "getAllChangeSetsCmd";
			}
			
			@Override
			public  List<ChangeSet> execute(Connection conn) throws SQLException {
				List<ChangeSet> changeSets = null;
				PreparedStatement stm;
				String sql = SELECT_WITH_LIMIT;
				if(wsId!=null){
					sql +=" WHERE ws.id=? ";
				}
				if(dateFrom!=null && dateTo!=null){
					sql+=" AND  (ch.COMMIT_DATE_TIME>=? AND ch.COMMIT_DATE_TIME<=?) ";
				}
				 sql+= " ORDER BY ws.name DESC, ws.id, ch.COMMIT_DATE_TIME DESC, r.RELATIVE_PATH ASC";
				//System.out.println(sql);
				// Logger.addMessage(dateFrom + " "+ dateTo);
		        stm = conn.prepareStatement(sql);
		        stm.setInt(1, limit);
		        if(wsId!=null){
					stm.setInt(2, wsId);
					if(dateFrom!=null && dateTo!=null){
		        		stm.setFloat(3, dateFrom);
		        		stm.setFloat(4, dateTo);
		        	}
				}else{
					if(dateFrom!=null && dateTo!=null){
		        		stm.setFloat(2, dateFrom);
		        		stm.setFloat(3, dateTo);
		        	}
				}
		        ResultSet rs = stm.executeQuery();
		        changeSets = getRecords(rs);
		        rs.close();
		        stm.close();
		        System.out.println(sql);
		        return changeSets;		
			}
		};
		return cmd;
	}
	
	private SQLSelectCommand getChangeSetFromDBCmd(ChangeSet chs){
		SQLSelectCommand cmd = new SQLSelectCommand() {
			

			@Override
			public String toString(){
				return "getChangeSetFromDBCmd";
			}
			
			@Override
			public  List<ChangeSet> execute(Connection conn) throws SQLException {
				List<ChangeSet> changeSets = null;
				PreparedStatement stm;
				ChangeSet found = null;
		    
		        String sql = SELECT_ALL + "  WHERE ch.NAME=? AND ch.WORKSPACE_ID=? AND ch.COMMITED=0 ";
		        stm = conn.prepareStatement(sql);
		        stm.setString(1, chs.getName());
		        stm.setInt(2, chs.getWorkspace().getId());            
		        ResultSet rs = stm.executeQuery();
		        changeSets = getRecords(rs);
		        for(ChangeSet ch : changeSets){
		        	if(chs.compareTo(ch)==0){
		        		found = ch;
		        		break;
		        	}
		        }
		        rs.close();
		        stm.close();
				return changeSets;	
			}
		};		
		return cmd;
	}
	
	private SQLSelectCommand filterByNameCmd(String chName, String rName, Integer workspaceId, Long dateFrom, Long dateTo, boolean useRegexp){
		SQLSelectCommand cmd = new SQLSelectCommand() {
			
			@Override
			public String toString(){
				return "filterByNameCmd";
			}
			
			@Override
			public  List<ChangeSet> execute(Connection conn) throws SQLException {

				String chsName = useRegexp ? chName : prepareString(chName);
				String resName = useRegexp ? rName : prepareString(rName);
				List<ChangeSet> changeSets = null;
				PreparedStatement stm;
				String sql = SELECT_ALL;
				if(!useRegexp){
					sql+= " WHERE (ch.NAME LIKE ? OR r.RELATIVE_PATH LIKE ?)";	
				}else {
					sql+= " WHERE (ch.NAME REGEXP(?) OR r.RELATIVE_PATH REGEXP(?))";
				}
				
				
				if(workspaceId!=null){
					sql+=" AND  ws.Id=? ";
				}
				if(dateFrom!=null && dateTo!=null){
					sql+=" AND  (ch.COMMIT_DATE_TIME>=? AND ch.COMMIT_DATE_TIME<=?) ";
				}
				
				sql+= " ORDER BY ws.name DESC, ws.id, ch.COMMIT_DATE_TIME DESC, r.RELATIVE_PATH ASC";
				Logger.addMessage(sql);
		        stm = conn.prepareStatement(sql);
		        
		        stm.setString(1, chsName);
		        stm.setString(2, resName);
		        if(workspaceId!=null){
		        	stm.setInt(3, workspaceId);
		        	if(dateFrom!=null && dateTo!=null){
		        		stm.setFloat(4, dateFrom);
		        		stm.setFloat(5, dateTo);
		        	}
		        }else{
		        	if(dateFrom!=null && dateTo!=null){
		        		stm.setFloat(3, dateFrom);
		        		stm.setFloat(4, dateTo);
		        	}
		        }
		        ResultSet rs = stm.executeQuery();
		        changeSets = getRecords(rs);
		        rs.close();
		        stm.close();
		    
		        return changeSets;
			}
		};
		return cmd;
	}

	
	private SQLSelectCommand getSimilarChangeSetCmd(ChangeSet chs){
		String chName = chs.getName(); 
		Long dateTime = chs.getCommitDateTime().getTimeStamp();
		Integer workspaceId = chs.getWorkspace().getId();
		
		SQLSelectCommand cmd = new SQLSelectCommand() {
			
			@Override
			public String toString(){
				return "getSimilarChangeSetCmd";
			}
			
			@Override
			public  List<ChangeSet> execute(Connection conn) throws SQLException {

				String chsName = chName;
				List<ChangeSet> changeSets = null;
				PreparedStatement stm;
				String sql = SELECT_ALL;
				
				sql+= " WHERE  (ch.NAME=? AND ch.COMMIT_DATE_TIME BETWEEN ? AND ?) ";
				if(workspaceId!=null){
					sql+=" AND  ws.Id=? ";
				}
				sql+= " ORDER BY ws.name DESC, ws.id, ch.COMMIT_DATE_TIME DESC, r.RELATIVE_PATH ASC";
		        
		        stm = conn.prepareStatement(sql);
		        
		        stm.setString(1, chsName);
		        stm.setLong(2, dateTime - 60000);
		        stm.setLong(3, dateTime + 60000);
		        if(workspaceId!=null){
		        	stm.setInt(4, workspaceId);
		        }
		        ResultSet rs = stm.executeQuery();
		        changeSets = getRecords(rs);
		        rs.close();
		        stm.close();
		    
		        return changeSets;
			}
		};
		return cmd;
	}

	
	public static void main(String[] args){
		
		ChangeSetDAOImpl dao = new ChangeSetDAOImpl();
		List<AbstractTreeNode> chsList = dao.filterByName(null,"*.js", null, null, null, false); //dao.getAllChangeSets();
		if(chsList!=null){
			for(AbstractTreeNode ch : chsList){
				System.out.println(ch);
			}	
		}
	}
}
