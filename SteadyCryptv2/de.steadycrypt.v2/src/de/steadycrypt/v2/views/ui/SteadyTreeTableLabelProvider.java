/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.ui;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.steadycrypt.v2.Activator;
import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.EncryptedFolder;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;

public class SteadyTreeTableLabelProvider implements ITableLabelProvider {
	
	private SimpleDateFormat sdf = new SimpleDateFormat(Messages.DATE_FORMAT);

	@Override
	public String getColumnText(Object element, int columnIndex)
	{
        SteadyTableIdentifier identifier = SteadyTableIdentifier.values()[columnIndex];
        String text = null;
        
        if(element instanceof EncryptedFileDob)
        {
	        switch(identifier)
	        {
	            case DATE:
	                text = sdf.format(((EncryptedFileDob)element).getDate());
	                break;
	            case NAME:
	                text = ((EncryptedFileDob)element).getName();
	                break;
	            case PATH:
	                text = ((EncryptedFileDob)element).getPath();
	                break;
	            case SIZE:
	                text = Long.toString(((EncryptedFileDob)element).getSize());
	                break;
	            case TYPE:
	                text = ((EncryptedFileDob)element).getType();
	                break;
	                
	            default:
	                assert false : identifier + " is not a legal identifier!"; //$NON-NLS-1$
	        }
        }
        
        else if(element instanceof EncryptedFolder)
        {
	        switch(identifier)
	        {
	            case DATE:
	                text = sdf.format(((EncryptedFolder)element).getDate());
	                break;
	            case NAME:
	                text = ((EncryptedFolder)element).getName();
	                break;
	            case PATH:
	                text = ((EncryptedFolder)element).getPath();
	                break;
	            case SIZE:
	                text = "";
	                break;
	            case TYPE:
	                text = "";
	                break;
	                
	            default:
	                assert false : identifier + " is not a legal identifier!"; //$NON-NLS-1$
	        }
        }
	        
        return text;
	}

	public Image getColumnImage(Object element, int columnIndex)
	{
		if(columnIndex == 0)
		{
			if(element instanceof EncryptedFolderDob)
			{
				return Activator.getImageDescriptor("icons/folder.png").createImage();
			}
			else if(element instanceof EncryptedFileDob)
			{
				if(((EncryptedFileDob)element).getType().contains("doc"))
					return Activator.getImageDescriptor("icons/file-doc.png").createImage();
				else if(((EncryptedFileDob)element).getType().contains("pdf"))
					return Activator.getImageDescriptor("icons/file-pdf.png").createImage();
				else if(((EncryptedFileDob)element).getType().contains("ppt"))
					return Activator.getImageDescriptor("icons/file-ppt.png").createImage();
				else if(((EncryptedFileDob)element).getType().contains("txt"))
					return Activator.getImageDescriptor("icons/file-txt.png").createImage();
				return Activator.getImageDescriptor("icons/file.png").createImage();
			}
		}
		return null;
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	
}
