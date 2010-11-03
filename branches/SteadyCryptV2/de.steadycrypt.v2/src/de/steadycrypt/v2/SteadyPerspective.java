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
import de.steadycrypt.v2.views.TableView;
import de.steadycrypt.v2.views.TreeTableView;

public class SteadyPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		
		layout.addStandaloneView(SideBarView.ID, false, SWT.RIGHT, 0.25f, layout.getEditorArea());
		layout.addView(TableView.ID, SWT.RIGHT, 0.75f, SideBarView.ID);
        layout.addView(TreeTableView.ID, IPageLayout.BOTTOM, 0.5f, TableView.ID);
		layout.setEditorAreaVisible(false);
		
	}
}
