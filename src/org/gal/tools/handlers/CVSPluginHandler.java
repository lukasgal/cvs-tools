package org.gal.tools.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.team.internal.ccvs.core.CVSProviderPlugin;
import org.gal.tools.cvs.controllers.CVSHistoryController;
import org.gal.tools.cvs.listeners.CVSConsoleListener;
import org.gal.tools.cvs.model.WorkspaceDAO;
import org.gal.tools.cvs.model.WorkspaceDAOImpl;
import org.gal.tools.cvs.model.WorkspaceVO;
import org.gal.tools.utils.Logger;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CVSPluginHandler extends AbstractHandler implements AbstractPluginHandler{
	
	private WorkspaceVO currentWorkspace;
	
	private void setCurrentWorkspace(String path) throws Exception{
		WorkspaceDAO wsdao = new WorkspaceDAOImpl();
		WorkspaceVO ws = wsdao.getWorkspaceByPath(path);
		if(ws==null){
			ws = new WorkspaceVO();
			ws.setPath(path);
			ws.setName(path);
			int id = wsdao.insertWorkspace(ws);
			ws.setId(id);
		}
		currentWorkspace = ws;
	}
	
	public WorkspaceVO getCurrentWorkspace(){
		return currentWorkspace;
	}
	

	/**
	 * The constructor.
	 * 
	 * @throws Exception
	 */
	public CVSPluginHandler() {
		Logger.setConsole(AbstractPluginHandler.getConsole());
		String wsPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		try {
				
			setCurrentWorkspace(wsPath);
			ToolsFactory.setWorkspace(currentWorkspace);	
			CVSHistoryController app = getMainApp();
			CVSProviderPlugin.getPlugin().setConsoleListener(new CVSConsoleListener(app));
			Logger.addMessage("CVS console listener has been registered.");
		
			
		} catch (Exception e) {
			// JOptionPane.showMessageDialog(null, e.getMessage());
			Logger.addStackTrace(e);
		}
	}

	@Override
	public CVSHistoryController getMainApp() throws Exception{
		return (CVSHistoryController) ToolsFactory.getTool(CVSHistoryController.ID);
	}
	
	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			CVSHistoryController app = getMainApp();
			app.showWindow();
		} catch (Exception e) {
			Logger.addStackTrace(e);
		}
		return null;
	}
}
