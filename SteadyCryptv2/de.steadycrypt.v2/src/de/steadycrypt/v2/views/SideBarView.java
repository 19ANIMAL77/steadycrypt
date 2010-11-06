/**
 * Date: 28.10.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.views.model.SideBarListener;

public class SideBarView extends ViewPart {
	
	public static String ID = "de.steadycrypt.v2.view.sideBar";
	private static org.apache.log4j.Logger log = Logger.getLogger(SideBarView.class);
	
	protected static EventListenerList listenerList = new EventListenerList();

	@Override
	public void createPartControl(Composite parent) {
		
		// ExpandBars
		ExpandBar exBar = new ExpandBar(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		
		// First item
		Composite composite = new Composite (exBar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);
		
		// Button experiment STARTS
		Button button = new Button (composite, SWT.PUSH);
		button.setText("Hallo Tabelle");
	    button.addSelectionListener(new SelectionListener() {

	        public void widgetSelected(SelectionEvent event) {

	        	fireSideBarEvent();

	        }

	        public void widgetDefaultSelected(SelectionEvent event) {}
	        
	      });
		// Button experiment ENDS
		
		ExpandItem item0 = new ExpandItem (exBar, SWT.NONE, 0);
		item0.setText("Filters");
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
	
	/**
	 * Use this method to register a listener which is interested in being
	 * notified about SideBar events.
	 * 
	 * @param listener
	 */
	public static synchronized void addSideBarListener(SideBarListener listener)
	{
		listenerList.add(SideBarListener.class, listener);
	}
	
	/**
	 * Use this method to remove a previous registered change listener
	 * from this object.
	 * 
	 * @param listener
	 */
	public static synchronized void removeSideBarListener(SideBarListener listener)
	{
		listenerList.remove(SideBarListener.class, listener);
	}
	
	/**
	 * Internally used method which is triggered if a event has been
	 * occurred.
	 */
	private void fireSideBarEvent()
	{
		Object[] listeners = listenerList.getListeners(SideBarListener.class);
		for(int i = listeners.length-1; i>=0; i-=1)
		{
			((SideBarListener)listeners[i]).deleteRow();
		}
	}

}
