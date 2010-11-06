/**
 * Date: 26.10.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private List<EncryptedFolderDob> persistedNonDeletedFolders = new ArrayList<EncryptedFolderDob>();
	private List<EncryptedFolderDob> successfullyProcessedFolders = new ArrayList<EncryptedFolderDob>();
	private List<EncryptedFileDob> successfullyProcessedFiles = new ArrayList<EncryptedFileDob>();
	
	public FileDropHandler()
	{
		// Provide KeyManager
		log.debug("Get KeyManager-Instance");	
		this.keyman = KeyManager.getInstance();	
		// Provide Crypter
		log.debug("Get Crypter-Instance");	
		this.crypter = new Crypter(this.keyman.getKey());	
	}
	
	public void processData(String[] droppedFileInformation, EncryptedFolderDob parent) throws IOException
	{	
        for (int i = 0 ; i < droppedFileInformation.length ; i++)
        {
        	browseFolders(new File(droppedFileInformation[i]), parent);
        }
	}
	
	private void browseFolders (File droppedElement, EncryptedFolderDob parent) throws IOException
	{
    	if(!droppedElement.isDirectory())
    	{
    		System.out.println(droppedElement.canWrite());
    		log.debug("File dropped");			
    		EncryptedFile droppedFile = crypter.encrypt(droppedElement, parent);
    		log.debug("Encryption finished");
        	
    		boolean success = droppedElement.delete();
        	log.debug("Sourcefile deleted");
    		
    		if(success)
    		{
	    		EncryptedFileDob encryptedPersistedFile = this.encryptedFileDao.addFile(droppedFile);
	    		parent.addFile(encryptedPersistedFile);
	    		log.debug("File persisted");
	    		successfullyProcessedFiles.add(encryptedPersistedFile);
    		}
    	}
    	else
    	{
    		log.debug("Folder dropped");
			EncryptedFolder droppedFolder = new EncryptedFolder(droppedElement, parent);
			
			EncryptedFolderDob encryptedPersistedFolder = this.encryptedFolderDao.addFolder(droppedFolder);
			parent.addFolder(encryptedPersistedFolder);
			persistedNonDeletedFolders.add(encryptedPersistedFolder);
    		log.debug("Folder persisted");
			
    		for(File file : droppedElement.listFiles())
    		{
    			browseFolders(file, encryptedPersistedFolder);
    		}
    		
    		boolean success = droppedElement.delete();
        	log.debug("Sourcefolder deleted");
    		
        	if(success)
    		{
    			persistedNonDeletedFolders.remove(encryptedPersistedFolder);
    			successfullyProcessedFolders.add(encryptedPersistedFolder);
    		}
    	}		
	}
	
	private void rollback()
	{
		//TODO: Decryption fŸr bereits verarbeitete Files
	}
		
}
