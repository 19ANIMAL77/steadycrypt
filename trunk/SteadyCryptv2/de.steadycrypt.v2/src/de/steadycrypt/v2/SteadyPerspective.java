/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.steadycrypt.v2.views.SideBarView;
import de.steadycrypt.v2.views.TreeTableView;

public class SteadyPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		
		layout.addStandaloneView(SideBarView.ID, false, IPageLayout.LEFT, 0.25f, layout.getEditorArea());
		layout.addStandaloneView(TreeTableView.ID, false, IPageLayout.RIGHT, 0.25f, SideBarView.ID);
		
		layout.setEditorAreaVisible(false);
		
	}
}
