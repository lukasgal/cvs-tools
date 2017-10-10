package org.gal.tools.handlers;

import java.util.HashMap;

import org.gal.tools.cvs.controllers.CVSHistoryController;
import org.gal.tools.cvs.model.WorkspaceVO;
import org.gal.tools.model.AbstractController;

public class ToolsFactory {
	private static HashMap<String, AbstractController> tools = new HashMap<>();
	
	private static WorkspaceVO currentWorkspace;
	
	public static AbstractController getTool(String id) throws Exception{
		//if(currentWorkspace==null) throw new Exception("ToolsFactory doesn't contain workspace reference.");
		AbstractController ctrl =  tools.get(id);
		if(ctrl==null){
			ctrl = createTool(id);
			tools.put(id, ctrl);
		}
		return ctrl; 
	}
	
	private static AbstractController createTool(String id){
		if(id.contains(CVSHistoryController.ID)){
			return new CVSHistoryController(currentWorkspace);
		}
		return null;
	}
	
	public static void setWorkspace(WorkspaceVO ws){
		currentWorkspace =ws;
	}
}
