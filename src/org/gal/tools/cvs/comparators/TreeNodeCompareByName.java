package org.gal.tools.cvs.comparators;

import java.util.Comparator;

import org.eclipse.core.resources.IResource;
import org.gal.tools.cvs.model.AbstractTreeNode;
import org.gal.tools.cvs.model.ResourceVO;

public class TreeNodeCompareByName implements Comparator<AbstractTreeNode>{

	boolean isAsc = false;
	
	public TreeNodeCompareByName(){
		
	}
	
	public TreeNodeCompareByName(boolean p_asc){
		isAsc = p_asc;
	}
	
	@Override
	public int compare(AbstractTreeNode o1, AbstractTreeNode o2) {
		if((o1 instanceof ResourceVO)){
			return (isAsc ? -1 : 1) * ((ResourceVO)o2).getRelativePath().compareTo(((ResourceVO)o1).getRelativePath());
		}
		return  (isAsc ? -1 : 1) * o1.getName().compareTo(o2.getName());
		
		
	}

}
