package org.gal.tools.cvs.model;

import java.util.ArrayList;
import java.util.Date;

public class TreeModelCommitDateTime extends AbstractTreeModel{
	
	
	public TreeModelCommitDateTime(AbstractTreeNode root){
		this.root = root;
		generate();
	}
	

	public static AbstractTreeNode getRowByValue(AbstractTreeNode root, String date){
		for(AbstractTreeNode d : root.getRows()){
			if(((CommitDateTime)d).getFormattedCommitDate().compareTo(date)==0){
				return d;
			}
		}
		return null;
	}
	
	
	
	@SuppressWarnings("deprecation")
	protected void generate(){
		ArrayList<AbstractTreeNode> data = root.getRows();
		if(data==null) return;
		
		String currValue;
		CommitDateTime newCDT = null;
		
		AbstractTreeNode newRoot = new RootTreeNode("DTRoot", new ArrayList<AbstractTreeNode>());
		for(AbstractTreeNode chs : root.getRows()){
			AbstractTreeNode cdt =  ((ChangeSet)chs).getCommitDateTime();
			currValue = ((CommitDateTime) cdt).getFormattedCommitDate();
			newCDT = (CommitDateTime) getRowByValue(newRoot, currValue);
			
			if(newCDT==null){
				long dt;
				if(currValue==null || currValue.isEmpty()){
					dt = 0L; 
				} else{
					dt = new Date(currValue).getTime();	
				}
				
				newCDT = new CommitDateTime(dt);
				newCDT.addRow(chs);
				newRoot.addRow(newCDT);
				newCDT.setParent(newRoot);
			}else{
				newCDT.addRow(chs);
				chs.setParent(newCDT);
			}
		}
		root.setRows(newRoot.getRows());
	}
}