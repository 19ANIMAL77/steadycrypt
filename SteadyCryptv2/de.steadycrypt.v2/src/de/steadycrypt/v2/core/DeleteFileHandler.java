/**
 * Date: 16.11.2010
 * SteadyCrypt v2 Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeSelection;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.EncryptedFolderDao;

public class DeleteFileHandler {
	
	private static Logger log = Logger.getLogger(DeleteFileHandler.class);
	
	private IProgressMonitor monitor;
	
	private EncryptedFolderDao encryptedFolderDao = new EncryptedFolderDao();
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	
	@SuppressWarnings("unchecked")
	public void processData(TreeSelection filesToDelete, IProgressMonitor monitor)
	{
		this.monitor = monitor;
		Iterator<DroppedElement> droppedElementsIterator = filesToDelete.iterator();
		
		while(droppedElementsIterator.hasNext())
		{
			try {
				browseFolders(droppedElementsIterator.next(), true);
				monitor.worked(1);
			} catch(IOException e) {
        		log.error(e.getMessage());
        	}
        }
	}

	public void browseFolders(DroppedElement elementToDelete, boolean rootFile) throws IOException
	{
		if(elementToDelete instanceof EncryptedFileDob)
		{
			monitor.subTask(elementToDelete.getName());
			EncryptedFileDob fileToDelete = (EncryptedFileDob)elementToDelete;
			log.debug("EncryptedFile handed over");
			
			File file = new File(Messages.getScFolder()+(fileToDelete).getScFileName());
			boolean success = file.delete();
			log.debug("scFile deleted");
			
			if(success)
			{
				this.encryptedFileDao.deleteFile(fileToDelete);
				log.debug("database entry deleted");
				if(rootFile)
					(fileToDelete).getParent().removeFile(fileToDelete);
			}
		}
		else if(elementToDelete instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob folderToDelete = (EncryptedFolderDob)elementToDelete;
			log.debug("EncryptedFolder handed over");
			
			for(EncryptedFolderDob nextFolderToDecrypt : folderToDelete.getFolders())
			{
				browseFolders(nextFolderToDecrypt, false);
			}
			
			for(EncryptedFileDob nextFileToDecrypt : folderToDelete.getFiles())
			{
				browseFolders(nextFileToDecrypt, false);
			}
			
			encryptedFolderDao.deleteFolder(folderToDelete);
			log.debug("database entry deleted");
			if(rootFile)
				folderToDelete.getParent().removeFolder(folderToDelete);
		}
	}

}
