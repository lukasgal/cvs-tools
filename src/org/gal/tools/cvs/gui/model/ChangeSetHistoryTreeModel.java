package org.gal.tools.cvs.gui.model;

import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gal.tools.cvs.model.AbstractTreeNode;
import org.gal.tools.cvs.model.ChangeSet;
import org.gal.tools.cvs.model.ChangeSetVO;
import org.gal.tools.cvs.model.ResourceVO;

public class ChangeSetHistoryTreeModel implements TreeModel {

	
	private TreeStructure data;
	
	public class TreeStructure{
		List<AbstractTreeNode> children;
		public TreeStructure(List<AbstractTreeNode> data){
			children = data;
		}
		public String getName(){
			return "Root";
		}
		public List<AbstractTreeNode> getChildren(){
			return children;
		}
		
		public void setChildren(List<AbstractTreeNode> data){
			this.children = data;
		}
	}
	
	public ChangeSetHistoryTreeModel(List<AbstractTreeNode> data){
		this.data = new TreeStructure(data);
	}
	
	public void setData(List<AbstractTreeNode> data){
		this.data.setChildren(data);
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getChild(Object obj, int i) {
		if(obj instanceof TreeStructure){
			return ((TreeStructure) obj).getChildren().get(i);
		}
		else if(obj instanceof ChangeSetVO){
			return ((ChangeSetVO) obj).getRows().get(i);
		}
		return null;
	}

	@Override
	public int getChildCount(Object obj) {
		if(obj instanceof TreeStructure){
			return ((TreeStructure) obj).getChildren().size();
		}else if(obj instanceof ChangeSetVO){
			return ((ChangeSetVO) obj).getRows().size();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object obj, Object arg1) {
		if(obj instanceof TreeStructure){
			((TreeStructure) obj).getChildren().indexOf(arg1);
		}else if(obj instanceof ChangeSetVO){
			return ((ChangeSetVO) obj).getRows().indexOf(arg1);
		}
		return 0;
	}

	@Override
	public Object getRoot() {

		return this.data;
	}

	@Override
	public boolean isLeaf(Object arg0) {
		boolean isLeaf = arg0 instanceof ResourceVO;
		return isLeaf;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}
