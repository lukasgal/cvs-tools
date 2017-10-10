package org.gal.tools.utils;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.gal.tools.cvs.model.ChangeSet;
import org.gal.tools.cvs.model.ChangeSetVO;

public final class Utils {
	public final static String TIME_FORMAT = "HH:mm:ss";
	public final static String DATE_FORMAT = "yyyy/MM/dd";
	public final static String APP_STORAGE_PATH = "plugins"+File.separator+"cvs.tools";
	
		
	public static String convertToDateTime(Long timestamp,Boolean withTime){
		if(timestamp==null) return "";
	    Date date = new Date(timestamp);
	    String formatStr = DATE_FORMAT;
	    if(withTime){
	    	formatStr +=" "+TIME_FORMAT;
	    }
	    Format format = new SimpleDateFormat(formatStr);
	    return format.format(date);
	}
	
	public static String converToTime(Long timestamp){
		if(timestamp==null) return "";
	    Date date = new Date(timestamp);
	    String formatStr = TIME_FORMAT;
	    
	    Format format = new SimpleDateFormat(formatStr);
	    return format.format(date);
	}
	
	
	public static String getAppStoragePath() {
		
		String path = new File(".").getAbsolutePath()+File.separator + APP_STORAGE_PATH;
		File f = new File(path);
		f.mkdirs();
		return f.getAbsolutePath();
	}
	
	
	@SuppressWarnings("unused")
	private Object reflectMethod(ChangeSet chs, String methodName){
		String[] methods = methodName.split("\\.");
		Object o = chs;
		try{
			for(String ms : methods){
				Class cls = o.getClass();
				Method m = cls.getMethod(ms, null);
				 o = m.invoke(o, null);	
			}
			return o;
		}catch(Exception ex){
			Logger.addStackTrace(ex);
		}
		return null;
	}
	
	public static void expandAllNodes(JTree tree) {
	    int j = tree.getRowCount();
	    int i = 0;
	    while(i < j) {
	        tree.expandRow(i);
	        i += 1;
	        j = tree.getRowCount();
	    }
	}
	
	public static void expandTreeToLevel(JTree tree, int level) {
		int j,l;
		for(int i = 0; i < level; i++){
			j = tree.getRowCount();
			l = j;
	    	for(int k = j; k > 0;k--){
	    		tree.expandRow(l - k);
	    		//System.out.println("l="+l+" k="+k +" == "+(l - k));
	    		l = tree.getRowCount();
	    		 
	    	}
		}
	}
	
	public static void collapseTreeToLevel(JTree tree, int level) {
		int j,l;
		for(int i = level; i > 0 ; i--){
			j = tree.getRowCount();
			l = j;
	    	for(int k = 0; k < j;k++){
	    		tree.collapseRow(l - k);
	    		System.out.println("l="+l+" k="+k +" == "+(l - k));
	    		l = tree.getRowCount();
	    		 
	    	}
		}
	}

	public static void collapseAllNodes(JTree tree) {
		int j = tree.getRowCount();
	    int i = 0;
	    while(i < j) {
	        tree.collapseRow(i);
	        i += 1;
	        j = tree.getRowCount();
	    }
	}
	
	public static String getExpansionState(JTree tree){

	    StringBuilder sb = new StringBuilder();

	    for(int i =0 ; i < tree.getRowCount(); i++){
	        TreePath tp = tree.getPathForRow(i);
	        if(tree.isExpanded(i)){
	            sb.append(tp.toString());
	            sb.append(",");
	        }
	    }

	    return sb.toString();

	}   

	public static void setExpansionState(JTree tree, String s){

	    for(int i = 0 ; i<tree.getRowCount(); i++){
	        TreePath tp = tree.getPathForRow(i);
	        if(s.contains(tp.toString() )){
	            tree.expandRow(i);
	        }   
	    }
	}
	
	public static Dimension getScreenSize(){
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		return new Dimension(width, height);
	}

	public static Date getEndOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}

	public static Date getStartOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}
	
	public static String getDifferentPartOfStrings(String s1, String s2){
		StringBuilder sb1 = new StringBuilder();
		char c;
		for(int i = 0; i<Math.min(s1.length(),s2.length());i++){
		     if((c = s1.charAt(i)) != s2.charAt(i)){
		           sb1.append(c);
		     }
		 }
		return sb1.toString().trim();
	}
	
	public static String getJIRAIssueNumber(String p_text){
		Pattern ISSUE_PATTERN = Pattern.compile(Config.getInstance().getProperty("JIRA_issueNumberPattern", "^[A-Z]{2}-\\d+"));
		Matcher l_matcher = ISSUE_PATTERN.matcher(p_text);
		if(l_matcher.find()){
			return l_matcher.group(0);
		}		
		return null;
		
	}
	
	public static void openWebpage(String urlString) {
	    try {
	        Desktop.getDesktop().browse(new URL(urlString).toURI());
	    } catch (Exception e) {
	        Logger.addStackTrace(e);
	    }
	}
}
