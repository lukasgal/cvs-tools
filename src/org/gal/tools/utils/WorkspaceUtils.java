package org.gal.tools.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.ide.IDE;

public final class WorkspaceUtils {

	
	public static MessageConsole getConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
	
	public static void openFile(String filePath){
		Display.getDefault().asyncExec(new Runnable() {
		    @Override
		    public void run() {
		    	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if(window==null) return;
				IWorkbenchPage page = window.getActivePage();
				IPath path = new Path(filePath);
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
					Logger.addStackTrace(e);
				}
		    }
		});
			
	}
}
