package org.gal.tools.cvs.model;

import java.util.List;

public interface ChangeSetDAO {
	
	public List<AbstractTreeNode> getAllChangeSets(Integer wsId, Integer limit,  Long dateFrom, Long dateTo);
	   public ChangeSet getChangeSet(int id);
	   public List<AbstractTreeNode> filterByName(String chName, String rName, Integer workspaceId, Long dateFrom, Long dateTo, boolean useRegexp);
	   public void insertChangeSet(ChangeSet changeSet);
	   public void updateChangeSet(ChangeSet changeSet);
	   public void deleteChangeSet(ChangeSet changeSet);

}
