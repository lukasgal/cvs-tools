package org.gal.tools.cvs.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.gal.tools.cvs.comparators.TreeNodeCompareByName;
import org.gal.tools.utils.Utils;



public class ChangeSetVO extends AbstractTreeNode implements ChangeSet, Comparable<ChangeSet>, Cloneable{

	
	private CommitDateTime commitDateTime;
	
	private WorkspaceVO workspace;
	
	private boolean saved;

	private boolean commited;

	
	
	public ChangeSetVO(){
		rows = new ArrayList<>();
	}
	
	@Override
	public CommitDateTime getCommitDateTime() {
		return commitDateTime;
	}

	@Override
	public IResource[] getResources() {
		return (IResource[])rows.toArray();
	}

	public WorkspaceVO getWorkspace() {
		return workspace;
	}

	public void setWorkspace(WorkspaceVO workspace) {
		this.workspace = workspace;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCommitDateTime(Long commitDateTime) {
		this.commitDateTime = new CommitDateTime(commitDateTime);
	}
	
	public void setCommitDateTime(CommitDateTime cdt){
		this.commitDateTime = cdt;
	}

	public void setResources(ArrayList<AbstractTreeNode> resources) {
		this.rows = resources;
	}

	public void setSaved(boolean saved){
		this.saved = saved;
	}
	
	@Override
	public boolean isSaved() {
		return saved;
	}	
	
	public String getFormattedCommitDateTime(){
		return commitDateTime.getFormattedCommitDateTime();
	}
	
	public String getFormattedCommitDate(){
		return commitDateTime.getFormattedCommitDate();
	}

	
	public String toString(){
		StringBuilder str = new StringBuilder();
		
		str.append(getName()).append("\n");
		//str.append("commited:"+isCommited()).append("\n");
		str.append(getFormattedCommitDateTime()).append("\n");
		//str.append("WS:").append(getWorkspace().getName()).append("\n");
		for(AbstractTreeNode res : getRows()){
			str.append(res).append("\n");
		}
		return str.toString();
	}

	public boolean isCommited() {
		return commited;
	}

	public void setCommited(Boolean commited) {
		this.commited = commited;
	}
	
	public ResourceVO getResourceByPath(String path){
		for(AbstractTreeNode row : getRows()){
			ResourceVO res = (ResourceVO)row;
			if(res.getRelativePath().contentEquals(path)){
				return res;
			}
		}
		return null;
	}
	
	@Override
	public int compareTo(org.gal.tools.cvs.model.ChangeSet o) {
		boolean equals = getName().compareTo(o.getName()) == 0 
				&& getWorkspace().getId()==o.getWorkspace().getId()
				&& getRows().equals(o.getRows());
		return equals ? 0 : -1;
	}


	/*
	public boolean getRow(TreeNode row) {
		if(row instanceof ResourceVO){
			for(TreeNode r : rows){
				ResourceVO res = (ResourceVO) row;
				if(res.equals(row)){
					return true;
				}
			}	
			return false;
		}else{
			return rows.contains(row);	
		}
	}*/

	@Override
	public void mergeResources(ChangeSet chs) {
		if(chs.getRows()==null || chs.getRows().isEmpty()) return;
		for(AbstractTreeNode res : chs.getRows()){
			if(res instanceof ResourceVO){
				if(!getRows().contains(res)){
					addRow(res);
				}
			}
		}
		getRows().sort(new TreeNodeCompareByName(true));
	}
	
	
	@SuppressWarnings("unchecked")
	public ChangeSetVO clone(){
		ChangeSetVO clone = new ChangeSetVO();
		clone.setName(getName());
		clone.setId(getId());
		Long dt = getCommitDateTime()==null ? 0L : getCommitDateTime().getTimeStamp();
		clone.setCommitDateTime(new CommitDateTime(dt));
		clone.setRows((ArrayList<AbstractTreeNode>) getRows().clone());
		clone.setWorkspace(getWorkspace());
		clone.setSaved(isSaved());
		clone.setCommited(isCommited());
		return clone;
	}
}
