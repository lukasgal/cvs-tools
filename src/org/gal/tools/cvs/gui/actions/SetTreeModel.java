package org.gal.tools.cvs.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.gal.tools.cvs.controllers.CVSHistoryController;
import org.gal.tools.cvs.gui.model.HistoryTreeModel;

public class SetTreeModel implements ActionListener {
	
	CVSHistoryController controller;
	
	public SetTreeModel(CVSHistoryController controller){
		this.controller = controller;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		boolean use = !HistoryTreeModel.J_MODEL.equals(e.getActionCommand());
		controller.setUseDateRange(use);
		controller.setTreeModel(e.getActionCommand());
		controller.refresh();
	}
}