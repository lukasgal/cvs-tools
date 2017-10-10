package org.gal.tools.cvs.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.team.internal.ccvs.core.client.Session;
import org.eclipse.team.internal.ccvs.core.client.listeners.IConsoleListener;
import org.gal.tools.cvs.controllers.CVSHistoryController;
import org.gal.tools.cvs.model.AbstractTreeNode;
import org.gal.tools.cvs.model.ChangeSet;
import org.gal.tools.cvs.model.ChangeSetDAOImpl;
import org.gal.tools.cvs.model.ChangeSetVO;
import org.gal.tools.cvs.model.OperationType;
import org.gal.tools.cvs.model.ResourceVO;
import org.gal.tools.utils.Logger;

public class CVSConsoleListener implements IConsoleListener {

	private static final CharSequence QUOTE_REPLACER = "${Quote}";
	

	private static final String PATTERN_REVISION_NEW_OLD = "^new revision:\\s*([\\d\\.]*); previous revision:\\s*([\\d\\.]*)$";
	private static final String PATTERN_REVISION_INIT = "^initial revision:\\s*([\\d\\.]*)$";
	private static final String PATTERN_COMMIT_RESOURCE = "^(.*)\\s<--\\s*(.*)$";
	


	
	
	private ArrayList<ProcessedElement> processes = new ArrayList<>();
	
	private ArrayList<ProcessedElement> processedFiles = new ArrayList<>();
	
	private ChangeSetDAOImpl dao;

	private String pathInProgress;
	
	private CVSHistoryController m_controller;
	
	public CVSConsoleListener(CVSHistoryController controller){
		dao = new ChangeSetDAOImpl();
		m_controller = controller;
	}
	
	private class ProcessedElement {
		
		private AbstractTreeNode element;
		private Integer operationType;
		private Session session;
		
		public ProcessedElement(AbstractTreeNode element, Integer opType, Session session){
			this.element = element;
			this.operationType = opType;
			this.session = session;
		}

		public AbstractTreeNode getElement() {
			return element;
		}
		
		public Session getSession() {
			return session;
		}

		public void setSession(Session session) {
			this.session = session;
		}

		public void setElement(AbstractTreeNode element) {
			this.element = element;
		}

		public Integer getOperationType() {
			return operationType;
		}

		public void setOperationType(Integer operationType) {
			this.operationType = operationType;
		}

	}
	private ArrayList<ProcessedElement> getElementsBySession(Session session){
		ArrayList<ProcessedElement> list = new ArrayList<>();
		for(ProcessedElement el : processes){
			if(el.getSession().equals(session)){
				list.add(el);				
			}
		}
		return list;
	}
	
	private void removeProcessed(ArrayList<ProcessedElement> p){
		for(ProcessedElement el : p){
			processes.remove(el);
		}
	}
	
	
	private ProcessedElement getProcessedFile(ResourceVO origin){
		if(!processedFiles.isEmpty()){
			for(ProcessedElement pf : processedFiles){
				ResourceVO res = ((ResourceVO)pf.getElement());
				if(res.equals(origin)){
					return pf; 
				}
			}	
		}
		return null;
	}
	
	@Override
	public void commandCompleted(Session session, IStatus status, Exception exception) {
		ArrayList<ProcessedElement> list = getElementsBySession(session);
		if(list.isEmpty()){
			return;
		}
		try{
			if(exception!=null){
				removeProcessed(list);
				Logger.addStackTrace(exception);
				return;
			}
			
			/*if(status.getCode()!=IStatus.OK){
				removeProcessed(list);
				Logger.addMessage(status.getMessage());
				return;
			}*/
			
			for(ProcessedElement processed : list){
				if(processed == null) return;
				
				AbstractTreeNode element = processed.getElement();
				
				if(element instanceof ResourceVO){
					processed.setSession(null);
					processedFiles.add(processed);
				}else if(element instanceof ChangeSet){
					ChangeSet chs = ((ChangeSet)element);
					
					for(AbstractTreeNode iRow : chs.getRows()){
						ResourceVO iRowRes = ((ResourceVO)iRow);
						ProcessedElement pe = getProcessedFile(iRowRes);
						int opType = OperationType.UPDATE;
						if(pe!=null){
							opType = pe.getOperationType();
							processedFiles.remove(pe);
						}
						if(iRowRes.getOperationType()==null){
							iRowRes.setOperationType(opType);	
						}
					}
					
					chs.setCommited(true);
					chs.setCommitDateTime(System.currentTimeMillis());
					saveChangeSet(chs);
				}
				processes.remove(processed);
			}
		}finally{
			Logger.addMessage("commandCompleted session="+session.getCurrentCommand().toString()+" status="+status.getCode()+" exception="+exception);	
		}
		
	}
	
	private void saveChangeSet(ChangeSet chs){
		java.util.List<AbstractTreeNode> similarChs = dao.getSimilarChangeSet(chs);
		if(similarChs==null || similarChs.isEmpty()){
			dao.insertChangeSet(chs);
		}else{
			ChangeSet storedChs = (ChangeSet) similarChs.get(0);
			storedChs.mergeResources(chs);
			dao.updateChangeSet(storedChs);			
		}	
		
		m_controller.showWindow();
	}
	
	private ResourceVO createResource(String path){
		String wsPath = CVSHistoryController.getCurrentWorkspace().getPath();			
		String relPath = path;
		String absPath = wsPath+relPath;
		File f = new File(absPath);
		Integer op = null;
		if(!f.exists()){
			op = OperationType.DELETE;
		}
		String fileName = f.getName();
		ResourceVO res = new ResourceVO();
		res.setAbsolutePath(absPath);
		res.setRelativePath(relPath);
		res.setName(fileName);
		res.setOperationType(op);
		return res;
	}
	
	private ChangeSetVO createChangeSetFromParams(String name, ArrayList<String> res){
		if(name==null || res==null || res.isEmpty()) return null;
		
		ChangeSetVO chs = new ChangeSetVO();
		chs.setWorkspace(CVSHistoryController.getCurrentWorkspace());
		chs.setName(name);
		for(String s : res){
			chs.addRow(createResource(s));
		}
		return chs;
	}

	@Override
	public void commandInvoked(Session session, String paramString) {
		if(!isAddCommand(paramString) && !isCommitCommand(paramString)) return;
		
		ArrayList<String> parsedElement = parseParamStrByQuotes(paramString);
		if(parsedElement.isEmpty()) {
			Logger.addMessage("Command "+paramString+ " doesn't contanin any valid element.");
			return;
		}

		if(isAddCommand(paramString)){
			for(String path : parsedElement){
				ResourceVO res = createResource(path);
				if(res!=null){
					processes.add(new ProcessedElement(res, OperationType.ADD,session));	
				}	
			}
			
		}else if(isCommitCommand(paramString)){
			Logger.addMessage("commandInvoked - cvs ci startsWith == true");
			
			String name = parsedElement.get(0);
			parsedElement.remove(0);

			
			AbstractTreeNode castChs;
			Integer opType = OperationType.UPDATE;
			castChs = createChangeSetFromParams(name,parsedElement);
			processes.add(new ProcessedElement(castChs, opType,session));
		}
		Logger.addMessage("commandInvoked "+paramString);
	}

	@Override
	public void errorLineReceived(Session session, String arg1, IStatus arg2) {
		Logger.addMessage("errorLineReceived paramStr="+arg1+" status="+arg2.getCode());

	}

	@Override
	public void messageLineReceived(Session session, String paramString, IStatus arg2) {
		String match;
		if((match = getCommitedResource(paramString))!=null){
			 pathInProgress = match;
		}else if((match = getNewRevision(paramString))!=null && pathInProgress != null){
			setRevision(session,pathInProgress, match);
			 pathInProgress = null;			
		}else if((match = getInitRevision(paramString))!=null && pathInProgress != null){
			setRevision(session,pathInProgress, match);
			pathInProgress = null;
		}
		
		
		
		Logger.addMessage("messageLineReceived cmd="+paramString+" paramString="+arg2+" status="+arg2.getCode());

	}


	private void setRevision(Session session, String path, String revision){
		ArrayList<ProcessedElement> list = getElementsBySession(session);
		for(ProcessedElement processed : list){
			if(processed == null) return;
			
			AbstractTreeNode element = processed.getElement();
			
			if(element instanceof ChangeSet){
				ChangeSet chs = ((ChangeSet)element);
				
				for(AbstractTreeNode iRow : chs.getRows()){
					ResourceVO iRowRes = ((ResourceVO)iRow);
					if(path.contains(iRowRes.getRelativePath())){
						iRowRes.setRevision(revision);
						break;
					}
				}
			}
		}
	}
	
	private static boolean isAddCommand(String cmd){
		return cmd.startsWith("cvs add"); 
	}
	
	private static boolean isCommitCommand(String cmd){
		return cmd.startsWith("cvs ci"); 
	}
	

	
	public static ArrayList<String> parseParamStrByQuotes(String str){
		ArrayList<String> list = new ArrayList<>();
		str = str.replace("\"\"", QUOTE_REPLACER);
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(str);
		String match;
		while (m.find()) {
			match = m.group(1).replace(QUOTE_REPLACER, "\"");
		  list.add(match);
		}
		return list;
	}
	public String getNewRevision(String paramString){
		Pattern p = Pattern.compile(PATTERN_REVISION_NEW_OLD);
		Matcher m = p.matcher(paramString);
		if(m.matches()){
			return m.group(1);	
		}
		return null;
	}
	
	public String getInitRevision(String paramString){
		Pattern p = Pattern.compile(PATTERN_REVISION_INIT);
		Matcher m = p.matcher(paramString);
		if(m.matches()){
			return m.group(1);	
		}
		return null;
	}
	
	public String getCommitedResource(String paramString){
		Pattern p = Pattern.compile(PATTERN_COMMIT_RESOURCE);
		Matcher m = p.matcher(paramString);
		if(m.matches()){
			return m.group(1);	
		}
		return null;
	}
	
	public static void main(String[] args){
		String testStr = "cvs ci -m \"KC-461: JavaScrip Error while editing table in CK editor\" -l \"/de.usu.fw.ui.smartclient.ckeditor/web/js/ckeditor/core/editor.js\" \"/de.usu.fw.ui.smartclient.ckeditor/CKeditorSetup.txt\"";
		System.out.println(parseParamStrByQuotes(testStr));
	}
	
}
