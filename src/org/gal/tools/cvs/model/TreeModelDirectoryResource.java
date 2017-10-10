package org.gal.tools.cvs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeModelDirectoryResource extends AbstractTreeModel {

	public TreeModelDirectoryResource(AbstractTreeNode root) {
		this.root = root;
		generate();
	}
	
	public AbstractTreeNode createHierarchyDir(AbstractTreeNode chs) {
		
		Map<Integer, String> map = new HashMap<>();
		Map<Integer, AbstractTreeNode> mapPack = new HashMap<Integer, AbstractTreeNode>();
		AbstractTreeNode parentPackage = new RootTreeNode("Root", new ArrayList<AbstractTreeNode>());
		AbstractTreeNode p = parentPackage;

		mapPack.put(Integer.valueOf(0), p);
		for (int k = 0; k < chs.getRows().size(); k++) {
			String[] parsedPath = null;
			if(chs.getRow(k) instanceof ResourceVO){
				parsedPath = ((ResourceVO) chs.getRow(k)).getRelativePath().split("/");	
			}
			if(chs.getRow(k) instanceof FileDirectory){
				parsedPath = ((FileDirectory) chs.getRow(k)).getName().split("/");	
			}

			boolean newBranch = false;
			if(parsedPath==null) continue;
			for (int i = 1; i < parsedPath.length; i++) {
				if(parsedPath[i].isEmpty()) continue;
				newBranch |= !parsedPath[i].equals(map.get(Integer.valueOf(i)));
				if (newBranch) {
					if (mapPack.size() > 1) {
						parentPackage = (AbstractTreeNode) mapPack.get(Integer.valueOf(i - 1));
					} else {
						parentPackage = p;
					}

					AbstractTreeNode newPack;
					if (parsedPath.length - 1 != i) {
						newPack = new FileDirectory(parsedPath[i]);
						if(i==1){
							((FileDirectory)newPack).setRoot(true);	
						}else{
							((FileDirectory)newPack).setRoot(false);
						}
					} else {
						newPack = (ResourceVO) chs.getRow(k);
						((ResourceVO)newPack).setName(((ResourceVO)newPack).getName());
					}
					//newPack.setParent(parentPackage);
					parentPackage.addRow(newPack);
					mapPack.put(Integer.valueOf(i), newPack);
					map.put(Integer.valueOf(i), parsedPath[i]);
				}
			}
		}
		chs.setRows(p.getRows());
		return p;
	}

	protected void generate() {
		ArrayList<AbstractTreeNode> data = root.getRows();
		if (data == null)
			return;

		if(root instanceof ChangeSetVO){
			createHierarchyDir(root);
			return;
		}
		
		for (AbstractTreeNode ws : data) {
			createHierarchyDir(ws);
		}
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

		TreeModelDirectoryResource tmr = new TreeModelDirectoryResource(root);
		
		// TreeNodeInterface r = tmr.createPath(res);
		printTree(tmr.getRoot(), 0);
	}

}
