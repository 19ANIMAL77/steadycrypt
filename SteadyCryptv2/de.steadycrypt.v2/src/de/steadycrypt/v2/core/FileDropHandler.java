/**
 * Date: 26.10.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.dao.EncryptedFileDao;

public class FileDropHandler {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(FileDropHandler.class);

	private KeyManager keyman;
	private Crypter crypter;
	
	public FileDropHandler()
	{
		// Provide KeyManager
		log.debug("Get KeyManager-Instance");	
		this.keyman = KeyManager.getInstance();	
		// Provide Crypter
		log.debug("Get Crypter-Instance");	
		this.crypter = new Crypter(this.keyman.getKey());	
	}
	
	public List<EncryptedFileDob> processData(String[] droppedFileInformation)
	{
		EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
		ArrayList<EncryptedFile> encryptedFiles = new ArrayList<EncryptedFile>();
		ArrayList<EncryptedFileDob> encryptedPersistedFiles = new ArrayList<EncryptedFileDob>();
		
        for (int i = 0 ; i < droppedFileInformation.length ; i++)
        {
        	encryptedFiles.add(crypter.encrypt(new File(droppedFileInformation[i])));
        	
			log.debug("Encryption finished");
        }
        
        encryptedPersistedFiles.addAll(encryptedFileDao.addFiles(encryptedFiles));
        
		return encryptedPersistedFiles;		
	}
		
}
