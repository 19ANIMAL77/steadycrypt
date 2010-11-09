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

	private final String INSERT_FAVORITE = "INSERT INTO favorite (name, filename, filetype, fromdate, todate, minsize, maxsize) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private final String UPDATE_FAVORITE = "UPDATE favorite SET name=?, filename=?, filetype=?, fromdate=?, todate=?, minsize=?, maxsize=? WHERE id=?";
	private final String SELECT_FAVORITE = "SELECT id, name, filename, filetype, fromdate, todate, minsize, maxsize FROM favorite ORDER BY id";
	private final String DELETE_FAVORITE = "DELETE FROM favorite WHERE id=?";

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
				favorites.add(new FilterFavoriteDob(rs.getInt("id"), rs.getString("name"), rs.getString("filename"), rs.getString("filetype"), rs.getDate("fromdate"),
						rs.getDate("todate"), rs.getLong("minsize"), rs.getLong("maxsize")));
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
			pStmt.setDate(4, filterFavorite.getFromDate());
			pStmt.setDate(5, filterFavorite.getToDate());
			pStmt.setLong(6, filterFavorite.getMinSize());
			pStmt.setLong(7, filterFavorite.getMaxSize());
			
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
			pStmt.setDate(4, filterFavorite.getFromDate());
			pStmt.setDate(5, filterFavorite.getToDate());
			pStmt.setLong(6, filterFavorite.getMinSize());
			pStmt.setLong(7, filterFavorite.getMaxSize());
			pStmt.setInt(8, filterFavorite.getId());
			
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

}
