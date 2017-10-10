package org.gal.tools.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.gal.tools.handlers.CVSPluginHandler;

public final class Logger {
	
	private static MessageConsoleStream consoleStream;
	
	public static void setConsole(MessageConsole console){
		consoleStream = console.newMessageStream();
	}
	
	public static void addMessage(String message){
		if(consoleStream!=null){
			consoleStream.println(message);
		}else{
			System.out.println(message);
		}
	}
	
	public static void addStackTrace(Exception ex){
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		String exceptionDetails = sw.toString();
		if(ex.getMessage()!=null && !ex.getMessage().isEmpty()){
			JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());	
		}
		
		if(consoleStream!=null){
			consoleStream.println(exceptionDetails);
		}else{
			System.out.println(exceptionDetails);
		}
		
	}

}
