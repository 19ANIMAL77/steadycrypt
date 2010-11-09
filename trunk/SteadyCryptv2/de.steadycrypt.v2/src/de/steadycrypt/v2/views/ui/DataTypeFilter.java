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

public class DataTypeFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element){
		
		//TODO: gewählter type aus SideBarView..->
		if(element instanceof EncryptedFileDob && ((EncryptedFileDob)element).getType().contains("_GEWÄHLTER_TYPE_")){
			return true;
		} else if (element instanceof EncryptedFolderDob){
			return find((DroppedElement) element, "_GEWÄHLTER_TYPE_");
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
			String contents = ((EncryptedFileDob) element).getType();
			
			if ((contents.contains(dataTypeString))) 
			{
				return found = true;
			}			
		}
		else if(element instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob folderToDecrypt = (EncryptedFolderDob)element;
						
			for(EncryptedFolderDob nextFolderToDecrypt : folderToDecrypt.getFolders())
			{
				found = found == true ? true : find(nextFolderToDecrypt, dataTypeString);
			}
			
			for(EncryptedFileDob nextFileToDecrypt : folderToDecrypt.getFiles())
			{
				found =  found == true ? true : find(nextFileToDecrypt, dataTypeString);
			}
			
			//TODO: Folder hat kein dataType
//			if ((contents.contains(searchString.toUpperCase()))) 
//			{
//				return found = true;
//			}
			return found = true;
			
		}
		
		return found;
	}
}
