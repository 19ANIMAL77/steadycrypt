/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeSelection;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.EncryptedFolderDao;

public class DecryptHandler {
	
	private static Logger log = Logger.getLogger(DecryptHandler.class);
	
	private KeyManager keyman;
	private Crypter crypter;
	private IProgressMonitor monitor;
	
	private EncryptedFolderDao encryptedFolderDao = new EncryptedFolderDao();
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();

	private List<EncryptedFolderDob> successfulDecryptedFolders = new ArrayList<EncryptedFolderDob>();
	private List<EncryptedFileDob> successfulDecryptedFiles = new ArrayList<EncryptedFileDob>();
    
	public DecryptHandler()
	{
		// Provide KeyManager
		this.keyman = KeyManager.getInstance();
		log.debug("Get KeyManager-Instance");
		// Provide Crypter
		this.crypter = new Crypter(keyman.getKey());
		log.debug("Crypt instance created");
	}
	
	@SuppressWarnings("unchecked")
	public void processData(TreeSelection filesToDecrypt, String path, IProgressMonitor monitor)
	{
		this.monitor = monitor;
		Iterator<DroppedElement> selectedElementsIterator = filesToDecrypt.iterator();

		while(selectedElementsIterator.hasNext()) {
			try {
				browseFolders(selectedElementsIterator.next(), path, true);
        		monitor.worked(1);
			} catch(IOException e) {
        		log.error(e.getMessage());
        	}
        }
		successfulDecryptedFolders.clear();
		successfulDecryptedFiles.clear();
		
		// Opens the explorer/finder by using the extraction path
		Desktop desktop = Desktop.getDesktop();
        try {
        	desktop.open(new File(path));
        } catch(IOException e) {
            e.printStackTrace();
        }		
	}
	
	public void processData(DroppedElement filesToDecrypt, IProgressMonitor monitor)
	{
		this.monitor = monitor;
		String path = filesToDecrypt.getPath().substring(0, filesToDecrypt.getPath().lastIndexOf("/"));
		File destination = new File(path);
		if(!destination.exists())
			destination.mkdir();
		
		System.out.println(path);
		try {
			browseFolders(filesToDecrypt, path, true);
    		monitor.worked(1);
		} catch(IOException e) {
    		log.error(e.getMessage());
    	}
    	successfulDecryptedFolders.clear();
		successfulDecryptedFiles.clear();
		
		// Opens the explorer/finder by using the extraction path
		Desktop desktop = Desktop.getDesktop();
        try {
        	desktop.open(destination);
        } catch(IOException e) {
            e.printStackTrace();
        }		
	}

	public void browseFolders(DroppedElement elementToDecrypt, String destination, boolean rootFile) throws IOException
	{
		if(elementToDecrypt instanceof EncryptedFileDob)
		{
			monitor.subTask(elementToDecrypt.getName());
			EncryptedFileDob fileToDecrypt = (EncryptedFileDob)elementToDecrypt;
			if(successfulDecryptedFiles.contains(fileToDecrypt))
				return;
			log.debug("EncryptedFile handed over");
			crypter.decrypt(fileToDecrypt, destination);
			log.debug("File decrypted");
			
			File file = new File(Messages.getScFolder()+(fileToDecrypt).getScFileName());
			boolean success = file.delete();
			log.debug("scFile deleted");
			
			if(success)
			{
				successfulDecryptedFiles.add(fileToDecrypt);
				this.encryptedFileDao.deleteFile(fileToDecrypt);
				log.debug("database entry deleted");
				if(rootFile)
					(fileToDecrypt).getParent().removeFile(fileToDecrypt);
			}
		}
		else if(elementToDecrypt instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob folderToDecrypt = (EncryptedFolderDob)elementToDecrypt;
			if(successfulDecryptedFolders.contains(folderToDecrypt))
				return;
			log.debug("EncryptedFolder handed over");
			
			File newSubDestination = new File(destination+"/"+folderToDecrypt.getName());
			if(!newSubDestination.exists())
				newSubDestination.mkdir();
			
			for(EncryptedFolderDob nextFolderToDecrypt : folderToDecrypt.getFolders())
			{
				browseFolders(nextFolderToDecrypt, newSubDestination.getPath(), false);
			}
			
			for(EncryptedFileDob nextFileToDecrypt : folderToDecrypt.getFiles())
			{
				browseFolders(nextFileToDecrypt, newSubDestination.getPath(), false);
			}
			
			successfulDecryptedFolders.add(folderToDecrypt);
			encryptedFolderDao.deleteFolder(folderToDecrypt);
			log.debug("database entry deleted");
			if(rootFile)
				folderToDecrypt.getParent().removeFolder(folderToDecrypt);
		}
	}

}
