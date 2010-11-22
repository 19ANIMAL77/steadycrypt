/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.ui;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.steadycrypt.v2.Activator;
import de.steadycrypt.v2.Messages;
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
	            	//TODO: Konfigurierbar machen?
	                text = ((EncryptedFileDob)element).getName().substring(0, ((EncryptedFileDob)element).getName().lastIndexOf("."));
	                break;
	            case SIZE:
	                BigDecimal size = new BigDecimal(((EncryptedFileDob)element).getSize()/1024);
	                text = size.compareTo(new BigDecimal(1000)) > 0 ? size.divide(new BigDecimal(1024)).setScale(2,BigDecimal.ROUND_HALF_UP).toString() +" MB" : size.setScale(0,BigDecimal.ROUND_HALF_UP).toString() + " KB";
	                break;
	            case TYPE:
	                text = ((EncryptedFileDob)element).getType() + Messages.FILE;
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
		
		String fileType = "";
		
		if(columnIndex == 0)
		{
			if(element instanceof EncryptedFolderDob)
			{
				return Activator.getImageDescriptor("icons/folder.png").createImage();
			}
			else if(element instanceof EncryptedFileDob)
			{
				fileType = ((EncryptedFileDob) element).getType();
//				fileType = fileType.substring(0, fileType.lastIndexOf("-"));
				
				try
				{
					return Activator.getImageDescriptor("icons/file-"+fileType+".png").createImage();
				}
				catch (Exception e) 
				{
					if(((EncryptedFileDob)element).getType().contains("html"))
						return Activator.getImageDescriptor("icons/file-htm.png").createImage();
					
					else if(((EncryptedFileDob)element).getType().contains("docx") || 
							((EncryptedFileDob)element).getType().contains("pages") || 
							((EncryptedFileDob)element).getType().contains("odt"))
						return Activator.getImageDescriptor("icons/file-doc.png").createImage();
					
					else if(((EncryptedFileDob)element).getType().contains("xlsx") || 
							((EncryptedFileDob)element).getType().contains("numbers") || 
							((EncryptedFileDob)element).getType().contains("ods"))
						return Activator.getImageDescriptor("icons/file-xls.png").createImage();
					
					else if(((EncryptedFileDob)element).getType().contains("pptx") || 
							((EncryptedFileDob)element).getType().contains("key") || 
							((EncryptedFileDob)element).getType().contains("odp"))
						return Activator.getImageDescriptor("icons/file-ppt.png").createImage();
					
					else if(((EncryptedFileDob)element).getType().contains("pptx"))
						return Activator.getImageDescriptor("icons/file-ppt.png").createImage();
					
					else
						return Activator.getImageDescriptor("icons/file.png").createImage();
				}
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
