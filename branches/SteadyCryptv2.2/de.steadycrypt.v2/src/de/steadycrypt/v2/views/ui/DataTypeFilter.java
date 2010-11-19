package de.steadycrypt.v2.views.ui;
/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.views.SideBarView;

public class DataTypeFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if(SideBarView.fileTypeFilterString.equals(Messages.Filter_NONE))
		{
			return true;
		}
		else if(element instanceof EncryptedFileDob && ((EncryptedFileDob)element).getType().contains(SideBarView.fileTypeFilterString))
		{
			return true;
		} 
		else if (element instanceof EncryptedFolderDob)
		{
			return find((DroppedElement) element, SideBarView.fileTypeFilterString);
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param element
	 * @param searchString
	 * @return boolean
	 */
	private boolean find(DroppedElement element, String dataTypeString)
	{
		boolean found = false;
		
		if(element instanceof EncryptedFileDob)
		{
			if ((((EncryptedFileDob) element).getType().contains(dataTypeString))) 
			{
				return found = true;
			}			
		}
		else if(element instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob folderToCheck = (EncryptedFolderDob)element;
						
			for(EncryptedFolderDob nextFolderToCheck : folderToCheck.getFolders())
			{
				found = found == true ? true : find(nextFolderToCheck, dataTypeString);
			}
			
			for(EncryptedFileDob nextFileToCheck : folderToCheck.getFiles())
			{
				found =  found == true ? true : find(nextFileToCheck, dataTypeString);
			}
			
			if ((dataTypeString.equalsIgnoreCase(Messages.FileTypeFilter_FOLDER))) 
			{
				return found = true;
			}
		}
		
		return found;
	}
}
