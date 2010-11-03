package de.steadycrypt.v2.views.ui;
/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.steadycrypt.v2.bob.EncryptedFolder;

public class ThreeItemFilter extends ViewerFilter {

	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		return parentElement instanceof EncryptedFolder && ((EncryptedFolder)parentElement).size() >= 3;
	}

}
