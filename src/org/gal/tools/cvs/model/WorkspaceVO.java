package org.gal.tools.cvs.model;

import java.util.ArrayList;

public class WorkspaceVO extends AbstractTreeNode implements Comparable<WorkspaceVO>, Cloneable{
	
	private String path;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append(getId()).append(" : ").append(getName()).append("(").append(getPath()).append(")");
		return str.toString();
	}
	
	@Override
	public int compareTo(WorkspaceVO o) {
		return getPath().compareTo(o.getPath());
	}

	public WorkspaceVO clone(){
		WorkspaceVO ws = new WorkspaceVO();
		ws.setId(getId());
		ws.setName(getName());
		ws.setPath(getPath());
		return ws;
	}
}
