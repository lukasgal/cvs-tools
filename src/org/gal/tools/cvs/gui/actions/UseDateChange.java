package org.gal.tools.cvs.gui.actions;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gal.tools.cvs.controllers.CVSHistoryController;

public class UseDateChange implements ChangeListener {
	
	CVSHistoryController controller;
	
	public UseDateChange(CVSHistoryController controller){
		this.controller = controller;
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		
		if (e.getSource() instanceof JCheckBox){
			JCheckBox chb = (JCheckBox) e.getSource();
			this.controller.setFilterByDate(chb.isSelected());
			controller.setEnabledDateRange(chb.isSelected());
			controller.filterTree();
		}	

	}

}
