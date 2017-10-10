package org.gal.tools.cvs.model;

import java.lang.reflect.InvocationTargetException;

import org.gal.tools.utils.Logger;

public abstract class AbstractTreeModel {
	protected AbstractTreeNode root;
	
	public AbstractTreeNode getRoot(){
		return this.root;
	}
	
	public void setChildModel(String modelClass){
		for(AbstractTreeNode node : root.getRows()){
			try {
				AbstractTreeModel child = (AbstractTreeModel) Class.forName(modelClass).getConstructor(AbstractTreeNode.class).newInstance(node);
				node.setRows(child.getRoot().getRows());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException
					| ClassNotFoundException e) {
				Logger.addStackTrace(e);
			}
		}
	}
	
	abstract protected void generate();
}
