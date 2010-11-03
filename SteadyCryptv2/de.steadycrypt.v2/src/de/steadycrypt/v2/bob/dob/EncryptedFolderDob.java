/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob.dob;

import java.sql.Date;

import de.steadycrypt.v2.bob.EncryptedFolder;

public class EncryptedFolderDob extends EncryptedFolder {

	private int id;

    // =========================================================================

	/**
	 * Used when new folder was added.
	 */
	public EncryptedFolderDob(int id, EncryptedFolder encryptedFolder)
	{
		super(encryptedFolder.getName(), encryptedFolder.getDate(), encryptedFolder.getPath());
		this.id = id;
	}
	
	/**
	 * Used when the content table is being read.
	 * @param id
	 * @param name
	 * @param date
	 * @param path
	 */
	public EncryptedFolderDob(int id, String name, Date date, String path)
	{
		super(name, date, path);
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
