package org.gal.tools.cvs.model;

public interface WorkspaceDAO {
	
	public Integer insertWorkspace(WorkspaceVO ws);
	
	public WorkspaceVO getWorkspaceByPath(String path);

	public Integer updateWorkspaceName(WorkspaceVO ws);

}
