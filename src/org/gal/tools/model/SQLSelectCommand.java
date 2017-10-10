package org.gal.tools.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface SQLSelectCommand {
	
	public  <T> List<T> execute(Connection conn) throws SQLException;
	
}
