package org.gal.tools.cvs.model;

public final class OperationType {
	public static final Integer ADD = 0;
	public static final Integer UPDATE = 1;
	public static final Integer DELETE = 2;
	public static final Integer VIEW = 3;	
	
	public static String toString(Integer op){
		String s = null;
		
		if(ADD.equals(op)){
			s = "I";
		}else if(UPDATE.equals(op)){
			s = "U";
		}else if(DELETE.equals(op)){
			s = "D";
		}
		return s;
	}
}
