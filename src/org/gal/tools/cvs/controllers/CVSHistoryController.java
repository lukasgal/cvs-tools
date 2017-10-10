package org.gal.tools.cvs.controllers;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.gal.tools.cvs.gui.CVSHistoryWindow;
import org.gal.tools.cvs.gui.actions.ExpandCollapseTreeAction;
import org.gal.tools.cvs.gui.actions.RefreshHistoryTree;
import org.gal.tools.cvs.gui.actions.SetTreeModel;
import org.gal.tools.cvs.gui.actions.ShowOnlyCurrentWSAction;
import org.gal.tools.cvs.gui.actions.ShowResourcesAsListAction;
import org.gal.tools.cvs.gui.listeners.HistoryTreeKeyListener;
import org.gal.tools.cvs.gui.listeners.HistoryTreeMouseListener;
import org.gal.tools.cvs.gui.listeners.TxtSearchKeyListener;
import org.gal.tools.cvs.gui.model.HistoryTreeModel;
import org.gal.tools.cvs.gui.renderers.ChangeSetHistoryTreeCellRenderer;
import org.gal.tools.cvs.model.AbstractTreeNode;
import org.gal.tools.cvs.model.ChangeSet;
import org.gal.tools.cvs.model.ChangeSetDAO;
import org.gal.tools.cvs.model.ChangeSetDAOImpl;
import org.gal.tools.cvs.model.ChangeSetVO;
import org.gal.tools.cvs.model.ResourceVO;
import org.gal.tools.cvs.model.RootTreeNode;
import org.gal.tools.cvs.model.WorkspaceDAO;
import org.gal.tools.cvs.model.WorkspaceDAOImpl;
import org.gal.tools.cvs.model.WorkspaceVO;
import org.gal.tools.model.AbstractController;
import org.gal.tools.utils.Config;
import org.gal.tools.utils.DBUtils;
import org.gal.tools.utils.Icons;
import org.gal.tools.utils.Utils;
import org.gal.tools.utils.WorkspaceUtils;


public class CVSHistoryController extends Observable implements AbstractController{

	public static final String ID = "cvshistory";

	private Config m_config;
	
	private List<AbstractTreeNode> records;
	
	private CVSHistoryWindow window;

	private boolean filtered = false;
	
	ChangeSetDAO chsDao;
	
	private String lastTreeState;
	
	boolean showOnlyCurrenWS = true;
	private String filterText = null; 
	private static WorkspaceVO workspace;
	
	private String currentTreeModel;
	
	private boolean showResourcesAsList = true;
	
	public final static Integer DEFAULT_REC_LIMIT = 1000000;
	
	private Integer limitRecords = DEFAULT_REC_LIMIT;
	
	private boolean filterByDate = true;
	
	public CVSHistoryController(WorkspaceVO ws){
		DBUtils.updateDatabaseSchema();
		workspace = ws;
		this.window = new CVSHistoryWindow();
		currentTreeModel = HistoryTreeModel.WC_MODEL;
		initWindow();
		chsDao = new ChangeSetDAOImpl();
		filterTree();
	}
	
	public void refresh(){
		records.clear();
		if(filterText==null){
			fetch();	
		}else{
			filter(filterText, isfilterByRegexp());
		}
		setTreeModel(currentTreeModel);
	}
	
	public void fetch(){
		
		Integer wsId = null;
		if(showOnlyCurrenWS && workspace!=null){
			wsId = workspace.getId();
		}
		records = chsDao.getAllChangeSets(wsId, getLimitRecords(), getDateFrom(), getDateTo());
		filtered = false;
		filterText = null;
	}
	
	public void filter(String str, boolean useRegexp){

		filterText = str;
		Integer wsId = null;
		if(showOnlyCurrenWS && workspace!=null){
			wsId = workspace.getId();
		}
		records = chsDao.filterByName(str, str, wsId, getDateFrom(), getDateTo(), useRegexp);
		filtered = true;
	}
	
	public List<AbstractTreeNode> getData(){
		List<AbstractTreeNode> copy = new ArrayList<> (records.size());
		for(AbstractTreeNode rec : records){
			copy.add(((ChangeSetVO)rec).clone());
		}
		
		return copy;
	}
	
	public void createTree(){
		
	}
	
	
	public String getLastTreeState() {
		return lastTreeState;
	}

	public void setLastTreeState(JTree tree) {
		this.lastTreeState = Utils.getExpansionState(tree);
	}

	public boolean isFiltered(){
		return filtered;
	}
	
	private void fireChanged(Integer changeCode){
		setChanged();
		notifyObservers(changeCode);
	}

	public void setShowOnlyCurrentWS(boolean show){
		this.showOnlyCurrenWS = show;
	}
	
	public boolean showOnlyCurrentWS() {
		
		return showOnlyCurrenWS;
	}
	
	public Long getDateFrom(){
		if(!filterByDate) return null;
		Date from = (Date) window.getSpShowFrom().getValue();
		from = Utils.getStartOfDay(from);
		return from.getTime();
	}

	public Long getDateTo(){
		if(!filterByDate) return null;
		Date to = (Date) window.getSpShowTo().getValue();
		to = Utils.getEndOfDay(to);
		return to.getTime();
	}
	
	public static void openFileFromTree(JTree tree){
		Object obj = tree.getLastSelectedPathComponent();
		if(obj instanceof ResourceVO){
			WorkspaceUtils.openFile(((ResourceVO) obj).getRelativePath());
		}	
	}
	
	public void setTreeModel(String model){
		if(currentTreeModel!=model){
			this.currentTreeModel = model;
		}
		
		HistoryTreeModel treeModel = new HistoryTreeModel(new RootTreeNode("Root",(ArrayList<AbstractTreeNode>) this.getData()), currentTreeModel,showResourcesAsList);
    	window.getTree().setModel(treeModel);
    	Utils.expandTreeToLevel(window.getTree(),treeModel.getDefaultExpandLevel());
    }
	
	public static WorkspaceVO getCurrentWorkspace(){
		return workspace;
	}
	
	
	public void setShowResourcesAsList(boolean b){
		this.showResourcesAsList = b;
		if(HistoryTreeModel.D_MODEL.equals(currentTreeModel) && !b){
			window.getBtnWDCModelView().doClick();
		}
		window.getBtnDirectoryModel().setEnabled(b);
	}
	public CVSHistoryWindow getWindow(){
		return this.window;
	}
	public void filterTree(){
		JTextField field = window.getSearch();
		
    	String text = field.getText();
    	
		if(text.trim().isEmpty()){
			fetch();
			setTreeModel(currentTreeModel);
			
			((ChangeSetHistoryTreeCellRenderer)window.getTree().getCellRenderer()).removeHighlights();
		}else{
						
			filter(text, isfilterByRegexp());
			if(getData().isEmpty()) {
				 field.setBackground(Color.red);
					
			}else{
				field.setBackground(Color.white);
			}
			setTreeModel(currentTreeModel);
			//Utils.expandAllNodes(window.getTree());
			HistoryTreeModel treeModel = (HistoryTreeModel) window.getTree().getModel();
			Utils.expandTreeToLevel(window.getTree(),treeModel.getDefaultExpandLevel());
			((ChangeSetHistoryTreeCellRenderer)window.getTree().getCellRenderer()).setHighlightTerm(text, isfilterByRegexp());
		}
    }
	
	private int getLimit(){
		return (Integer)window.getSpLimit().getValue();
	}
	
	public boolean isFilterByDate() {
		return filterByDate;
	}

	public void setFilterByDate(boolean filterByDate) {
		this.filterByDate = filterByDate;
	}

	public boolean isfilterByRegexp(){
		return window.getChbRegexp().isSelected();
	}
	
	private void initWindow(){
		ExpandCollapseTreeAction expandCollapseAction = new ExpandCollapseTreeAction(window.getTree());
		DefaultTreeCellRenderer l_treeRenderer = new ChangeSetHistoryTreeCellRenderer();
		window.getTree().setCellRenderer(l_treeRenderer);
		window.getTree().setRowHeight(17);
		window.getTree().expandRow(0);
		window.getTree().setRootVisible(false);
		window.getTree().addKeyListener(new HistoryTreeKeyListener(this));
		window.getTree().addMouseListener(new HistoryTreeMouseListener(this));
		window.getTree().setComponentPopupMenu(getTreePopUpMenu());

		window.getBtnExpandAll().setToolTipText("Expand");
		window.getBtnExpandAll().setText(null);
		window.getBtnExpandAll().setActionCommand(ExpandCollapseTreeAction.EXPAND);
		window.getBtnExpandAll().setIcon(Icons.EXPAND_ALL_ICON);
		//window.getBtnExpandAll().setBorderPainted(false);
		window.getBtnExpandAll().addActionListener(expandCollapseAction);
		
		window.getBtnCollapseAll().setToolTipText("Collapse");
		window.getBtnCollapseAll().setActionCommand(ExpandCollapseTreeAction.COLLAPSE);
		window.getBtnCollapseAll().setText(null);
		window.getBtnCollapseAll().setIcon(Icons.COLLAPSE_ALL_ICON);
		//window.getBtnCollapseAll().setBorderPainted(false);
		window.getBtnCollapseAll().addActionListener(expandCollapseAction);
		
		window.getBtnShowCurrentWS().addActionListener(new ShowOnlyCurrentWSAction(this));
		window.getBtnShowCurrentWS().setToolTipText(window.getBtnShowCurrentWS().getText());
		window.getBtnShowCurrentWS().setText(null);
		window.getBtnShowCurrentWS().setIcon(Icons.CURRENT_WORKSPACE_ICON);
		window.getBtnShowCurrentWS().setEnabled(workspace!=null);
		window.getBtnShowCurrentWS().setSelected(workspace!=null);
		
		window.getBtnShowAsList().addActionListener(new ShowResourcesAsListAction(this));
		window.getBtnShowAsList().setToolTipText(window.getBtnShowAsList().getText());
		window.getBtnShowAsList().setText(null);
		window.getBtnShowAsList().setIcon(Icons.SHOW_AS_LIST_ICON);
		window.getBtnShowAsList().setSelected(true);
		KeyListener searchKeyListener = new TxtSearchKeyListener(this);
		window.getSearch().addKeyListener(searchKeyListener);
		
		SetTreeModel treeModelActionListener = new SetTreeModel(this);
		
		window.getBtnWDCModelView().setToolTipText(window.getBtnWDCModelView().getText());
		window.getBtnWDCModelView().setText(null);
		window.getBtnWDCModelView().setIcon(Icons.WORKSPACE_ICON);
		window.getBtnWDCModelView().setActionCommand(HistoryTreeModel.WC_MODEL);
		window.getBtnWDCModelView().addActionListener(treeModelActionListener);
		window.getBtnWDCModelView().setSelected(true);
		
		window.getBtnJiraModelView().setToolTipText(window.getBtnJiraModelView().getText());
		window.getBtnJiraModelView().setText(null);
		window.getBtnJiraModelView().setIcon(Icons.JIRA_ICON);
		window.getBtnJiraModelView().setActionCommand(HistoryTreeModel.J_MODEL);
		window.getBtnJiraModelView().addActionListener(treeModelActionListener);
		
		window.getBtnDWCModel().setToolTipText(window.getBtnDWCModel().getText());
		window.getBtnDWCModel().setText(null);
		window.getBtnDWCModel().setIcon(Icons.DATE_ICON);
		window.getBtnDWCModel().setActionCommand(HistoryTreeModel.C_MODEL);
		window.getBtnDWCModel().addActionListener(treeModelActionListener);
		//window.getBtnDWCModel().setSelected(false);
		
		window.getBtnDirectoryModel().setToolTipText(window.getBtnDirectoryModel().getText());
		window.getBtnDirectoryModel().setText(null);
		window.getBtnDirectoryModel().setIcon(Icons.CLOSE_PROJECT_ICON);
		window.getBtnDirectoryModel().setActionCommand(HistoryTreeModel.D_MODEL);
		window.getBtnDirectoryModel().addActionListener(treeModelActionListener);
		
		window.getBtnResourcesModel().setToolTipText(window.getBtnResourcesModel().getText());
		window.getBtnResourcesModel().setText(null);
		window.getBtnResourcesModel().setIcon(Icons.FILE_ICON);
		window.getBtnResourcesModel().setActionCommand(HistoryTreeModel.R_MODEL);
		window.getBtnResourcesModel().addActionListener(treeModelActionListener);
		//window.getBtnResourcesModel().setSelected(false);
		
		
		window.getBtnRefresh().setToolTipText(window.getBtnRefresh().getText());
		window.getBtnRefresh().setText(null);
		window.getBtnRefresh().setIcon(Icons.REFRESH_ICON);
		window.getBtnRefresh().setSelected(false);
		window.getBtnRefresh().addActionListener(new RefreshHistoryTree(this));
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		
		window.getSpShowFrom().setValue(cal.getTime());
		window.getSpLimit().setValue(limitRecords);
		setVisibleLimit(false);
		window.setIconImage(Icons.CVS_HISTORY_ICON.getImage());
		window.setAlwaysOnTop(false);
		window.setLocationByPlatform(false);
		window.setLocation(new Point(0,0));
		window.setSize(700, Utils.getScreenSize().height-50);
		window.setTitle("CVS Commits history "+ (workspace!=null ? workspace.getName() : ""));
		
	}
	
	public void showWindow(){
		focusWindow();
	}

	@Override
	public String getId() {

		return ID;
	}

	public Integer getLimitRecords() {
		return getLimit(); //limitRecords;
	}

	public void setLimitRecords(Integer limitRecords) {
		this.limitRecords = limitRecords;
	}
	
	public void setVisibleDateRange(boolean show){
		window.getSpShowFrom().setVisible(show);
		window.getSpShowTo().setVisible(show);
		window.getLblFrom().setVisible(show);
		window.getLblTo().setVisible(show);
	}
	
	public void setVisibleLimit(boolean show){
		window.getSpLimit().setVisible(show);
		window.getLbNumbuerRows().setVisible(show);
	}
	
	public static void main(String[] args){
		CVSHistoryController l_controller = new CVSHistoryController(null);
		l_controller.showWindow();
	}
	
	public void editWorkspaceName(){
		Object o = window.getTree().getLastSelectedPathComponent();
		if(o instanceof WorkspaceVO){
			changeWorkspaceName((WorkspaceVO) o);
		}else {
			JOptionPane.showMessageDialog(null, "Source is not editable.");
		}
	}
	
	private ActionListener getEditWSNameAction(){
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editWorkspaceName();
			}
		};
	}
	
	public void addJIRAComment(){
		Object o = window.getTree().getLastSelectedPathComponent();
		if(o instanceof ChangeSetVO){
			createJIRAComment((ChangeSetVO) o);
		}
	}
	
	
	private ActionListener getAddCommentAction(){
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addJIRAComment();
			}
		};
	}
	
	
	private JPopupMenu getTreePopUpMenu() {
	    JPopupMenu menu = new JPopupMenu();
	    
	    JMenuItem item = new JMenuItem("<html>Edit <span style='color:silver;'>(F6)</span></html>");
	   
	    item.addActionListener(getEditWSNameAction());
	    menu.add(item);

	    JMenuItem item2 = new JMenuItem("<html>Add JIRA comment <span style='color:silver;'>(F3)</span></html>");
	   
	    item2.addActionListener(getAddCommentAction());
	    menu.add(item2);	

	    return menu;
	}
	
	public void changeWorkspaceName(WorkspaceVO p_ws){
		 String l_newName = JOptionPane.showInputDialog("Change workspace name", p_ws.getName());
		 if(l_newName==null || p_ws.getName().equals(l_newName)) return;
		 p_ws.setName(l_newName);
		 WorkspaceDAO wsDao = new WorkspaceDAOImpl();
		 wsDao.updateWorkspaceName(p_ws);
		 refresh();
	}

	public void createJIRAComment(ChangeSetVO o) {
		JIRACommentCreator j = JIRACommentCreator.getInstance();
		j.createComment((ChangeSet) o);
		
	}
	
	private void focusWindow(){
		if(!window.isVisible()){
			window.setVisible(true);	
		}
		if(!window.hasFocus()){
			window.toFront();
			window.requestFocus();	
		}
		refresh();
	}
}
