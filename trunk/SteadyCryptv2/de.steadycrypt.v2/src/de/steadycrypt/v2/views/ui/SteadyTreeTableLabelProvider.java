/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.ui;

import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;

public class SteadyTreeTableLabelProvider extends LabelProvider {
	
	private SimpleDateFormat sdf = new SimpleDateFormat(Messages.DATE_FORMAT);
	
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
        
        else if(element instanceof EncryptedFolderDob)
        {
	        switch(identifier)
	        {
	            case DATE:
	                text = sdf.format(((EncryptedFolderDob)element).getDate());
	                break;
	            case NAME:
	                text = ((EncryptedFolderDob)element).getName();
	                break;
	            case PATH:
	                text = ((EncryptedFolderDob)element).getPath();
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
	
	public String getColumnText(Object element)
    {
        String text = null;
        
        if(element instanceof EncryptedFolderDob)
        {
        	text = ((EncryptedFolderDob)element).getName();
        }
        
        else if(element instanceof EncryptedFileDob)
        {
        	text = ((EncryptedFileDob)element).getName();
        }
	        
        return text;
    }

    public Image getColumnImage(Object element, int columnIndex)
    {
        return null;
    }

}
