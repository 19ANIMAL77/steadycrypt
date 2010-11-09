/**
 * Date: 09.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob.dob;

import java.sql.Date;

import de.steadycrypt.v2.bob.FilterFavorite;

public class FilterFavoriteDob extends FilterFavorite {
	
	private int id;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public FilterFavoriteDob(int id, FilterFavorite filterfavorite)
	{
		super(filterfavorite.getName(), filterfavorite.getFilename(), filterfavorite.getFiletype(), filterfavorite.getFromDate(),
				filterfavorite.getToDate(), filterfavorite.getMinSize(), filterfavorite.getMaxSize());
		this.id = id;
	}
	
	public FilterFavoriteDob(int id, String name, String fileName, String fileType, Date fromDate, Date toDate, Long minSize, Long maxSize)
	{
		super(name, fileName, fileType, fromDate, toDate, minSize, maxSize);
		this.id = id;
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
