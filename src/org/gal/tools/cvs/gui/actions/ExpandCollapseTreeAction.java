package org.gal.tools.cvs.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import org.gal.tools.cvs.gui.model.HistoryTreeModel;
import org.gal.tools.utils.Utils;

public class ExpandCollapseTreeAction implements ActionListener{

	public static final String EXPAND = "expand";
	private JTree tree;
	private int level = 0;
	private static final int MAX_LEVEL = 20;
	private static final int MIN_LEVEL = 0;
	public static final String COLLAPSE = "collapse";
	private HistoryTreeModel model;
	
	
	public ExpandCollapseTreeAction(JTree tree){
		this.tree = tree;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		TreeModel tm = tree.getModel();
		if(!(tm instanceof HistoryTreeModel)){
			return;
			//throw new RuntimeException("ExpandCollapseTreeAction can be used only with model of the instance of HistoryTreeModel.");
		}
		if(model!= tm){
			model = ((HistoryTreeModel)tm);
			level = model.getDefaultExpandLevel();
		}
		
		

		if(EXPAND.equals(e.getActionCommand())){
			
				Utils.expandAllNodes(tree);
			
		}else if(COLLAPSE.equals(e.getActionCommand())){
			
				Utils.collapseAllNodes(tree);
				//Utils.expandTreeToLevel(tree, model.getDefaultExpandLevel());
			
		}
		
		
	}

	public int getLevel() {
		return level;
	}
}
