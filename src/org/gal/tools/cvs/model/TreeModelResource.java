package org.gal.tools.cvs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeModelResource extends AbstractTreeModel {
	

	public TreeModelResource(AbstractTreeNode root) {
		this.root = root;
		generate();
	}
	

	protected void generate() {
		ArrayList<AbstractTreeNode> data = root.getRows();
		AbstractTreeNode newRoot = new RootTreeNode("WSRoot", new ArrayList<AbstractTreeNode>()){
			public AbstractTreeNode getRow(AbstractTreeNode row){
				for(AbstractTreeNode r : rows){
					if((r instanceof ResourceVO) && ((ResourceVO)r).getRelativePath().contentEquals(((ResourceVO)row).getRelativePath())) return r;
				}
				return null;
			}
		};
		if(data==null) return;
		AbstractTreeNode r = null;
		for(AbstractTreeNode chs : data){
			for(AbstractTreeNode resource :  ((ChangeSet)chs).getRows()){
				
				ChangeSetVO cloneChangeSet = ((ChangeSetVO)chs).clone();
				if(newRoot.getRow(resource)!=null){
					r = newRoot.getRow(resource);
					r.addRow(cloneChangeSet);
				}else{
					ResourceVO cloneResource = ((ResourceVO)resource).clone();
					cloneResource.setOperationType(OperationType.VIEW);
					newRoot.addRow(cloneResource);
					cloneResource.addRow(cloneChangeSet);
				}	
			}
			
			
		}
		root.setRows(newRoot.getRows());
	}

	public static void printTree(AbstractTreeNode node, int level) {
		for (AbstractTreeNode row : node.getRows()) {
			for (int i = 0; i < level; i++) {
				System.out.print("\t");
			}
			System.out.print(row.getName());
			System.out.println();

			if (row.getRows() != null) {
				printTree(row, level + 1);
			}
		}
	}

	public static void main(String[] args) {
		RootTreeNode root = new RootTreeNode("Root", new ArrayList<AbstractTreeNode>());

		ResourceVO res = new ResourceVO();
		res.setAbsolutePath("c:/Develmo/poosd/de.usu.infoboard/web/js/infoObjects/DocumentList.js");
		res.setRelativePath("/de.usu.infoboard/web/js/infoObjects/DocumentList.js");
		res.setName("DocumentList.js");
		root.addRow(res);

		res = new ResourceVO();
		res.setAbsolutePath("c:/Develmo/poosd/de.usu.infoboard/web/js/infoObjects/SingletonPipni.js");
		res.setRelativePath("/de.usu.infoboard/web/js/infoObjects/SingletonPipni.js");
		res.setName("SingletonPipni.js");
		root.addRow(res);

		res = new ResourceVO();
		res.setAbsolutePath("c:/Develmo/poosd/de.usu.infoboard/web/js/DL.js");
		res.setRelativePath("/de.usu.infoboard/web/js/DL.js");
		res.setName("DL.js");
		root.addRow(res);

		res = new ResourceVO();
		res.setAbsolutePath("c:/Develmo/poosd/de.usu.infoboard/conf/script.sc.xml");
		res.setRelativePath("/de.usu.infoboard/conf/script.sc.xml");
		res.setName("script.sc.xml");
		root.addRow(res);

		TreeModelResource tmr = new TreeModelResource(root);
		
		// TreeNodeInterface r = tmr.createPath(res);
		printTree(tmr.getRoot(), 0);
	}

}
