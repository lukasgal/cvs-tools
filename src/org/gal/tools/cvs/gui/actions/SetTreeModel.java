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
		if(HistoryTreeModel.J_MODEL.equals(e.getActionCommand())){
			controller.setFilterByDate(false);
			controller.setVisibleDateRange(false);
			//controller.setVisibleLimit(true);			
			//controller.getWindow().getSpLimit().setValue(50);
		}else{
			controller.setFilterByDate(true);
			controller.setVisibleDateRange(true);
			//controller.setVisibleLimit(false);		
			//controller.getWindow().getSpLimit().setValue(CVSHistoryController.DEFAULT_REC_LIMIT);
		}
		controller.setTreeModel(e.getActionCommand());
		controller.refresh();
	}
}