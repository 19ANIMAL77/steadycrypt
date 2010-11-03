package de.steadycrypt.v2.views.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.steadycrypt.v2.bob.EncryptedFolder;

public class ThreeItemFilter extends ViewerFilter {

	/*
	 * @see ViewerFilter#select(Viewer, Object, Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return parentElement instanceof EncryptedFolder && ((EncryptedFolder)parentElement).size() >= 3;
	}

}
