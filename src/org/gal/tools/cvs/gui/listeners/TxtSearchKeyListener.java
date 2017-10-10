package org.gal.tools.cvs.gui.listeners;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import org.gal.tools.cvs.controllers.CVSHistoryController;

public class TxtSearchKeyListener implements KeyListener {
	
	public CVSHistoryController controller;
	
	public TxtSearchKeyListener(CVSHistoryController controller){
		this.controller = controller;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {

		// ((JTextField)e.getSource()).setBackground(Color.white);

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			controller.filterTree();
		} else {
			if(e.getSource() instanceof JTextField){
				JTextField field = ((JTextField) e.getSource());
				if (field.getBackground() == Color.red) {
					field.setBackground(Color.white);
				}	
			}
		}
	}

}
