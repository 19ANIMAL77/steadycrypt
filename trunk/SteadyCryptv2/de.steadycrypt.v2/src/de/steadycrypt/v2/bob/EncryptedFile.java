/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob;

import java.io.File;
import java.sql.Date;

public class EncryptedFile {

	private String name;
	private String type;
	private Long size;
	private Date date;
	private String path;
	private String file;
	
	public static final String encryptionPath = System.getProperty("user.dir")+"/sc-files/";

    // =========================================================================
	
	public EncryptedFile() {
		
	}
	
	/**
	 * Used when a new file was dropped.
	 * @param newFile
	 */
	public EncryptedFile(File newFile)
	{
		this.name = newFile.getName();
		this.type = defineFileType();
		this.size = newFile.length();
		this.date = new Date(System.currentTimeMillis());
		this.path = newFile.getPath();
		this.file = System.nanoTime()+".sc";
	}

	/**
	 * Used when the content table is being read.
	 * @param name
	 * @param type
	 * @param size
	 * @param date
	 * @param path
	 * @param file
	 */
	public EncryptedFile(String name, String type, long size, Date date, String path, String file)
	{
		this.name = name;
		this.type = type;
		this.size = size;
		this.date = date;
		this.path = path;
		this.file = file;
	}
	
    // =========================================================================

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Long getSize() {
		return size;
	}
	
	public void setSize(Long size) {
		this.size = size;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getFile()
	{
		return file;
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public String defineFileType()
	{
		String ext = (this.name.lastIndexOf(".")==-1)?"":this.name.substring(this.name.lastIndexOf(".")+1,this.name.length());

		String fileType = ext+"-File";
		
		return fileType;
	}

}
