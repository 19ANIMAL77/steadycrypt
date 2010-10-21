package de.steadycrypt.v2;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		
		layout.addStandaloneView(TableView.ID, true, SWT.RIGHT, 0.5f, layout.getEditorArea());
		layout.setEditorAreaVisible(false);
		
	}
}
