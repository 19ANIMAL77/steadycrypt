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
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.core.DbManager;

public class EncryptedFileDao {

	private final String INSERT_FILE = "INSERT INTO file (name, type, size, encryptiondate, originalpath, encryptedfilename, containingfolderid) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private final String UPDATE_FILE = "UPDATE file SET name=?, type=?, size=?, encryptiondate=?, originalpath=?, encryptedfilename=?, containingfolderid=? WHERE id=?";
	private final String SELECT_FILE = "SELECT id, name, type, size, encryptiondate, originalpath, encryptedfilename FROM file ORDER BY name";
	private final String SELECT_FILE_FOR_FOLDER = "SELECT id, name, type, size, encryptiondate, originalpath, encryptedfilename FROM file WHERE containingfolderid=";
	private final String SELECT_ROOT_FILES = "SELECT id, name, type, size, encryptiondate, originalpath, encryptedfilename FROM file WHERE containingfolderid=0  ORDER BY name";
	private final String DELETE_FILE = "DELETE FROM file WHERE id=?";
	private final String SELECT_FILE_TYPES = "SELECT DISTINCT type FROM file ORDER BY type";

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
				files.add(new EncryptedFileDob(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getLong("size"), rs.getDate("encryptiondate"),
						rs.getString("originalpath"), rs.getString("encryptedfilename")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return files;		
	}
	
	public List<EncryptedFileDob> getFilesForFolder(EncryptedFolderDob folder)
	{
		List<EncryptedFileDob> files = new ArrayList<EncryptedFileDob>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append(SELECT_FILE_FOR_FOLDER);
			sql.append(folder.getId());
			sql.append(" ORDER BY name");
			ResultSet rs = stmt.executeQuery(sql.toString());
			
			while(rs.next())
			{
				files.add(new EncryptedFileDob(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getLong("size"), rs.getDate("encryptiondate"),
						rs.getString("originalpath"), rs.getString("encryptedfilename")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return files;		
	}
	
	public List<EncryptedFileDob> getRootFiles()
	{
		List<EncryptedFileDob> files = new ArrayList<EncryptedFileDob>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_ROOT_FILES);
			
			while(rs.next())
			{
				files.add(new EncryptedFileDob(rs.getInt("id"), rs.getString("name"), rs.getString("type"), rs.getLong("size"), rs.getDate("encryptiondate"),
						rs.getString("originalpath"), rs.getString("encryptedfilename")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return files;		
	}

	public EncryptedFileDob addFile(EncryptedFile encryptedFile)
	{
		EncryptedFileDob encryptedFileDob = null;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(INSERT_FILE, Statement.RETURN_GENERATED_KEYS);

			pStmt.setString(1, encryptedFile.getName());
			pStmt.setString(2, encryptedFile.getType());
			pStmt.setLong(3, encryptedFile.getSize());
			pStmt.setDate(4, encryptedFile.getDate());
			pStmt.setString(5, encryptedFile.getPath());
			pStmt.setString(6, encryptedFile.getFile());
			pStmt.setInt(7, encryptedFile.getParent().getId());
			
			pStmt.execute();
			
			ResultSet rs = pStmt.getGeneratedKeys();
			
			while(rs.next())
			{
				encryptedFileDob = new EncryptedFileDob(rs.getInt(1), encryptedFile);
			}
			
			pStmt.close();
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return encryptedFileDob;
	}

	public List<EncryptedFileDob> addFiles(List<EncryptedFile> encryptedFiles)
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
				pStmt.setInt(7, encryptedFile.getParent().getId());
				
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

	public boolean updateFiles(List<EncryptedFileDob> encryptedFiles)
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
				pStmt.setInt(7, encryptedFile.getParent().getId());
				pStmt.setInt(8, encryptedFile.getId());
				
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

	public boolean deleteMultipleFiles(List<EncryptedFileDob> encryptedFiles)
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
	
	public List<String> getAllFileTypes()
	{
		List<String> fileTypes = new ArrayList<String>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_FILE_TYPES);
			
			while(rs.next())
			{
				fileTypes.add(rs.getString("type"));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return fileTypes;		
	}

}
