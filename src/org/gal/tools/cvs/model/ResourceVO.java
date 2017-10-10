package org.gal.tools.cvs.model;
	
import java.util.ArrayList;

public class ResourceVO extends AbstractTreeNode implements Comparable<ResourceVO>, Cloneable{

	
	private String relativePath;

	private String absolutePath;

	private String revision = "";
	
	private Integer operationType = 0;

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String toString(){
		return getRelativePath();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ResourceVO){
			return ((ResourceVO) obj).getRelativePath().compareTo(getRelativePath()) == 0;
		}
		return false;
	}
	

	@Override
	public int hashCode() {
		return getRelativePath().hashCode();
	}

	@Override
	public int compareTo(ResourceVO o) {
		return getRelativePath().compareTo(o.getRelativePath());
	}

	public Integer getOperationType(){
		return operationType;
	}
	
	public void setOperationType(Integer type){
		this.operationType = type;
	}
	
	public String getRevision() {
		return revision==null ? "" : revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public ResourceVO clone(){
		ResourceVO clone = new ResourceVO();
		clone.setAbsolutePath(getAbsolutePath());
		clone.setId(getId());
		clone.setName(getName());
		clone.setOperationType(getOperationType());
		clone.setRelativePath(getRelativePath());
		return clone;
	}
}
