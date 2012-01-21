/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob;

import java.io.File;
import java.sql.Date;

import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;

public class EncryptedFile extends DroppedElement {

	private String type;
	private Long size;
	private String scFileName;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	/**
	 * Used when a new file was dropped.
	 * @param newFile
	 * @param parent
	 */
	public EncryptedFile(File newFile, EncryptedFolderDob parent)
	{
		super(newFile.getName(), new Date(System.currentTimeMillis()), newFile.getPath().replace("\\", "/"), parent);
		this.type = defineFileType();
		this.size = newFile.length();
		this.scFileName = System.nanoTime()+".sc";
	}

	/**
	 * Used when the content table is being read.
	 * @param name
	 * @param type
	 * @param size
	 * @param date
	 * @param path
	 * @param scFileName
	 */
	public EncryptedFile(String name, String type, long size, Date date, String path, String scFileName)
	{
		super(name, date, path);
		this.type = type;
		this.size = size;
		this.scFileName = scFileName;
	}

	/**
	 * Used when the content table is being read.
	 * @param name
	 * @param type
	 * @param size
	 * @param date
	 * @param path
	 * @param scFileName
	 * @param parent
	 */
	public EncryptedFile(String name, String type, long size, Date date, String path, String scFileName, EncryptedFolderDob parent)
	{
		super(name, date, path, parent);
		this.type = type;
		this.size = size;
		this.scFileName = scFileName;
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

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
	
	public String getScFileName()
	{
		return scFileName;
	}
	
	public void setScFileName(String scFileName) {
		this.scFileName = scFileName;
	}
	
	public String defineFileType()
	{
		String ext = "";
		if(this.name.lastIndexOf(".") > 0) {
			ext = this.name.substring(this.name.lastIndexOf(".")+1,this.name.length());
		}
		
		return ext.toLowerCase();
	}

}
