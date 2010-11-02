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
	
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	private ArrayList<EncryptedFile> encryptedFiles = new ArrayList<EncryptedFile>();
	
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
		ArrayList<EncryptedFileDob> encryptedPersistedFiles = new ArrayList<EncryptedFileDob>();
		
        for (int i = 0 ; i < droppedFileInformation.length ; i++)
        {
        	browseFolders(new File(droppedFileInformation[i]));
        }
        
        encryptedPersistedFiles.addAll(encryptedFileDao.addFiles(encryptedFiles));
        
		return encryptedPersistedFiles;		
	}
	
	private void browseFolders (File droppedFile)
	{
    	if(!droppedFile.isDirectory())
    	{
    		log.debug("File dropped");
			log.debug(droppedFile.getPath());
//    		encryptedFiles.add(crypter.encrypt(droppedFile));
        	log.debug("Encryption finished");
        	
//        	droppedFile.delete();
//        	log.debug("Sourcefile deleted");
    	}
    	else
    	{
    		log.debug("Folder dropped");
			log.debug(droppedFile.getPath());
    		for(File file : droppedFile.listFiles())
    		{
//    			log.debug(file.getPath());
    			browseFolders(file);
    		}
    	}		
	}
		
}
