package org.gal.tools.cvs.model;

import java.util.List;

import org.eclipse.core.resources.IResource;

public interface ChangeSet {
	
	public Integer getId();
	
	public WorkspaceVO getWorkspace();

	public CommitDateTime getCommitDateTime();
		
	public IResource[] getResources();

	public String getName();

	public void setId(Integer id);
	
	public boolean isSaved();
	
	public boolean isCommited();
	
	public List<AbstractTreeNode> getRows();
	
	public int compareTo(ChangeSet o);
	
	public void setCommited(Boolean commited);
	
	public void setCommitDateTime(Long commitDateTime);

	public void setSaved(boolean b);
	
	public void mergeResources(ChangeSet chs);
}
