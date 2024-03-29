/**
 * Date: 01.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob.dob;

import java.sql.Date;

import de.steadycrypt.v2.bob.EncryptedFile;

public class EncryptedFileDob extends EncryptedFile{
	
	private int id;

    // =========================================================================

	/**
	 * Used when new file was added.
	 */
	public EncryptedFileDob(int id, EncryptedFile encryptedFile)
	{
		super(encryptedFile.getName(), encryptedFile.getType(), encryptedFile.getSize(),
				encryptedFile.getDate(), encryptedFile.getPath(), encryptedFile.getScFileName(),
				encryptedFile.getParent());
		this.id = id;
	}
	
	/**
	 * Used when the content table is being read.
	 * @param id
	 * @param name
	 * @param type
	 * @param size
	 * @param date
	 * @param path
	 * @param file
	 */
	public EncryptedFileDob(int id, String name, String type, long size, Date date, String path, String file)
	{
		super(name, type, size, date, path, file);
		this.id = id;
	}

    // =========================================================================

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
