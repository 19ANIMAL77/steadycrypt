/**
 * Date: 09.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob;


public class FilterFavorite {

	private String name;
	private String fileName;
	private String fileType;
	private String encryptionPeriod;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public FilterFavorite(String name, String fileName, String fileType, String encryptionPeriod)
	{
		this.name = name;
		this.fileName = fileName;
		this.fileType = fileType;
		this.encryptionPeriod = encryptionPeriod;
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
	public String getEncryptionPeriod() {
		return encryptionPeriod;
	}
	public void setEncryptionPeriod(String encryptionPeriod) {
		this.encryptionPeriod = encryptionPeriod;
	}

}
