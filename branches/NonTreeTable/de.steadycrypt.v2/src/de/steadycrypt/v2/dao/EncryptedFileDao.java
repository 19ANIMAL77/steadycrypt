/**
 * Date: 31.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.core.DbManager;

public class EncryptedFileDao {

//	private final String INSERT_FILE_STMT = "INSERT INTO CONTENT (name, type, size, enc_date, org_path, enc_name) VALUES (";
	private final String INSERT_FILE = "INSERT INTO CONTENT (name, type, size, enc_date, org_path, enc_name) VALUES (?, ?, ?, ?, ?, ?)";
	private final String UPDATE_FILE = "UPDATE CONTENT SET name=?, type=?, size=?, enc_date=?, org_path=?, enc_name=? WHERE id=?";
	private final String SELECT_FILE = "SELECT id, name, type, size, enc_date, org_path, enc_name FROM content ORDER BY id DESC";
	private final String DELETE_FILE = "DELETE FROM content WHERE id=?";

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public List<EncryptedFileDob> getAllFiles()
	{
		List<EncryptedFileDob> files = new ArrayList<EncryptedFileDob>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_FILE);
			
			while(rs.next())
			{
				files.add(new EncryptedFileDob(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getLong("size"), rs.getDate("enc_date"),
						rs.getString("org_path"), rs.getString("enc_name")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return files;		
	}

	public List<EncryptedFileDob> addFiles(ArrayList<EncryptedFile> encryptedFiles)
	{
		List<EncryptedFileDob> encryptedFileDobs = new ArrayList<EncryptedFileDob>();
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(INSERT_FILE, Statement.RETURN_GENERATED_KEYS);

			for (EncryptedFile encryptedFile : encryptedFiles)
			{
				pStmt.setString(1, encryptedFile.getName());
				pStmt.setString(2, encryptedFile.getType());
				pStmt.setLong(3, encryptedFile.getSize());
				pStmt.setDate(4, encryptedFile.getDate());
				pStmt.setString(5, encryptedFile.getPath());
				pStmt.setString(6, encryptedFile.getFile());
				
				pStmt.execute();
				
				ResultSet rs = pStmt.getGeneratedKeys();
				
				while(rs.next())
				{
					encryptedFileDobs.add(new EncryptedFileDob(rs.getInt(1), encryptedFile));
				}
			}
			
			pStmt.close();
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return encryptedFileDobs;
	}

	public boolean updateFiles(ArrayList<EncryptedFileDob> encryptedFiles)
	{
		boolean successful = false;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(UPDATE_FILE);

			for (EncryptedFileDob encryptedFile : encryptedFiles)
			{
				pStmt.setString(1, encryptedFile.getName());
				pStmt.setString(2, encryptedFile.getType());
				pStmt.setLong(3, encryptedFile.getSize());
				pStmt.setDate(4, encryptedFile.getDate());
				pStmt.setString(5, encryptedFile.getPath());
				pStmt.setString(6, encryptedFile.getFile());
				pStmt.setInt(7, encryptedFile.getId());
				
				successful = pStmt.executeUpdate() > 0 ? true : false;
			}
			
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return successful;
	}

	public boolean deleteFile(EncryptedFileDob encryptedFile)
	{
		boolean successful = false;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(DELETE_FILE);
			
			pStmt.setInt(1, encryptedFile.getId());
			
			successful = pStmt.executeUpdate() > 0 ? true : false;
			
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return successful;
	}

	public boolean deleteMultipleFiles(ArrayList<EncryptedFileDob> encryptedFiles)
	{
		boolean successful = false;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(DELETE_FILE);
			
			for (EncryptedFileDob encryptedFile : encryptedFiles)
			{
				pStmt.setInt(1, encryptedFile.getId());
				
				successful = pStmt.executeUpdate() > 0 ? true : false;
				
				if (!successful) break;
			}
			
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return successful;
	}

}
