package de.steadycrypt.v2.views.ui;
/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;

public class SearchFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		return true;
//		return parentElement instanceof EncryptedFolderDob && ((EncryptedFolderDob)parentElement).size() >= 3;
	}
}
