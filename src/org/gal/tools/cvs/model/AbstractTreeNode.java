package org.gal.tools.cvs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public abstract class AbstractTreeNode {
	
	protected ArrayList<AbstractTreeNode> rows = new ArrayList<>();
	
	protected Integer id;
	
	protected String name;

	protected AbstractTreeNode parent;

	public String getName() {
		return name;
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public ArrayList<AbstractTreeNode> getRows(){
		return rows;	
	}
	
	public void addRow(AbstractTreeNode row){
		if(row!=null){
			rows.add(row);
			row.setParent(this);
		}
	}
	
	public AbstractTreeNode getRow(AbstractTreeNode row) {
		
		for(AbstractTreeNode r : rows){
			if(r.getId()==row.getId()) return r;
		}
		return null;
	}

	
	public AbstractTreeNode getRow(int i) {
		return rows.get(i);
	}	

	
	public AbstractTreeNode getParent() {

		return this.parent;
	}

	public void setParent(AbstractTreeNode parent) {
		this.parent = parent;
		
	}

		
	public void setRows(ArrayList<AbstractTreeNode> rows) {
		if(rows==null){
			this.rows = new ArrayList<>();
			return;
		}
		for(AbstractTreeNode row : rows){
			row.setParent(this);
		}
		this.rows = rows;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
}