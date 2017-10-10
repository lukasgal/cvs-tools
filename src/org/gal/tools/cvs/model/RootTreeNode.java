package org.gal.tools.cvs.model;

import java.util.ArrayList;

public class RootTreeNode extends AbstractTreeNode{

	public RootTreeNode(String name, ArrayList<AbstractTreeNode> rows){
		this.name = name;
		this.rows = rows;
	}
}
