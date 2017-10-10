package org.gal.tools.cvs.model;

public class JIRACommentVO {

	private Integer m_id;
	private CommitDateTime m_created;
	private String m_issueNumber;
	private String m_text;
	private String m_responseText;
	
	public Integer getId() {
		return m_id;
	}
	public void setId(Integer id) {
		this.m_id = id;
	}
	public CommitDateTime getCreated() {
		return m_created;
	}
	public void setCreated(CommitDateTime created) {
		this.m_created = created;
	}
	public String getIssueNumber() {
		return m_issueNumber;
	}
	public void setIssueNumber(String issueNumber) {
		this.m_issueNumber = issueNumber;
	}
	public String getText() {
		return m_text;
	}
	public void setText(String text) {
		this.m_text = text;
	}
	public String getResponseText() {
		return m_responseText;
	}
	public void setResponseText(String response) {
		this.m_responseText = response;
	}
}
