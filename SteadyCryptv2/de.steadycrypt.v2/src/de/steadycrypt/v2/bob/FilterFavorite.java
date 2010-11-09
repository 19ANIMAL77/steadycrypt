/**
 * Date: 09.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob;

import java.sql.Date;

public class FilterFavorite {

	private String name;
	private String fileName;
	private String fileType;
	private Date fromDate;
	private Date toDate;
	private Long minSize;
	private Long maxSize;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public FilterFavorite(String name, String fileName, String fileType, Date fromDate, Date toDate, Long minSize, Long maxSize)
	{
		this.name = name;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFilename() {
		return fileName;
	}
	public void setFilename(String filename) {
		this.fileName = filename;
	}
	public String getFiletype() {
		return fileType;
	}
	public void setFiletype(String filetype) {
		this.fileType = filetype;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public Long getMinSize() {
		return minSize;
	}
	public void setMinSize(Long minSize) {
		this.minSize = minSize;
	}
	public Long getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(Long maxSize) {
		this.maxSize = maxSize;
	}

}
