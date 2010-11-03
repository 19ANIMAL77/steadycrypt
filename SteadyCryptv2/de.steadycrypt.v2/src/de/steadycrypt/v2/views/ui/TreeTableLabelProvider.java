package de.steadycrypt.v2.views.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.EncryptedFolder;

public class TreeTableLabelProvider extends LabelProvider {	
	private Map imageCache = new HashMap(11);
	
	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {

		return null;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof EncryptedFolder) {
			if(((EncryptedFolder)element).getName() == null) {
				return "Folder";
			} else {
				return ((EncryptedFolder)element).getName();
			}
		} else if (element instanceof EncryptedFile) {
			return ((EncryptedFile)element).getTitle();
		} else {
			throw unknownElement(element);
		}
	}

	public void dispose() {
		for (Iterator<?> i = imageCache.values().iterator(); i.hasNext();) {
			((Image) i.next()).dispose();
		}
		imageCache.clear();
	}

	protected RuntimeException unknownElement(Object element) {
		return new RuntimeException("Unknown type of element in tree of type " + element.getClass().getName());
	}

}
