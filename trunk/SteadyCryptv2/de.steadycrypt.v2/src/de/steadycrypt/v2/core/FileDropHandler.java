/**
 * Date: 26.10.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.File;

import org.apache.log4j.Logger;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.EncryptedFolder;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.EncryptedFolderDao;

public class FileDropHandler {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(FileDropHandler.class);

	private KeyManager keyman;
	private Crypter crypter;

	private EncryptedFolderDao encryptedFolderDao = new EncryptedFolderDao();
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	
	public FileDropHandler()
	{
		// Provide KeyManager
		log.debug("Get KeyManager-Instance");	
		this.keyman = KeyManager.getInstance();	
		// Provide Crypter
		log.debug("Get Crypter-Instance");	
		this.crypter = new Crypter(this.keyman.getKey());	
	}
	
	public void processData(String[] droppedFileInformation, EncryptedFolderDob parent)
	{	
        for (int i = 0 ; i < droppedFileInformation.length ; i++)
        {
        	browseFolders(new File(droppedFileInformation[i]), parent);
        }
	}
	
	private void browseFolders (File droppedElement, EncryptedFolderDob parent)
	{
    	if(!droppedElement.isDirectory())
    	{
    		log.debug("File dropped");
			log.debug(droppedElement.getPath());
    		EncryptedFile droppedFile = crypter.encrypt(droppedElement, parent);
    		
    		EncryptedFileDob encryptedPersistedFile = this.encryptedFileDao.addFile(droppedFile);
    		parent.addFile(encryptedPersistedFile);
        	
    		log.debug("Encryption finished");
        	
//        	droppedFile.delete();
//        	log.debug("Sourcefile deleted");
    	}
    	else
    	{
    		log.debug("Folder dropped");
			log.debug(droppedElement.getPath());
			EncryptedFolder droppedFolder = new EncryptedFolder(droppedElement, parent);
			
			EncryptedFolderDob encryptedPersistedFolder = this.encryptedFolderDao.addFolder(droppedFolder);
			parent.addFolder(encryptedPersistedFolder);
			
    		for(File file : droppedElement.listFiles())
    		{
//    			log.debug(file.getPath());
    			browseFolders(file, encryptedPersistedFolder);
    		}
    	}		
	}
		
}
