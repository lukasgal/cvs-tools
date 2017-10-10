package org.gal.tools.cvs.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import org.gal.tools.cvs.controllers.CVSHistoryController;

public class ShowOnlyCurrentWSAction implements ActionListener{
	
	private CVSHistoryController controller;
	
	public ShowOnlyCurrentWSAction(CVSHistoryController controller){
		this.controller = controller;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.setShowOnlyCurrentWS(((JToggleButton)e.getSource()).isSelected());
		controller.filterTree();
		
		
	}

}
