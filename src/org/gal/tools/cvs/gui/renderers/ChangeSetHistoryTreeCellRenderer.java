package org.gal.tools.cvs.gui.renderers;

import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

import org.gal.tools.cvs.controllers.CVSHistoryController;
import org.gal.tools.cvs.gui.model.HistoryTreeModel;
import org.gal.tools.cvs.model.ChangeSetVO;
import org.gal.tools.cvs.model.CommitDateTime;
import org.gal.tools.cvs.model.FileDirectory;
import org.gal.tools.cvs.model.OperationType;
import org.gal.tools.cvs.model.ResourceVO;
import org.gal.tools.cvs.model.WorkspaceVO;
import org.gal.tools.utils.Icons;

public class ChangeSetHistoryTreeCellRenderer extends DefaultTreeCellRenderer {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String highlightTerm;
	
	private boolean m_useRegexp = false;
	
	public ChangeSetHistoryTreeCellRenderer(){
	}
	
	

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		TreeModel tm = tree.getModel();
		if(!(tm instanceof HistoryTreeModel)) return this;
		String model = ((HistoryTreeModel)tm).getCurrentModel();
		String text = "", template = "<html>${text}</html>";
		if ((value instanceof ChangeSetVO)) {
			ChangeSetVO ch = ((ChangeSetVO) value);
			text = ch.getName();
			template = "<html>${text}&nbsp;";
			
			String date;
			
			if(ch.getParent() instanceof CommitDateTime){
				date = ch.getCommitDateTime().getFormattedCommitTime();
			}else {
				date = ch.getCommitDateTime().getFormattedCommitDateTime();
			}
			if(!date.isEmpty()){
				template +="<span style='color:gray;text-decoration:italic;margin-left:8px'>("+date+")</span>";	
			}
			
			
			
			
			if(!model.equals(HistoryTreeModel.WC_MODEL) && !model.equals(HistoryTreeModel.J_MODEL)){
				template +="&nbsp;&nbsp;<span style='color:#5F4C0B;margin-left:8px'>["+ch.getWorkspace().getName()+"]</span>";	
			}
			if(ch.getParent() instanceof ResourceVO){
				ResourceVO res = ((ResourceVO)ch.getParent());
				ResourceVO res2;
				if(model.equals(HistoryTreeModel.R_MODEL)){
					res2 = res;
				}else {
					res2 = ch.getResourceByPath(res.getRelativePath());	
				}
				
				String rev = res2!=null ? res2.getRevision() : "";
				template +="&nbsp;&nbsp;<span style='color:gray;margin-left:8px'>"+rev+"</span>";
			}
			
			template +="</html>";
			
			setIcon(Icons.CHANGESET_ICON);	
			
			
		}
		if ((value instanceof WorkspaceVO)) {
			WorkspaceVO ch = ((WorkspaceVO) value);
			text = ch.getName();
			template = "<html><span style='font-family:arial;font-weight:bold;font-size:13pt;'>${text}</span></html>";
			ImageIcon icon = Icons.WORKSPACE_ICON;
			if(CVSHistoryController.getCurrentWorkspace()!=null && ch.getId()==CVSHistoryController.getCurrentWorkspace().getId()){
				icon = Icons.CURRENT_WORKSPACE_ICON;
			}
			setIcon(icon);	
			
		}
		
		if ((value instanceof CommitDateTime)) {
			CommitDateTime ch = ((CommitDateTime) value);
			String name = ch.getName();
			if(name.isEmpty()){
				name = "No date";
			}
			template = "<html><span style='font-family:arial;font-weight:bold;color: black;'>${text}</span></html>";
			text = name;
			
			setIcon(Icons.DATE_ICON);	
			
		}
		
		if ((value instanceof FileDirectory)) {
			FileDirectory ch = ((FileDirectory) value);
			text = ch.getName();
			if(ch.isRoot()){
				if(expanded){
					setIcon(Icons.OPEN_PROJECT_ICON);	
				}
				if(!expanded){
					setIcon(Icons.CLOSE_PROJECT_ICON);	
				}
			}else{
				if(expanded){
					setIcon(Icons.OPEN_DIR_ICON);	
				}
				if(!expanded){
					setIcon(Icons.CLOSE_DIR_ICON);	
				}	
			}
			
		}
		
		if (value instanceof ResourceVO) {
			if (!(((ResourceVO) value).getParent() instanceof FileDirectory)){
				text = ((ResourceVO) value).getRelativePath();
			}else{
				text = ((ResourceVO) value).getName();	
			}
			ImageIcon ico = null;
			int opType = ((ResourceVO) value).getOperationType();
			String revision = ((ResourceVO) value).getRevision();
			
			template = "<html><span style='font-family:monospace;color: black;'>${text}</span>&nbsp;&nbsp;<span style='font-family:arial;color: #5F4C0B;'>"+revision+"</span></html>";
			if(opType==OperationType.ADD){
				ico = Icons.FILE_ADD_ICON;
			}else if(opType==OperationType.UPDATE){
				ico = Icons.FILE_EDIT_ICON;
			}else if(opType==OperationType.DELETE){
				ico = Icons.FILE_REMOVE_ICON;
			}else if(opType==OperationType.VIEW){
				ico = Icons.FILE_ICON;
				template = "<html><span style='font-family:arial;font-weight:bold;color: black;'>${text}</span></html>";
			}
			
			setIcon(ico);	
		}
		
		if(highlightTerm!=null && !highlightTerm.isEmpty()){
			text = replaceWord(text);
			text = template.replace("${text}", text);
		}else{
			text = template.replace("${text}", text);
		}

		
		setText(text);
		setToolTipText(getText());
	    return this;
	  }
	
	private String replaceWord(String text){
		String p_pattern = "(?i).*("+highlightTerm+").*";
		if(m_useRegexp){
			p_pattern = highlightTerm;
		}
		Pattern p = Pattern.compile(p_pattern);
		Matcher m = p.matcher(text);
		String res = null;
		if(m.find()){
			String word =  (m_useRegexp) ? m.group() : m.group(1);
			res = text.replace(word, "<span style='background:yellow'>"+word+"</span>");
		}else{
			res = text;
		}
		return res;
	}
	
	
	
	public void setHighlightTerm(String term, boolean useRegexp){
		if(!useRegexp){
			highlightTerm = term.replace("*", "");	
		}
		highlightTerm = term;
		m_useRegexp = useRegexp;
		
		
	}
	public void removeHighlights(){
		highlightTerm = null;
	}
}
