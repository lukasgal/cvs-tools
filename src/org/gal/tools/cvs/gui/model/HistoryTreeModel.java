package org.gal.tools.cvs.gui.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gal.tools.cvs.comparators.TreeNodeCompareByName;
import org.gal.tools.cvs.model.AbstractTreeModel;
import org.gal.tools.cvs.model.AbstractTreeNode;
import org.gal.tools.utils.Logger;

public class HistoryTreeModel implements TreeModel {

	private static final String TREE_MODEL_DIRECTORY_RESOURCE = "org.gal.tools.cvs.model.TreeModelDirectoryResource";
	private static final String TREE_MODEL_COMMIT_DATE_TIME = "org.gal.tools.cvs.model.TreeModelCommitDateTime";
	private static final String TREE_MODEL_WORKSPACE = "org.gal.tools.cvs.model.TreeModelWorkspace";
	private static final String TREE_MODEL_JIRA = "org.gal.tools.cvs.model.TreeModelJira";
	private static final String TREE_MODEL_RESOURCE = "org.gal.tools.cvs.model.TreeModelResource";
	private static final String TREE_MODEL_PROJECTS = "org.gal.tools.cvs.model.TreeModelProjects";
	
	public static final String C_MODEL = "cmodel";
	public static final String J_MODEL = "jmodel";
	public static final String WC_MODEL = "wcmodel";
	public static final String R_MODEL = "rmodel";
	public static final String D_MODEL = "dmodel";
	
	private AbstractTreeNode root;

	private int defaultExpandLevel = 2;
	
	private Comparator<AbstractTreeNode> comparator = new TreeNodeCompareByName();
	
	private String model;
	
	public AbstractTreeModel createModel(AbstractTreeNode root, String mainClass, ArrayList<String> childClasses){
		AbstractTreeModel model = null;
		
		try {
			
			model = (AbstractTreeModel) Class.forName(mainClass).getConstructor(AbstractTreeNode.class).newInstance(root);
			Collections.sort(model.getRoot().getRows(), Collections.reverseOrder(comparator));
			if(childClasses.isEmpty()) return model;
			mainClass = childClasses.remove(0);
			
			for(AbstractTreeNode node : model.getRoot().getRows()){
				createModel(node, mainClass, childClasses);
			}
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			Logger.addStackTrace(e);
		}
		return model;
	}
	
	public String getCurrentModel(){
		return model;
	}
	
	private ArrayList<String> getRModel(){
		defaultExpandLevel = 1;
		ArrayList<String> childClasses = new ArrayList<>();
		childClasses.add(TREE_MODEL_RESOURCE);
		return childClasses;
	}
	
	private ArrayList<String> getWCModel(){
		defaultExpandLevel = 2;
		ArrayList<String> childClasses = new ArrayList<>();
		childClasses.add(TREE_MODEL_WORKSPACE);
		childClasses.add(TREE_MODEL_COMMIT_DATE_TIME);
		return childClasses;
	}
	
	private ArrayList<String> getJModel(){
		ArrayList<String> childClasses = new ArrayList<>();
		defaultExpandLevel = 1;
		childClasses.add(TREE_MODEL_WORKSPACE);
		childClasses.add(TREE_MODEL_JIRA);
		return childClasses;
	}
	
	private ArrayList<String> getCModel(){
		defaultExpandLevel = 1;
		ArrayList<String> childClasses = new ArrayList<>();
		childClasses.add(TREE_MODEL_COMMIT_DATE_TIME);
		//childClasses.add(TREE_MODEL_WORKSPACE);
		return childClasses;
	}
	
	private ArrayList<String> getDModel(){
		defaultExpandLevel = 0;
		ArrayList<String> childClasses = new ArrayList<>();
		childClasses.add(TREE_MODEL_PROJECTS);
		//childClasses.add(TREE_MODEL_WORKSPACE);
		return childClasses;
	}
	
	public HistoryTreeModel(AbstractTreeNode root,String modelName, boolean showAsList) {
		ArrayList<String> childClasses = null;
		model = modelName;
		
		if(modelName.contentEquals(WC_MODEL)){
			childClasses = getWCModel();
		}else if(modelName.contentEquals(J_MODEL)){
			childClasses = getJModel();
		}else if(modelName.contentEquals(C_MODEL)){
			childClasses = getCModel();
		}else if(modelName.contentEquals(R_MODEL)){
			childClasses = getRModel();
		}else if(modelName.contentEquals(D_MODEL)){
			childClasses = getDModel();
		}
		
		if(!showAsList){
			childClasses.add(TREE_MODEL_DIRECTORY_RESOURCE);	
		}		
		this.root = createModel(root,childClasses.remove(0) , childClasses).getRoot();
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getChild(Object o, int i) {
		if(o instanceof AbstractTreeNode){
			return ((AbstractTreeNode) o).getRow(i);
		}
		return null;
	}

	@Override
	public int getChildCount(Object o) {
		if(o instanceof AbstractTreeNode){
			return ((AbstractTreeNode) o).getRows().size();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object o, Object child) {
		if(o instanceof AbstractTreeNode){
			return ((AbstractTreeNode) o).getRows().indexOf(child);
		}
		return 0;
	}

	@Override
	public Object getRoot() {
		// TODO Auto-generated method stub
		return root;
	}

	@Override
	public boolean isLeaf(Object o) {
		if(o instanceof AbstractTreeNode){
			return ((AbstractTreeNode) o).getRows()==null || ((AbstractTreeNode) o).getRows().isEmpty(); 
		}
		return false;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
	public int getDefaultExpandLevel() {
		return defaultExpandLevel;
	}

	public void setDefaultExpandLevel(int defaultExpandLevel) {
		this.defaultExpandLevel = defaultExpandLevel;
	}

}
