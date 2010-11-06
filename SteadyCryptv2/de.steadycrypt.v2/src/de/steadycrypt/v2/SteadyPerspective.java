/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.steadycrypt.v2.views.SideBarView;
import de.steadycrypt.v2.views.TreeTableView;

public class SteadyPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		
		String editorAreaID = layout.getEditorArea();
		
		layout.addStandaloneView(SideBarView.ID, false, SWT.LEFT, 0.25f, editorAreaID);
		layout.addStandaloneView(TreeTableView.ID, false, SWT.TOP, 0.60f, editorAreaID);
		
		layout.setEditorAreaVisible(false);
		
	}
}
