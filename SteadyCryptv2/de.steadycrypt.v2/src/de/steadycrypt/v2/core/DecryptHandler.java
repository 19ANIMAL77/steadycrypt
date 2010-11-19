/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

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
	
	private EncryptedFolderDao encryptedFolderDao = new EncryptedFolderDao();
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
    
	public DecryptHandler()
	{
		// Provide KeyManager
		this.keyman = KeyManager.getInstance();
		log.debug("Get KeyManager-Instance");
		// Provide Crypter
		this.crypter = new Crypter(keyman.getKey());
		log.debug("Crypt instance created");
	}
	
	public void processData(TreeSelection filesToDecrypt)
	{
		DroppedElement selectedElement = (DroppedElement)filesToDecrypt.getFirstElement();
		
		DirectoryDialog directoryDialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
		directoryDialog.setText(Messages.TableView_ExportFileDialog_Title);
		String path = directoryDialog.open();
		
		if(path != null)
		{
			try {
				browseFolders(selectedElement, path, true);
			} catch(IOException e) {
        		log.error(e.getMessage());
        		e.printStackTrace();
			}
		}
	}
	
	public void processData(List<DroppedElement> filesToDecrypt)
	{
		if(!filesToDecrypt.isEmpty())
		{
			DirectoryDialog directoryDialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
			directoryDialog.setText(Messages.TableView_ExportFileDialog_Title);
			String path = directoryDialog.open();
			
			if(path != null)
			{
				for(DroppedElement currentElement : filesToDecrypt)
				{
					try {
						browseFolders(currentElement, path, true);
					} catch(IOException e) {
		        		log.error(e.getMessage());
		        		e.printStackTrace();
					}
				}
			}
		}
	}

	public void browseFolders(DroppedElement elementToDecrypt, String destination, boolean rootFile) throws IOException
	{
		if(elementToDecrypt instanceof EncryptedFileDob)
		{
			EncryptedFileDob fileToDecrypt = (EncryptedFileDob)elementToDecrypt;
			log.debug("EncryptedFile handed over");
			crypter.decrypt(fileToDecrypt, destination);
			log.debug("File decrypted");
			
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
			
			File newSubDestination = new File(destination+"/"+folderToDecrypt.getName());
			boolean success = newSubDestination.exists();
			if(!success)
				success = newSubDestination.mkdir();
			
			if(success)
			{
				for(EncryptedFolderDob nextFolderToDecrypt : folderToDecrypt.getFolders())
				{
					browseFolders(nextFolderToDecrypt, newSubDestination.getPath(), false);
				}
				
				for(EncryptedFileDob nextFileToDecrypt : folderToDecrypt.getFiles())
				{
					browseFolders(nextFileToDecrypt, newSubDestination.getPath(), false);
				}
				
				encryptedFolderDao.deleteFolder(folderToDecrypt);
				log.debug("database entry deleted");
				if(rootFile)
					folderToDecrypt.getParent().removeFolder(folderToDecrypt);
			}
		}
	}

}
