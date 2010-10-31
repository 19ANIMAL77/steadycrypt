/**
 * Date: 31.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.core.DbManager;

public class EncryptedFileDao {

	private final String INSERT_FILE = "INSERT INTO CONTENT (name, type, size, enc_date, org_path, enc_name) VALUES (?, ?, ?, ?, ?, ?)";
	private final String UPDATE_FILE = "UPDATE CONTENT SET name=?, type=?, size=?, enc_date=?, org_path=?, enc_name=? WHERE id=?";
	private final String SELECT_FILE = "SELECT name, type, size, enc_date, org_path, enc_name FROM content ORDER BY id desc";
	private final String DELETE_FILE = "SELECT name, type, size, enc_date, org_path, enc_name FROM content ORDER BY id desc";
	
	public Object[] getAllFiles()
	{
		List<EncryptedFile> files = new ArrayList<EncryptedFile>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_FILE);
			
			while(rs.next())
			{
				files.add(new EncryptedFile(rs.getString("name"), rs.getString("type"), rs.getLong("size"), rs.getDate("enc_date"),
						rs.getString("org_path"), rs.getString("enc_name")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return files.toArray();		
	}

	public void addFile(EncryptedFile encryptedFile)
	{
		
	}

	public void addMultipleFiles(ArrayList<EncryptedFile> encryptedFiles)
	{
		
	}

}
