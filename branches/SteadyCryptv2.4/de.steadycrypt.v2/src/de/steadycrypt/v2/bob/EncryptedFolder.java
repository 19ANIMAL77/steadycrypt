/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob;

import java.io.File;
import java.sql.Date;

import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;

public class EncryptedFolder extends DroppedElement {

	/**
	 * Used when a new folder was dropped.
	 * 
	 * @param newFile
	 * @param parent
	 */
	public EncryptedFolder(File newFile, EncryptedFolderDob parent) {
		super(newFile.getName(), new Date(System.currentTimeMillis()), newFile.getPath().replace("\\", "/"), parent);
	}

	/**
	 * Used when the content table is being read.
	 * 
	 * @param name
	 * @param date
	 * @param path
	 */
	public EncryptedFolder(String name, Date date, String path) {
		super(name, date, path);
	}

	/**
	 * Used when the content table is being read.
	 * 
	 * @param name
	 * @param date
	 * @param path
	 * @param parent
	 */
	public EncryptedFolder(String name, Date date, String path, EncryptedFolderDob parent) {
		super(name, date, path, parent);
	}

}
