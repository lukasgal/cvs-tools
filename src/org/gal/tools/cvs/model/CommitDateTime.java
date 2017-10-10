package org.gal.tools.cvs.model;

import org.gal.tools.utils.Utils;

public class CommitDateTime extends AbstractTreeNode implements Comparable<CommitDateTime> {

	private Long commitDateTime; 
	
	public CommitDateTime(Long dt){
		commitDateTime = dt;
	}
	
	
	public Long getTimeStamp() {
		return commitDateTime!=null ? commitDateTime : 0L;
	}



	public void setCommitDateTime(Long commitDateTime) {
		this.commitDateTime = commitDateTime;
	}


	public String getFormattedCommitDateTime(){
		return commitDateTime > 0 ? Utils.convertToDateTime(commitDateTime, true): "";
	}
	
	public String getFormattedCommitDate(){
		return commitDateTime > 0 ? Utils.convertToDateTime(commitDateTime, false): "";
	}

	public String getFormattedCommitTime(){
		return commitDateTime > 0 ? Utils.converToTime(commitDateTime): "";
	}
	
	
	@Override
	public String getName() {
		return getFormattedCommitDate();
	}
	
	public String toString(){
		return getFormattedCommitDate();
	}

	@Override
	public int compareTo(CommitDateTime o) {
		commitDateTime.compareTo(o.getTimeStamp());
		return 0;
	}
}
