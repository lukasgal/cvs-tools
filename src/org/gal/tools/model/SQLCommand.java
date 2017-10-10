package org.gal.tools.model;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLCommand {

    public void execute(Connection conn) throws SQLException;
	
}
