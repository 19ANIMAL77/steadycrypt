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
	private String file;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	/**
	 * Used when a new file was dropped.
	 * @param newFile
	 * @param parent
	 */
	public EncryptedFile(File newFile, EncryptedFolderDob parent)
	{
		super(newFile.getName(), new Date(System.currentTimeMillis()), newFile.getPath(), parent);
		this.type = defineFileType();
		this.size = newFile.length();
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
		super(name, date, path);
		this.type = type;
		this.size = size;
		this.file = file;
	}

	/**
	 * Used when the content table is being read.
	 * @param name
	 * @param type
	 * @param size
	 * @param date
	 * @param path
	 * @param file
	 * @param parent
	 */
	public EncryptedFile(String name, String type, long size, Date date, String path, String file, EncryptedFolderDob parent)
	{
		super(name, date, path, parent);
		this.type = type;
		this.size = size;
		this.file = file;
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

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
//	public void accept(IDroppedElementVisitor visitor, Object passAlongArgument)
//	{
//		visitor.visitFile(this, passAlongArgument);
//	}

}
