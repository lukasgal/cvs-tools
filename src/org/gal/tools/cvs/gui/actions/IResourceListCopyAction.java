package org.gal.tools.cvs.gui.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JList;

import org.eclipse.core.resources.IResource;

public class IResourceListCopyAction extends AbstractAction{
    
	private static final long serialVersionUID = 1L;

	public static void fixCopyFor(JList list) {
        list.getActionMap().put("copy", new IResourceListCopyAction());
    }
    
    public IResourceListCopyAction() {
        super("copy");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        copyListSelectionToClipboard((JList) e.getSource());
    }
    
    private void copyListSelectionToClipboard(JList<IResource> list) {
	    List<IResource> values = list.getSelectedValuesList();
	    if(values==null){
	    	return;
	    }
        StringSelection selection = new StringSelection(getFormattedValues(values));;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.getSystemClipboard().setContents(selection, selection);
        if (toolkit.getSystemSelection() != null) {
            toolkit.getSystemSelection().setContents(selection, selection);
        }
    }
    
    private String getFormattedValues(List<IResource> values){
		StringBuffer str = new StringBuffer();
		int i = 0;
		for(IResource res : values){
			str.append(res.getFullPath());
			if(i < values.size()-1){
				str.append("\n");
			}
			i++;
		}
		return str.toString();
	}
}
