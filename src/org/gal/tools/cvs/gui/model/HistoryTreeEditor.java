package org.gal.tools.cvs.gui.model;

import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.gal.tools.cvs.model.AbstractTreeNode;
import org.gal.tools.cvs.model.WorkspaceVO;

public class HistoryTreeEditor extends DefaultTreeCellEditor {

	public HistoryTreeEditor(JTree arg0, DefaultTreeCellRenderer arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
    public Object getCellEditorValue() {
        Object o = super.getCellEditorValue();
        if(o instanceof AbstractTreeNode){
        	return ((AbstractTreeNode) o).getName();
        }
        return o;
        //return new Resource(value);
    }

    @Override
    public boolean isCellEditable(EventObject e) {
    	boolean editable = false;
        JTree tree  = (e!=null && (e.getSource() instanceof JTree)) ? ((JTree)e.getSource()) : null;
        if(tree==null) return false;
        Object o = tree.getLastSelectedPathComponent();
        if(o instanceof WorkspaceVO) return true;
        return editable;
    }


}
