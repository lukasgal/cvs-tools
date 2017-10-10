package org.gal.tools.cvs.gui.listeners;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import org.gal.tools.cvs.controllers.CVSHistoryController;
import org.gal.tools.cvs.model.WorkspaceVO;

public class HistoryTreeCellEditorListener implements CellEditorListener{

	CVSHistoryController m_controller;
	
	public HistoryTreeCellEditorListener(CVSHistoryController p_controller) {
		m_controller = p_controller;
	}
	
	@Override
	public void editingCanceled(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editingStopped(ChangeEvent arg0) {
		Object source = arg0.getSource();
		if(source instanceof WorkspaceVO){
			m_controller.changeWorkspaceName((WorkspaceVO) source);
		}
		
		
	}

}
