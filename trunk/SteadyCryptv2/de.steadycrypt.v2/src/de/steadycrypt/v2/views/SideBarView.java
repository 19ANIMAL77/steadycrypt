/**
 * Date: 28.10.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.util.Iterator;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.Activator;
import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.dob.FilterFavoriteDob;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.FilterFavoriteDao;
import de.steadycrypt.v2.views.model.SideBarListener;

public class SideBarView extends ViewPart {
	
	public static String ID = "de.steadycrypt.v2.view.sideBar";
	public static String searchString = "";
	
	private static Logger log = Logger.getLogger(SideBarView.class);
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	private FilterFavoriteDao filterFavoriteDao = new FilterFavoriteDao();
    private List<FilterFavoriteDob> favorites;
    private Action deleteFavoriteAction;
    private TableViewer tableViewer;

	protected static EventListenerList listenerList = new EventListenerList();

	@Override
	public void createPartControl(Composite parent)
	{
		makeActions();
		
	    this.favorites = filterFavoriteDao.getFavorites();		
	
		// First part - Properties for filters
		FormToolkit toolKit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());

		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);

		// Filters Section
		
		Section filterSection = toolKit.createSection(parent, Section.TITLE_BAR);
		filterSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		filterSection.setText(Messages.SideBarView_Filters);
		
		Composite filterComposite = toolKit.createComposite(filterSection);
		filterComposite.setLayout(new GridLayout(2, true));
		toolKit.paintBordersFor(filterComposite);
		filterSection.setClient(filterComposite);
		
		final Label lblFileName = new Label(filterComposite, SWT.FLAT);
		lblFileName.setText(Messages.SideBarView_NameFilter);
		lblFileName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
				
		final Text txtSearchField = new Text(filterComposite, SWT.BORDER);
		txtSearchField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		final Label lblFileTypes = new Label(filterComposite, SWT.FLAT);
		lblFileTypes.setText(Messages.SideBarView_TypeFilter);
		lblFileTypes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

	    final Combo comboFileTypes = new Combo(filterComposite, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
	    comboFileTypes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
	    for(String fileType : encryptedFileDao.getAllFileTypes())
	    {
	    	comboFileTypes.add(fileType);
	    }
		
		final Label lblDate = new Label(filterComposite, SWT.FLAT);
		lblDate.setText(Messages.SideBarView_DateFilter);
		lblDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		final Label lblDateFrom = new Label(filterComposite, SWT.FLAT);
		lblDateFrom.setText(Messages.SideBarView_DateFilterFrom);
		lblDateFrom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		final Label lblDateTo = new Label(filterComposite, SWT.FLAT);
		lblDateTo.setText(Messages.SideBarView_DateFilterTo);
		lblDateTo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		// Save Filter Part
		
        Label horizontalSeparator = new Label(filterComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		final Label lblSaveFavorite = new Label(filterComposite, SWT.FLAT);
		lblSaveFavorite.setText(Messages.SideBarView_SaveFavorite);
		lblSaveFavorite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
				
		final Text txtSaveFavorite = new Text(filterComposite, SWT.BORDER);
		txtSaveFavorite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		final Button saveButton = new Button(filterComposite, SWT.FLAT);
		saveButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		saveButton.setText(Messages.SideBarView_SaveFavoriteButton);
		saveButton.setImage(Activator.getImageDescriptor("icons/favorite-add.png").createImage());
		
		// Favorites Section

		Section favoritesSection = toolKit.createSection(parent, Section.TITLE_BAR);
		favoritesSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		favoritesSection.setText(Messages.SideBarView_Favorites);
		
		Composite favoritesComposite = toolKit.createComposite(favoritesSection);
		favoritesComposite.setLayout(new GridLayout(1, true));
		toolKit.paintBordersFor(favoritesComposite);
		favoritesSection.setClient(favoritesComposite);

        final Table table = new Table(favoritesComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
        table.setHeaderVisible(false);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 100;
        table.setLayoutData(gridData);
        
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(192);

        this.tableViewer = new TableViewer(table);
        tableViewer.setLabelProvider(new ITableLabelProvider()
        {
            public String getColumnText(Object element, int columnIndex)
            {
            	if(element instanceof FilterFavoriteDob)
            		return ((FilterFavoriteDob)element).getName();
            	
            	return "";
            }

            public Image getColumnImage(Object element, int columnIndex)
            {
            	return Activator.getImageDescriptor("icons/favorite.png").createImage();
            }

            // unused methods
            public void addListener(ILabelProviderListener listener) { }
			public void dispose() { }
			public boolean isLabelProperty(Object element, String property) { return false; }
			public void removeListener(ILabelProviderListener listener) { }
        });
        
        tableViewer.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements(Object inputElement)
            {                	
                return favorites.toArray();
            }

            // unused methods
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
            public void dispose() { }
        });
        
        tableViewer.setInput(favorites.toArray());
		
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
        
        MenuManager popupMenuManager = new MenuManager("PopupMenu");
        IMenuListener listener = new IMenuListener() { 
        public void menuAboutToShow(IMenuManager manager) { 
            manager.add(deleteFavoriteAction); 
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); 
            } 
        }; 
        popupMenuManager.addMenuListener(listener); 
        popupMenuManager.setRemoveAllWhenShown(true); 
        getSite().registerContextMenu(popupMenuManager, getSite().getSelectionProvider());
        Menu menu = popupMenuManager.createContextMenu(table);
        table.setMenu(menu);
		
	}
	
	private void makeActions()
	{
    	deleteFavoriteAction = new Action() {
            @SuppressWarnings("rawtypes")
			public void run()
        	{
                try
                {
                	TreeSelection ts = (TreeSelection)tableViewer.getSelection();
					Iterator iterator = ts.iterator();
	                while(iterator.hasNext())
	                {
	                	Object nextElement = iterator.next();
	                	if(nextElement instanceof FilterFavoriteDob)
	                		filterFavoriteDao.deleteFavorite((FilterFavoriteDob)iterator.next());
	                }
	                tableViewer.refresh();
                }
                catch(ClassCastException e) { }
        	}
        };
        
        deleteFavoriteAction.setText(Messages.SideBarView_DeleteFavorite);
        deleteFavoriteAction.setToolTipText(Messages.SideBarView_DeleteFavorite);
        deleteFavoriteAction.setImageDescriptor(Activator.getImageDescriptor("icons/favorite-delete.png"));
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
