/**
 * Date: 09.11.2010
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

import de.steadycrypt.v2.bob.FilterFavorite;
import de.steadycrypt.v2.bob.dob.FilterFavoriteDob;
import de.steadycrypt.v2.core.DbManager;

public class FilterFavoriteDao {

	private final String INSERT_FAVORITE = "INSERT INTO favorite (name, filename, filetype, encryptionperiod) VALUES (?, ?, ?, ?)";
	private final String UPDATE_FAVORITE = "UPDATE favorite SET name=?, filename=?, filetype=?, encryptionperiod=? WHERE id=?";
	private final String SELECT_FAVORITE = "SELECT id, name, filename, filetype, encryptionperiod FROM favorite ORDER BY id";
	private final String DELETE_FAVORITE = "DELETE FROM favorite WHERE id=?";
	private final String CHECK_FAVORITE = "SELECT id, name, filename, filetype, encryptionperiod FROM favorite WHERE name LIKE ?";

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public List<FilterFavoriteDob> getFavorites()
	{
		List<FilterFavoriteDob> favorites = new ArrayList<FilterFavoriteDob>();
		try
		{
			Connection connection = DbManager.getConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(SELECT_FAVORITE);
			
			while(rs.next())
			{
				favorites.add(new FilterFavoriteDob(rs.getInt("id"), rs.getString("name"), rs.getString("filename"), rs.getString("filetype"), rs.getString("encryptionperiod")));
			}
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return favorites;		
	}

	public FilterFavoriteDob addFavorite(FilterFavorite filterFavorite)
	{
		FilterFavoriteDob filterFavoriteDob = null;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(INSERT_FAVORITE, Statement.RETURN_GENERATED_KEYS);

			pStmt.setString(1, filterFavorite.getName());
			pStmt.setString(2, filterFavorite.getFilename());
			pStmt.setString(3, filterFavorite.getFiletype());
			pStmt.setString(4, filterFavorite.getEncryptionPeriod());
			
			pStmt.execute();
			
			ResultSet rs = pStmt.getGeneratedKeys();
			
			while(rs.next())
			{
				filterFavoriteDob = new FilterFavoriteDob(rs.getInt(1), filterFavorite);
			}
			
			pStmt.close();
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return filterFavoriteDob;
	}

	public boolean updateFavorite(FilterFavoriteDob filterFavorite)
	{
		boolean successful = false;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(UPDATE_FAVORITE);

			pStmt.setString(1, filterFavorite.getName());
			pStmt.setString(2, filterFavorite.getFilename());
			pStmt.setString(3, filterFavorite.getFiletype());
			pStmt.setString(4, filterFavorite.getEncryptionPeriod());
			pStmt.setInt(5, filterFavorite.getId());
			
			successful = pStmt.executeUpdate() > 0 ? true : false;
			
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return successful;
	}

	public boolean deleteFavorite(FilterFavoriteDob filterFavorite)
	{
		boolean successful = false;
		
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(DELETE_FAVORITE);
			
			pStmt.setInt(1, filterFavorite.getId());
			
			successful = pStmt.executeUpdate() > 0 ? true : false;
			
			connection.commit();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return successful;
	}

	/**
	 * @param filterName
	 * @return boolean
	 */
	public boolean allreadyExists(String filterName)
	{
		boolean found = false;
		try
		{
			Connection connection = DbManager.getConnection();
			PreparedStatement pStmt = connection.prepareStatement(CHECK_FAVORITE);

			pStmt.setString(1, filterName);
			
			ResultSet rs = pStmt.executeQuery();
			
			found = rs.next();
		}
		catch (SQLException e)
		{
			DbManager.printSQLException(e);
		}
		
		return found;	
	}

}
