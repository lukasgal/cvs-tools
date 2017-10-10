package org.gal.tools.cvs.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import org.gal.tools.cvs.controllers.CVSHistoryController;

public class ShowResourcesAsListAction implements ActionListener{
	
	private CVSHistoryController controller;
	
	public ShowResourcesAsListAction(CVSHistoryController controller){
		this.controller = controller;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.setShowResourcesAsList(((JToggleButton)e.getSource()).isSelected());
		controller.refresh();	
	}
}
