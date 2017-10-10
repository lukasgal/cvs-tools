package org.gal.tools.cvs.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTree;

import org.gal.tools.utils.Utils;

public class ExpandTreeAction implements ActionListener{

	private JTree tree;
	
	public ExpandTreeAction(JTree tree){
		this.tree = tree;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Utils.expandAllNodes(tree);
	}

	
	
}
