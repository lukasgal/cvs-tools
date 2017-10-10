package org.gal.tools.cvs.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gal.tools.model.SQLSelectCommand;
import org.gal.tools.utils.DBUtils;

public class JIRACommentDAOImpl implements JIRACommentDAO {
	
	private static JIRACommentDAOImpl m_instance;
	
	private JIRACommentDAOImpl(){
		
	}
	
	public static JIRACommentDAOImpl getInstance() {
		if(m_instance==null){
			m_instance = new JIRACommentDAOImpl();
		}
		return m_instance;
	}
	
	
	@Override
	public Integer insertComment(JIRACommentVO ws){
		List<Integer> results = DBUtils.executeSelectCommand(insertJIRACommentCmd(ws));
		if(results!=null && results.size()==1){
			return results.get(0);
		}
		return null;
	}
	
	
	
	private SQLSelectCommand insertJIRACommentCmd(JIRACommentVO ws){
		SQLSelectCommand cmd = new SQLSelectCommand() {
			public String toString(){
				return "insertJIRACommentCmd";
			}
			
			@Override
			public  List<Integer> execute(Connection conn) throws SQLException {
				PreparedStatement stm;
		        conn.setAutoCommit(false);
		        List<Integer> results = new ArrayList<>();
		        stm = conn.prepareStatement("INSERT INTO JIRACOMMENT (ISSUE_NUMBER, TEXT, CREATED, RESPONSE_TEXT) VALUES(?,?,?,?)");
		        stm.setString(1, ws.getIssueNumber());
		        stm.setString(2,ws.getText());
		        stm.setLong(3, ws.getCreated().getTimeStamp());
		        stm.setString(4,ws.getResponseText());
		        stm.executeUpdate();
		        conn.commit();
		        ResultSet rs = stm.getGeneratedKeys();
		        Integer last_id = 0;
		        if(rs.next()){
		        	last_id = rs.getInt(1);
		        	results.add(last_id);
		        }
		        return results;		
			}
		};
		return cmd;
	}
}