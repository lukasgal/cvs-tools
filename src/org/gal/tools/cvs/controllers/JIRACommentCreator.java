package org.gal.tools.cvs.controllers;


import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.gal.tools.cvs.gui.JIRACommentDlg;
import org.gal.tools.cvs.model.AbstractTreeNode;
import org.gal.tools.cvs.model.ChangeSet;
import org.gal.tools.cvs.model.ChangeSetVO;
import org.gal.tools.cvs.model.CommitDateTime;
import org.gal.tools.cvs.model.JIRACommentDAOImpl;
import org.gal.tools.cvs.model.JIRACommentVO;
import org.gal.tools.cvs.model.OperationType;
import org.gal.tools.cvs.model.ResourceVO;
import org.gal.tools.utils.Config;
import org.gal.tools.utils.Icons;
import org.gal.tools.utils.Logger;
import org.gal.tools.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class JIRACommentCreator {
	
	private Config m_config;
	
	private static JIRACommentCreator l_instance;
	
	private static final String DEFAULT_COMMENT_TEMPLATE = "CHANGES:\n{code}${resources}{/code}";
	private static final String LINE_BREAK = "\n";
	
	
	private class JIRAResponse{
		
		private String m_text;
		private Integer m_httpCode;
		
		public JIRAResponse(String p_text, Integer p_httpCode){
			m_text =p_text;
			m_httpCode = p_httpCode;
		}
		
		public String getText() {
			return m_text;
		}
		public Integer getHttpCode() {
			return m_httpCode;
		}
	}
	
	
	public static JIRACommentCreator getInstance(){
		if(l_instance==null){
			l_instance = new JIRACommentCreator();
		}
		return l_instance;
	}
	
	public void createComment(ChangeSet l_chs){
		if(l_chs==null) return;
		String url = m_config.getProperty("JIRA_addCommentRestUrl", null);
		String issueNumber = Utils.getJIRAIssueNumber(l_chs.getName());
		if(issueNumber==null){
			return;
		}
		String title = l_chs.getName();
		
		showDialog(getCommentText(l_chs), issueNumber, url, title);
	}
	
	private JIRACommentCreator(){
		m_config = Config.getInstance();
	}
	
	public String getCommentText(ChangeSet p_chs){
		StringBuilder l_sbRes = new StringBuilder();
		if(p_chs instanceof ChangeSet){
			for(AbstractTreeNode l_res : ((ChangeSetVO)p_chs).getRows()){
				ResourceVO r = (ResourceVO)l_res;
				if("true".equals(m_config.getProperty("addOperationTypeToResource", "true"))){
					l_sbRes.append(OperationType.toString(r.getOperationType())+" ");	
				}
				
				l_sbRes.append(r).append(LINE_BREAK);
			}	
		}
		
		String l_template = m_config.getProperty("JIRA_commentTemplate", DEFAULT_COMMENT_TEMPLATE);
		l_template = l_template.replace("${resources}", l_sbRes);
		l_template = l_template.replace("${version}", p_chs.getWorkspace().getName());
		return l_template;
	}
	
	private JIRAResponse addComment(String p_text, String p_url) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, ParseException{
		 
         URL obj = new URL(p_url);

         HttpURLConnection con = (HttpURLConnection) obj.openConnection();
         JSONObject json = new JSONObject();
         json.put("body", p_text);
         
         con.setRequestMethod("POST");
         String username = m_config.getProperty("JIRA_username", "");
         String password = m_config.getProperty("JIRA_password", "");

         String cred = username+":"+password;
         byte[] encoded = Base64.getEncoder().encode(cred.getBytes());
         String credentials = new String(encoded);

         //Setting the Authorization Header as 'Basic' with the given credentials
         con.setRequestProperty("Authorization", "Basic " + credentials);
         con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
         con.setRequestProperty("User-Agent", HttpHeaders.USER_AGENT);
         con.setDoOutput(true);
         con.setDoInput(true);
         con.setDefaultUseCaches(false);
         OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());
         
         wr.write(json.toString());
         wr.flush();

         Integer responseCode = con.getResponseCode();
         
         //Logger.addMessage(responseCode.toString());
         //reading the return
         BufferedReader in = new BufferedReader(
                 new InputStreamReader(con.getInputStream())
         );
         String inputLine;
         StringBuffer response = new StringBuffer();

         while ((inputLine = in.readLine()) != null) {
             response.append(inputLine);
         }
         in.close();

         //String result = response.toString();
         
         return new JIRAResponse(response.toString(), responseCode);
	}
	
	private void showDialog(String p_text, String p_issueNumber, String p_url, String p_title) {
		
		JIRACommentDlg p_dialog = new JIRACommentDlg(null, true);
		
		p_dialog.setIconImage(Icons.CVS_HISTORY_ICON.getImage());
		
		p_dialog.setTitle("Add JIRA comment");
		
		p_dialog.setSize(1000, 500);
		
		p_dialog.setLocationRelativeTo(null);
		
		final String propIssueUrl = m_config.getProperty("JIRA_issueUrl", null);
		
		if(propIssueUrl!=null){
			final String issueUrl = propIssueUrl.replace("${issue}", p_issueNumber);
			p_dialog.getLblIssueNumber().setText("<html><span style='color:blue;text-decoration:underline;cursor:pointer'>"+p_title+"</span></html>");
			p_dialog.getLblIssueNumber().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			p_dialog.getLblIssueNumber().setToolTipText("Open issue in web browser.");
			p_dialog.getLblIssueNumber().addMouseListener(new MouseAdapter() {
			    public void mouseClicked(MouseEvent e)  {  
			    	Utils.openWebpage(issueUrl);
			    }  
			});
		}else{
			p_dialog.getLblIssueNumber().setText(p_title);	
		}
		
		
		p_dialog.getTxtComment().setText(p_text);
		
		p_dialog.getTxtComment().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
					p_dialog.dispose();
				}
			}
		});
		
		p_dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		if(p_url!=null){	
			p_dialog.getBtnAddComment().addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					try {
						JIRACommentVO comment = new JIRACommentVO();
						comment.setCreated( new CommitDateTime(System.currentTimeMillis()));
						comment.setIssueNumber(p_issueNumber);
						comment.setText(p_dialog.getTxtComment().getText());
						
						JIRAResponse resp = addComment(comment.getText(), p_url.replace("${issue}", comment.getIssueNumber()));
						
						Integer status = resp.getHttpCode();
						
						if(status==HttpStatus.SC_CREATED){
							comment.setResponseText(resp.getText());
							JIRACommentDAOImpl.getInstance().insertComment(comment);
							JOptionPane.showMessageDialog(null, "Comment has been created.");
						}
						p_dialog.dispose();
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
							| SecurityException | IOException | ParseException e) {
						Logger.addStackTrace(e);
					}
				}
			});
		}else{
			p_dialog.getBtnAddComment().setEnabled(false);
		}
		p_dialog.getBtnCancel().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				p_dialog.dispose();
				
			}
		});
		
		p_dialog.setVisible(true);
	}

	
	public static void main(String[] args){
/*		ChangeSetDAOImpl dao = new ChangeSetDAOImpl();
		ChangeSet chs =  dao.getChangeSet(10);

		JIRACommentCreator j = JIRACommentCreator.getInstance();
		j.createComment(chs);*/
	}
}
