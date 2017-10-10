package org.gal.tools.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.search.core.text.TextSearchEngine;
import org.gal.tools.cvs.gui.CVSHistoryWindow;

public class WorkspaceResourceWalker implements Runnable{
	// = new ArrayList<>(Arrays.asList("conf", "css", "ftl","gif","html","htm","ico","inc","ini","java","jpg","js","json","jsp","lib","png","readme","sql","svg","tpl","txt","woff","woff2","wsdl","xml","zip" ));
	public ArrayList<String> allowedExtensions;
	
	/**
	 * @param String - extension
	 * @param TreeSet . list of files
	 */
	private volatile HashMap<String,TreeSet<String>> files = new HashMap<>();
	
	
	public WorkspaceResourceWalker(ArrayList<String> allowedExtensions){
		if(allowedExtensions!=null && !allowedExtensions.isEmpty()){
			this.allowedExtensions = allowedExtensions;
		}
	}
	
	public void processContainer(IContainer container) throws CoreException{

		if (container == null || !container.isAccessible() || container.members().length == 0)
			return;
		   IResource [] members = container.members();
	   
		   for (IResource member : members){
	       if (member instanceof IContainer){
	    	   processContainer((IContainer)member);
	       }else if (member instanceof IFile){
	    	   processFile((IFile)member);  
	       } 
	    }
	} 
	
	public String endsWith(String path){
		
		for(String s : allowedExtensions){
			if(path.endsWith(s)) return s;
		}
		return null;
	}
	
	public void processFile(IFile file){
		String path = file.getFullPath().toString();
		String ext = endsWith(path);
		if(ext==null) return;
			
		
		TreeSet<String> paths;
		if(!files.containsKey(ext)){
			paths = new TreeSet<>();
			paths.add(path);
			files.put(ext, paths);
		}else{
			files.get(ext).add(path);
		}
	}
	
	public void loadWSFiles(){
		IContainer cont = ResourcesPlugin.getWorkspace().getRoot();
        	try {
    			processContainer(cont);
    		} catch (CoreException e) {
    			Logger.addStackTrace(e);
    		}
	}

	public HashMap<String, TreeSet<String>> getFiles() {
		return files;
	}

	@Override
	public void run() {
		loadWSFiles();
	}	
}