package org.gal.tools.cvs.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.gal.tools.utils.Utils;
/**
 * Generate tree Root-> Workspace-> Commit date -> Change set -> Resource
 * @author oem
 *
 */
public class TreeModelJira extends AbstractTreeModel{
	
		
	public TreeModelJira(AbstractTreeNode root){
		this.root = root;
		generate();
	}

	
	
	protected void generate(){
		ArrayList<AbstractTreeNode> data = root.getRows();
		AbstractTreeNode newRoot = new RootTreeNode("JiraRoot", new ArrayList<AbstractTreeNode>());
		if(data==null) return;
		Map<String, ChangeSetVO> l_map = new TreeMap<>();
		
		for(AbstractTreeNode chs : data){
			ChangeSetVO l_chs = ((ChangeSetVO) chs);
			String l_issueNumber = Utils.getJIRAIssueNumber(l_chs.getName());
			if(l_issueNumber!=null){
				ChangeSetVO l_issueChs = l_map.get(l_issueNumber);
				if(l_issueChs!=null){
					l_issueChs.mergeResources(l_chs);
					if(l_issueChs.getName().length() > l_chs.getName().length()){
						String l_part = Utils.getDifferentPartOfStrings(l_issueChs.getName(), l_chs.getName());
						if(!l_part.isEmpty()){
							l_part = "#"+l_part;
						}
						l_issueChs.setName(l_chs.getName()+l_part);
					}
				}else{
					l_issueChs = l_chs.clone();
					l_issueChs.setCommitDateTime(0L);
					l_map.put(l_issueNumber, l_issueChs);
				}
			}
		}
		for(ChangeSetVO l_ch : l_map.values()){
			newRoot.addRow(l_ch);
		}
		root.setRows(newRoot.getRows());
	}
}
