package org.gal.tools.cvs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class TreeModelProjects extends AbstractTreeModel {

		
	public TreeModelProjects(AbstractTreeNode root) {
		this.root = root;
		generate();
	}
	
	public AbstractTreeNode getRowByName(AbstractTreeNode root, String name){
		for(AbstractTreeNode d : root.getRows()){
			if(name.contentEquals(d.getName())){
				return d;
			}
		}
		return null;
	}

	
	public void createHierarchyFromChsRes(AbstractTreeNode parentPackage, AbstractTreeNode chs) {
		for (int k = 0; k < chs.getRows().size(); k++) {
			AbstractTreeNode p = parentPackage;
			String[] parsedPath = null;
			if(chs.getRow(k) instanceof ResourceVO){
				parsedPath = ((ResourceVO) chs.getRow(k)).getRelativePath().split("/");	
			}
			if(chs.getRow(k) instanceof FileDirectory){
				parsedPath = ((FileDirectory) chs.getRow(k)).getName().split("/");	
			}
			AbstractTreeNode node;
			boolean newBranch = false;
			if(parsedPath==null) continue;
			for (int i = 1; i < parsedPath.length; i++) {
				if(parsedPath[i].isEmpty()) continue;
				
				newBranch |= (node = getRowByName(p, parsedPath[i]))==null;
				if (newBranch) {
					AbstractTreeNode newPack;
					if (parsedPath.length - 1 != i) {
						newPack = new FileDirectory(parsedPath[i]);
						if(i==1){
							((FileDirectory)newPack).setRoot(true);	
						}else{
							((FileDirectory)newPack).setRoot(false);
						}
						
						
					} else {
						newPack = ((ResourceVO) chs.getRow(k));
						
						ResourceVO newRes = new ResourceVO();
						newRes.setOperationType(OperationType.VIEW);
						newRes.setRevision(null);
						newRes.setName(newPack.getName());
						newRes.setRelativePath(((ResourceVO)newPack).getRelativePath());
						newRes.addRow(((ChangeSetVO)chs).clone());
						newPack = newRes;
					}
					p.addRow(newPack);
					p = newPack;
				}else{
					if(node instanceof ResourceVO){
						node.addRow(((ChangeSetVO)chs).clone());
					}
					p = node;
				}
			}
		}
	}

	protected void generate() {
		ArrayList<AbstractTreeNode> data = root.getRows();
		if (data == null)
			return;
		AbstractTreeNode newRoot = new RootTreeNode("ProjectRoot", new ArrayList<AbstractTreeNode>());
		for (AbstractTreeNode chs : data) {
			createHierarchyFromChsRes(newRoot, chs);
		}
		this.root = newRoot;
	}
}
