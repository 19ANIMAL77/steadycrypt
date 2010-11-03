package de.steadycrypt.v2.views.ui;

import org.eclipse.jface.viewers.ViewerSorter;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.EncryptedFolder;

public class FileFolderSorter extends ViewerSorter {
	
	/*
	 * @see ViewerSorter#category(Object)
	 */
	/** Orders the items in such a way that files appear 
	 * before moving folders, which appear before board games. */
	public int category(Object element) {
		if(element instanceof EncryptedFile) return 1;
		if(element instanceof EncryptedFolder) return 2;
		return 3;
	}

}
