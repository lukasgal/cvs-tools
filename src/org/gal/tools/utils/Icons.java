package org.gal.tools.utils;

import javax.swing.Icon;
import javax.swing.ImageIcon;



public final class Icons {
	
	public final static String ICONS_DIR = "/org/gal/tools/icons/";
	
	private Icons icons = new Icons();
	
	public static ImageIcon CVS_HISTORY_ICON = icon("cvshistory.gif");
	
	
	public static ImageIcon REFRESH_ICON = icon("refresh.gif");
	public static ImageIcon APPLICATION_ICON = icon("sample.gif");
	public static ImageIcon WORKSPACE_ICON = icon("workspace.gif");
	public static ImageIcon CURRENT_WORKSPACE_ICON = icon("currentWorkspace.gif");
	public static ImageIcon CHANGESET_ICON = icon("package.gif");
	public static ImageIcon FILE_ICON = icon("resource.gif");
	public static ImageIcon FILE_EDIT_ICON = icon("resource_edit.gif");
	public static ImageIcon FILE_REMOVE_ICON = icon("resource_remove.gif");
	public static ImageIcon FILE_ADD_ICON = icon("resource_add.gif");
	public static ImageIcon DATE_ICON = icon("date.gif");
	public static ImageIcon OPEN_DIR_ICON = icon("dirOpen.gif");
	public static ImageIcon CLOSE_DIR_ICON = icon("dir.gif");
	public static ImageIcon EXPAND_ALL_ICON = icon("expandAll.gif");
	public static ImageIcon COLLAPSE_ALL_ICON = icon("collapseAll.gif");
	public static ImageIcon SHOW_AS_LIST_ICON = icon("showAsList.gif");
	public static ImageIcon CLOSE_PROJECT_ICON = icon("project_close.gif");
	public static ImageIcon OPEN_PROJECT_ICON = icon("project_open.gif");
	public static ImageIcon JIRA_ICON = icon("jira.gif");
	
	private static ImageIcon icon(String name){
		try{
			return new ImageIcon(Icons.class.getResource(ICONS_DIR+name));
		}catch(Exception e){
			Logger.addStackTrace(e);
		}
		return null;
	}
	
	

}
