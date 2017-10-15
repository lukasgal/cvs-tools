package org.gal.tools.cvs.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import org.gal.tools.cvs.controllers.CVSHistoryController;

public class SearchRegexpAction implements ActionListener {

	CVSHistoryController controller;
	
	public SearchRegexpAction(CVSHistoryController controller){
		this.controller = controller;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JCheckBox){
			JCheckBox chb = (JCheckBox) e.getSource();
			this.controller.setfilterByRegexp(chb.isSelected());
			if(controller.getSearchText().isEmpty()) return;
			controller.filterTree();
		}

	}

}
