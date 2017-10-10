package org.gal.tools.cvs.gui.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTree;

import org.gal.tools.cvs.controllers.CVSHistoryController;

public class HistoryTreeMouseListener implements MouseListener{

	private CVSHistoryController controller;
	
	public HistoryTreeMouseListener(CVSHistoryController controller){
		this.controller = controller;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e){
		if (e.getClickCount() == 2) {
			controller.openFileFromTree(((JTree)e.getSource()));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
