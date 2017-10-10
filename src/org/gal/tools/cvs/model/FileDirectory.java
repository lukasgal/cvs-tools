package org.gal.tools.cvs.model;

public class FileDirectory extends AbstractTreeNode {

	private boolean root;
	
	public FileDirectory(){
		
	}
	
	public FileDirectory(String name){
		this.name=name;
	}
			
	@Override
	public String toString(){
		return this.name;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}
	
	
}
