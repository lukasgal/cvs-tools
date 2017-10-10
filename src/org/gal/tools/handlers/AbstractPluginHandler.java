package org.gal.tools.handlers;

import org.eclipse.ui.console.MessageConsole;
import org.gal.tools.model.AbstractController;
import org.gal.tools.utils.WorkspaceUtils;

public interface AbstractPluginHandler  {
	
	public final static String CONSOLE_NAME = "CVSTools";
	
	public static MessageConsole getConsole() {
		return WorkspaceUtils.getConsole(CONSOLE_NAME);
	}
	
	public AbstractController getMainApp() throws Exception;
}
