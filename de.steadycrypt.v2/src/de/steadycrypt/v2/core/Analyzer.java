/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Analyzer checks whether the file has already been encrypted
 * or other analytics.
 */
public class Analyzer {

	/**
	 * Makes a database query, asking for a data set which matches in name and size.
	 * If ResultSet is empty, file does not exist (return false). If not empty, file exists (return true).
	 * @return boolean
	 * @throws SQLException 
	 */
	public boolean fileAlreadyExists(FileInfo fi) throws SQLException {
		
		Connection conn = DbManager.getConnection();
		
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM content WHERE name='"+ fi.getName() +"' AND size ="+ fi.length());
		
		if(rs.next()) {
			return true;
		}
		else { return false; }
		
	}
	
	

}
