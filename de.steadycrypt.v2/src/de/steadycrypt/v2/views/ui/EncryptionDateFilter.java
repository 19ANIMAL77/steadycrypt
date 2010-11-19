package de.steadycrypt.v2.views.ui;
/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

import java.sql.Date;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.views.SideBarView;

public class EncryptionDateFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if(SideBarView.encryptionDateFilterString.equals(Messages.Filter_NONE))
		{
			return true;
		}
		else if(element instanceof EncryptedFileDob)
		{
			return ((EncryptedFileDob)element).getDate().after(SideBarView.encryptionDateFilter);
		} 
		else if (element instanceof EncryptedFolderDob)
		{
			return find((DroppedElement) element, SideBarView.encryptionDateFilter);
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param element
	 * @param searchString
	 * @return boolean
	 */
	private boolean find(DroppedElement element, Date encryptionDateFilter)
	{
		boolean found = false;
		
		if(element instanceof EncryptedFileDob)
		{
			return found = ((EncryptedFileDob)element).getDate().after(SideBarView.encryptionDateFilter);
		}
		else if(element instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob folderToCheck = (EncryptedFolderDob)element;
						
			for(EncryptedFolderDob nextFolderToCheck : folderToCheck.getFolders())
			{
				found = found == true ? true : find(nextFolderToCheck, encryptionDateFilter);
			}
			
			for(EncryptedFileDob nextFileToCheck : folderToCheck.getFiles())
			{
				found =  found == true ? true : find(nextFileToCheck, encryptionDateFilter);
			}
			
			return ((EncryptedFolderDob)element).getDate().after(SideBarView.encryptionDateFilter);
		}
		
		return found;
	}
}
