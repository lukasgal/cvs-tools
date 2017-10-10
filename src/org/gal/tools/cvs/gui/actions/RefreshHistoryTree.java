package org.gal.tools.cvs.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.gal.tools.cvs.controllers.CVSHistoryController;

public class RefreshHistoryTree implements ActionListener {
	
	CVSHistoryController controller;
	
	public RefreshHistoryTree(CVSHistoryController controller){
		this.controller = controller;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.refresh();
		
	}
}