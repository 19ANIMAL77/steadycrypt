package de.steadycrypt.v2.views.ui;
/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.views.SideBarView;

public class SearchFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element){
		
		if(element instanceof EncryptedFileDob && ((EncryptedFileDob)element).getName().toUpperCase().contains(SideBarView.fileNameFilterString.toUpperCase())){
			return true;
		} else if (element instanceof EncryptedFolderDob){
			return find((DroppedElement) element, SideBarView.fileNameFilterString.toUpperCase());
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param element
	 * @param searchString
	 * @return boolean
	 */
	private boolean find(DroppedElement element, String searchString)
	{
		boolean found = false;
		String contents = element.getName().toUpperCase();
		
		if(element instanceof EncryptedFileDob)
		{
			if (contents.contains(searchString)) 
			{
				return found = true;
			}			
		}
		else if(element instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob folderToCheck = (EncryptedFolderDob)element;
						
			for(EncryptedFolderDob nextFolderToCheck : folderToCheck.getFolders())
			{
				found = found == true ? true : find(nextFolderToCheck, searchString);
			}
			
			for(EncryptedFileDob nextFileToCheck : folderToCheck.getFiles())
			{
				found =  found == true ? true : find(nextFileToCheck, searchString);
			}
			
			if (contents.contains(searchString)) 
			{
				return found = true;
			}	
		}
		
		return found;
	}
}
