/**
 * Date: 28.10.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.part.ViewPart;

public class SideBarView extends ViewPart {
	
	public static String ID = "de.steadycrypt.v2.view.sidebar";

	@Override
	public void createPartControl(Composite parent) {
		
		// ExpandBars
		ExpandBar exBar = new ExpandBar(parent, 0);
		
		// First item
		Composite composite = new Composite (exBar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);

		ExpandItem item0 = new ExpandItem (exBar, SWT.NONE, 0);
		item0.setText("Filter");
		item0.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl(composite);
		item0.setExpanded(true);
		
		// Second item
		composite = new Composite (exBar, SWT.NONE);
		layout = new GridLayout (2, false);
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		
		ExpandItem item1 = new ExpandItem (exBar, SWT.NONE, 1);
		item1.setText("Favourites");
		item1.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item1.setControl(composite);
		item1.setExpanded(true);
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
