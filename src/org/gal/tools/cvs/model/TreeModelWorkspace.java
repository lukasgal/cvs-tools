package org.gal.tools.cvs.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
/**
 * Generate tree Root-> Workspace-> Commit date -> Change set -> Resource
 * @author oem
 *
 */
public class TreeModelWorkspace extends AbstractTreeModel{
	
	public TreeModelWorkspace(AbstractTreeNode root){
		this.root = root;
		generate();
	}

	protected void generate(){
		ArrayList<AbstractTreeNode> data = root.getRows();
		AbstractTreeNode newRoot = new RootTreeNode("WSRoot", new ArrayList<AbstractTreeNode>());
		if(data==null) return;

		for(AbstractTreeNode chs : data){
			AbstractTreeNode ws = ((ChangeSet)chs).getWorkspace();
			
			if(newRoot.getRow(ws)!=null){
				ws = newRoot.getRow(ws);
				ws.addRow(chs);
			}else{
				ws = ((WorkspaceVO)ws).clone();
				newRoot.addRow(ws);
				ws.setParent(newRoot);
				ws.addRow(chs);
			}
		}
		root.setRows(newRoot.getRows());
	}
}
