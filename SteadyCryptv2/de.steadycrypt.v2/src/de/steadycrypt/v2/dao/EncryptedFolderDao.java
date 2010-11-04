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

import de.steadycrypt.v2.bob.EncryptedFolder;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.core.DbManager;

public class EncryptedFolderDao {

	private final String INSERT_FOLDER = "INSERT INTO folder (name, encryptiondate, originalpath, containingfolderid) VALUES (?, ?, ?, ?)";
	private final String UPDATE_FOLDER = "UPDATE folder SET name=?, encryptiondate=?, originalpath=?, containingfolderid=? WHERE id=?";
	private final String SELECT_FOLDER = "SELECT id, name, encryptiondate, originalpath FROM folder ORDER BY name DESC";
	private final String SELECT_FOLDER_FOR_FOLDER = "SELECT id, name, encryptiondate, originalpath FROM folder WHERE containingfolderid=";
	private final String SELECT_ROOT_FOLDERS = "SELECT id, name, encryptiondate, originalpath FROM folder WHERE containingfolderid=0  ORDER BY name DESC";
	private final String DELETE_FOLDER = "DELETE FROM folder WHERE id=?";

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public List<EncryptedFolderDob> getAllFolders()
	{
		List<EncryptedFolderDob> folders = new ArrayList<EncryptedFolderDob>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_FOLDER);
			
			while(rs.next())
			{
				folders.add(new EncryptedFolderDob(rs.getInt("id"), rs.getString("name"), rs.getDate("encryptiondate"), rs.getString("originalpath")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return folders;		
	}
	
	public List<EncryptedFolderDob> getFoldersForFolder(EncryptedFolderDob folder)
	{
		List<EncryptedFolderDob> folders = new ArrayList<EncryptedFolderDob>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append(SELECT_FOLDER_FOR_FOLDER);
			sql.append(folder.getId());
			sql.append(" ORDER BY name DESC");
			ResultSet rs = stmt.executeQuery(sql.toString());
			
			while(rs.next())
			{
				folders.add(new EncryptedFolderDob(rs.getInt("id"), rs.getString("name"), rs.getDate("encryptiondate"), rs.getString("originalpath")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return folders;		
	}
	
	public List<EncryptedFolderDob> getRootFolders()
	{
		List<EncryptedFolderDob> folders = new ArrayList<EncryptedFolderDob>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_ROOT_FOLDERS);
			
			while(rs.next())
			{
				folders.add(new EncryptedFolderDob(rs.getInt("id"), rs.getString("name"), rs.getDate("encryptiondate"), rs.getString("originalpath")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return folders;		
	}

	public EncryptedFolderDob addFolder(EncryptedFolder encryptedFolder)
	{
		EncryptedFolderDob encryptedFolderDob = null;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(INSERT_FOLDER, Statement.RETURN_GENERATED_KEYS);

			pStmt.setString(1, encryptedFolder.getName());
			pStmt.setDate(2, encryptedFolder.getDate());
			pStmt.setString(3, encryptedFolder.getPath());
			pStmt.setInt(4, encryptedFolder.getParent().getId());
			
			pStmt.execute();
			
			ResultSet rs = pStmt.getGeneratedKeys();
				
			while(rs.next())
			{
				encryptedFolderDob = new EncryptedFolderDob(rs.getInt(1), encryptedFolder);
			}
			
			pStmt.close();
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return encryptedFolderDob;
	}

	public boolean updateFolders(List<EncryptedFolderDob> encryptedFolders)
	{
		boolean successful = false;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(UPDATE_FOLDER);

			for (EncryptedFolderDob encryptedFolder : encryptedFolders)
			{
				pStmt.setString(1, encryptedFolder.getName());
				pStmt.setDate(2, encryptedFolder.getDate());
				pStmt.setString(3, encryptedFolder.getPath());
				pStmt.setInt(4, encryptedFolder.getParent().getId());
				pStmt.setInt(5, encryptedFolder.getId());
				
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

	public boolean deleteFolder(EncryptedFolderDob encryptedFolder)
	{
		boolean successful = false;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(DELETE_FOLDER);
			
			pStmt.setInt(1, encryptedFolder.getId());
			
			successful = pStmt.executeUpdate() > 0 ? true : false;
			
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return successful;
	}

	public boolean deleteMultipleFolders(List<EncryptedFolderDob> encryptedFolders)
	{
		boolean successful = false;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(DELETE_FOLDER);
			
			for (EncryptedFolderDob encryptedFolder : encryptedFolders)
			{
				pStmt.setInt(1, encryptedFolder.getId());
				
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
