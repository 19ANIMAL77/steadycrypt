/**
 * Date: 28.10.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.views.model.SideBarListener;

public class SideBarView extends ViewPart {
	
	public static String ID = "de.steadycrypt.v2.view.sideBar";
	public static String searchString = "";
	
	private static Logger log = Logger.getLogger(SideBarView.class);
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();

	protected static EventListenerList listenerList = new EventListenerList();

	@Override
	public void createPartControl(Composite parent) {
		
	
		// First part - Properties for filters
        Composite filterComposite = new Composite(parent, SWT.BORDER);
        filterComposite.setLayout(new GridLayout(2, true));
		
		// GUI Components
		Label lblSearch = new Label(filterComposite, SWT.FLAT);
		lblSearch.setText(Messages.SideBarView_Search);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		lblSearch.setLayoutData(gridData);
				
		final Text txtSearchField = new Text(filterComposite, SWT.BORDER);
		txtSearchField.setLayoutData(gridData);
		
		Label lblFileTypes = new Label(filterComposite, SWT.FLAT);
		lblFileTypes.setText(Messages.SideBarView_Search);
		lblFileTypes.setLayoutData(gridData);

	    final Combo comboFileTypes = new Combo(filterComposite, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER  |SWT.READ_ONLY);
	    comboFileTypes.setLayoutData(gridData);
	    for(String fileType : encryptedFileDao.getAllFileTypes())
	    {
	    	comboFileTypes.add(fileType);
	    }
		
		/**
		 * Refresh the static searchString after every key is
		 * released and inform TreeTableView about the change
		 * via fireSideBarEvent.
		 */
		txtSearchField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				searchString = txtSearchField.getText();
				fireSideBarEvent();
			}
			
		});
		
	}

	@Override
	public void setFocus() {
		
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
			((SideBarListener)listeners[i]).doSearch();
		}
	}
}
