package org.gal.tools.cvs.gui.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTree;

import org.gal.tools.cvs.controllers.CVSHistoryController;
import org.gal.tools.cvs.controllers.JIRACommentCreator;
import org.gal.tools.cvs.model.ChangeSet;
import org.gal.tools.cvs.model.ChangeSetVO;
import org.gal.tools.utils.Config;
import org.gal.tools.utils.Logger;

public class HistoryTreeKeyListener implements KeyListener{

	private CVSHistoryController controller;
	
	public HistoryTreeKeyListener(CVSHistoryController controller){
		this.controller=controller;
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_F5){
			controller.refresh();
		}
		if(e.getKeyCode()==KeyEvent.VK_ENTER){
			if(e.getSource() instanceof JTree){
				controller.openFileFromTree(((JTree)e.getSource()));
			}
		}
		if(e.getKeyCode()==KeyEvent.VK_F3){
			controller.addJIRAComment();
		}
		
		if(e.getKeyCode()==KeyEvent.VK_F6){
			controller.editWorkspaceName();
		}
		
		if(e.getKeyCode()==KeyEvent.VK_F11){
			Config.getInstance().load();
			controller.refresh();
		}
	}

}
