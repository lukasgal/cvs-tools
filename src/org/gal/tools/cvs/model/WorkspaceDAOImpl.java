package org.gal.tools.cvs.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gal.tools.model.SQLSelectCommand;
import org.gal.tools.utils.DBUtils;

public class WorkspaceDAOImpl implements WorkspaceDAO {
	
	
	public WorkspaceDAOImpl(){
	
	}
	
	private SQLSelectCommand insertWorkspaceCmd(WorkspaceVO ws){
		SQLSelectCommand cmd = new SQLSelectCommand() {
			public String toString(){
				return "insertWorkspaceCmd";
			}
			
			@Override
			public  List<Integer> execute(Connection conn) throws SQLException {
				PreparedStatement stm;
		        conn.setAutoCommit(false);
		        List<Integer> results = new ArrayList<>();
		        stm = conn.prepareStatement("INSERT INTO WORKSPACE (NAME,PATH) VALUES(?,?)");
		        stm.setString(1, ws.getName());
		        stm.setString(2,ws.getPath());;
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
	
	@Override
	public Integer insertWorkspace(WorkspaceVO ws){
		List<Integer> results = DBUtils.executeSelectCommand(insertWorkspaceCmd(ws));
		if(results!=null && results.size()==1){
			return results.get(0);
		}
		return null;
	}
	
	@Override
	public Integer updateWorkspaceName(WorkspaceVO ws){
		List<Integer> results = DBUtils.executeSelectCommand(updateWorkspaceNameCmd(ws));
		if(results!=null && results.size()==1){
			return results.get(0);
		}
		return null;
	}

	public SQLSelectCommand getWorkspaceByPathCmd(String path) {
		SQLSelectCommand cmd = new SQLSelectCommand() {

			public String toString(){
				return "getWorkspaceByPathCmd";
			}

			
			@Override
			public List<WorkspaceVO> execute(Connection conn) throws SQLException {
				String selectSQL = "SELECT ID, Name, PATH FROM WORKSPACE WHERE PATH = ?";
				List<WorkspaceVO> results = new ArrayList<>();
				PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
				preparedStatement.setString(1, path);
				ResultSet rs = preparedStatement.executeQuery();
				WorkspaceVO ws = null;
				while (rs.next()) {
					ws = new WorkspaceVO();
					ws.setId(rs.getInt(1));
					ws.setName(rs.getString(2));
					ws.setPath(rs.getString(3));
					results.add(ws);
				}
				return results;		
			}
		};
		return cmd;
	}
	
	@Override
	public WorkspaceVO getWorkspaceByPath(String path) {
		List<WorkspaceVO> results = DBUtils.executeSelectCommand(getWorkspaceByPathCmd(path));
		if(results!=null && results.size()==1){
			return results.get(0);
		}
		return null;
	}
	private SQLSelectCommand updateWorkspaceNameCmd(WorkspaceVO ws){
		SQLSelectCommand cmd = new SQLSelectCommand() {
			public String toString(){
				return "updateWorkspaceNameCmd";
			}
			
			@Override
			public  List<Integer> execute(Connection conn) throws SQLException {
				PreparedStatement stm;
		        conn.setAutoCommit(false);
		        List<Integer> results = new ArrayList<>();
		        stm = conn.prepareStatement("UPDATE WORKSPACE SET NAME=? WHERE ID=?");
		        stm.setString(1, ws.getName());
		        stm.setInt(2,ws.getId());
		        stm.executeUpdate();
		        conn.commit();
		        return results;		
			}
		};
		return cmd;
	}

}
