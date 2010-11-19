/**
 * Date: 16.11.2010
 * SteadyCrypt v2 Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.EncryptedFolderDao;

public class DeleteFileHandler {
	
	private static Logger log = Logger.getLogger(DeleteFileHandler.class);
	
	private EncryptedFolderDao encryptedFolderDao = new EncryptedFolderDao();
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	
	public void processData(List<DroppedElement> filesToDecrypt)
	{
		for(DroppedElement currentElement : filesToDecrypt)
		{
			try {
				browseFolders(currentElement, true);
			} catch(IOException e) {
        		log.error(e.getMessage());
        		e.printStackTrace();
			}
		}
	}

	public void browseFolders(DroppedElement elementToDecrypt, boolean rootFile) throws IOException
	{
		if(elementToDecrypt instanceof EncryptedFileDob)
		{
			EncryptedFileDob fileToDecrypt = (EncryptedFileDob)elementToDecrypt;
			log.debug("EncryptedFile handed over");
			
			File file = new File(Crypter.encryptionPath+(fileToDecrypt).getFile());
			boolean success = file.delete();
			log.debug("scFile deleted");
			
			if(success)
			{
				this.encryptedFileDao.deleteFile(fileToDecrypt);
				log.debug("database entry deleted");
				if(rootFile)
					(fileToDecrypt).getParent().removeFile(fileToDecrypt);
			}
		}
		else if(elementToDecrypt instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob folderToDecrypt = (EncryptedFolderDob)elementToDecrypt;
			log.debug("EncryptedFolder handed over");
			
			for(EncryptedFolderDob nextFolderToDecrypt : folderToDecrypt.getFolders())
			{
				browseFolders(nextFolderToDecrypt, false);
			}
			
			for(EncryptedFileDob nextFileToDecrypt : folderToDecrypt.getFiles())
			{
				browseFolders(nextFileToDecrypt, false);
			}
			
			encryptedFolderDao.deleteFolder(folderToDecrypt);
			log.debug("database entry deleted");
			if(rootFile)
				folderToDecrypt.getParent().removeFolder(folderToDecrypt);
		}
	}

}
