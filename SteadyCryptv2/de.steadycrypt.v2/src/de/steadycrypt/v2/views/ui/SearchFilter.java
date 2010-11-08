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
		
		if(element instanceof EncryptedFileDob && ((EncryptedFileDob)element).getName().toUpperCase().contains(SideBarView.searchString.toUpperCase())){
			return true;
		} else if (element instanceof EncryptedFolderDob){
			return find((DroppedElement) element, SideBarView.searchString);
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
			if ((contents.contains(searchString.toUpperCase()))) 
			{
				return found = true;
			}			
		}
		else if(element instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob folderToDecrypt = (EncryptedFolderDob)element;
						
			for(EncryptedFolderDob nextFolderToDecrypt : folderToDecrypt.getFolders())
			{
				found = found == true ? true : find(nextFolderToDecrypt, searchString);
			}
			
			for(EncryptedFileDob nextFileToDecrypt : folderToDecrypt.getFiles())
			{
				found =  found == true ? true : find(nextFileToDecrypt, searchString);
			}
			
			if ((contents.contains(searchString.toUpperCase()))) 
			{
				return found = true;
			}	
		}
		
		return found;
	}
}
