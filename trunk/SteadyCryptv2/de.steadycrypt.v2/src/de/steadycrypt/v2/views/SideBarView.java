/**
 * Date: 28.10.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import de.steadycrypt.v2.bob.FilterFavorite;
import de.steadycrypt.v2.bob.dob.FilterFavoriteDob;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.FilterFavoriteDao;
import de.steadycrypt.v2.views.model.SideBarListener;

public class SideBarView extends ViewPart {
	
	public static String ID = "de.steadycrypt.v2.view.sideBar";
	public static String fileNameFilterString = "";
	public static String fileTypeFilterString = Messages.Filter_NONE;
	public static String encryptionDateFilterString = Messages.Filter_NONE;
	public static Date encryptionDateFilter;
	
	private static Logger log = Logger.getLogger(SideBarView.class);
	private static EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	private FilterFavoriteDao filterFavoriteDao = new FilterFavoriteDao();
    private List<FilterFavoriteDob> favorites;
	private EncryptionPeriod eP;
    
    private Action saveFavoriteAction;
    private Action loadFavoriteAction;
    private Action deleteFavoriteAction;
    
    private Text txtSearchField;
    private Text txtSaveFavorite;
    private static Combo comboFileTypes;
    private static Combo comboEncryptionDate;
    private Button saveButton;
    private Button loadButton;
    private Button deleteButton;
    private Table table;
    private TableViewer tableViewer;

	protected static EventListenerList listenerList = new EventListenerList();

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	@Override
	public void createPartControl(Composite parent)
	{
		createActions();
		
	    favorites = filterFavoriteDao.getFavorites();		
	
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
				
		txtSearchField = new Text(filterComposite, SWT.BORDER);
		txtSearchField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		final Label lblFileTypes = new Label(filterComposite, SWT.FLAT);
		lblFileTypes.setText(Messages.SideBarView_TypeFilter);
		lblFileTypes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

	    comboFileTypes = new Combo(filterComposite, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
	    comboFileTypes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
	    updateFileTypeFilter();
		
		final Label lblDate = new Label(filterComposite, SWT.FLAT);
		lblDate.setText(Messages.SideBarView_DateFilter);
		lblDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		comboEncryptionDate = new Combo(filterComposite, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		comboEncryptionDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		updateEncryptionDateFilter();

		// Save Filter Part
		
        Label horizontalSeparator = new Label(filterComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
		
		final Label lblSaveFavorite = new Label(filterComposite, SWT.FLAT);
		lblSaveFavorite.setText(Messages.SideBarView_SaveFavorite);
		lblSaveFavorite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
				
		txtSaveFavorite = new Text(filterComposite, SWT.BORDER);
		txtSaveFavorite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		saveButton = new Button(filterComposite, SWT.FLAT);
		saveButton.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
		saveButton.setText(Messages.SideBarView_SaveFavoriteButton);
		saveButton.setImage(Activator.getImageDescriptor("icons/favorite-add.png").createImage());
		
		// Favorites Section

		Section favoritesSection = toolKit.createSection(parent, Section.TITLE_BAR);
		favoritesSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		favoritesSection.setText(Messages.SideBarView_Favorites);
		
		Composite favoritesComposite = toolKit.createComposite(favoritesSection);
		favoritesComposite.setLayout(new GridLayout(2, true));
		toolKit.paintBordersFor(favoritesComposite);
		favoritesSection.setClient(favoritesComposite);

        table = new Table(favoritesComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
        table.setHeaderVisible(false);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gridData.heightHint = 100;
        table.setLayoutData(gridData);
        
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(192);

        tableViewer = new TableViewer(table);
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
            	return null;
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
		
		loadButton = new Button(favoritesComposite, SWT.FLAT);
		loadButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		loadButton.setText(Messages.SideBarView_LoadFavoriteButton);
		loadButton.setImage(Activator.getImageDescriptor("icons/favorite.png").createImage());
		
		deleteButton = new Button(favoritesComposite, SWT.FLAT);
		deleteButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		deleteButton.setText(Messages.SideBarView_DeleteFavoriteButton);
		deleteButton.setImage(Activator.getImageDescriptor("icons/favorite-delete.png").createImage());
		
		addListeners();
		createContextMenu();
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private void createActions()
	{
    	saveFavoriteAction = new Action() {
            
			public void run()
        	{	
				if(!(txtSaveFavorite.getText().length() > 0))
				{
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SideBarView_ErrorDialog_Title, Messages.SideBarView_ErrorDialog_MissingName);
				}
				else
				{
					if(filterFavoriteDao.allreadyExists(txtSaveFavorite.getText()))
					{
						if(MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SideBarView_WarningDialog_Title, NLS.bind(Messages.SideBarView_WarningDialog_Override, txtSaveFavorite.getText())))
						{
							FilterFavoriteDob filterToUpdate = null;
							for(FilterFavoriteDob filterFavorite : favorites)
							{
								if(filterFavorite.getName().equalsIgnoreCase(txtSaveFavorite.getText()))
								{
									filterFavorite.setFilename(txtSearchField.getText().length() > 0 ? txtSearchField.getText() : null);
									filterFavorite.setFiletype(comboFileTypes.getText().equals(Messages.Filter_NONE) ? null : comboFileTypes.getText());
									filterFavorite.setEncryptionPeriod(comboEncryptionDate.getText().equals(Messages.Filter_NONE) ? null : eP.toString());
									
									filterToUpdate = filterFavorite;
								}
							}
							filterFavoriteDao.updateFavorite(filterToUpdate);
							tableViewer.refresh();
						}
					}
					else
					{
						favorites.add(filterFavoriteDao.addFavorite(new FilterFavorite(txtSaveFavorite.getText(), txtSearchField.getText().length() > 0 ? txtSearchField.getText() : null, comboFileTypes.getText().equals(Messages.Filter_NONE) ? null : comboFileTypes.getText(), comboEncryptionDate.getText().equals(Messages.Filter_NONE) ? null : eP.toString())));
						tableViewer.refresh();
					}
				}
        	}
        };
        
    	loadFavoriteAction = new Action() {
            
			public void run()
        	{	
				FilterFavoriteDob filter;
				filter = (FilterFavoriteDob)((StructuredSelection)tableViewer.getSelection()).getFirstElement();
				
				if(filter == null)
					return;
				
				txtSearchField.setText(filter.getFilename() == null ? "" : filter.getFilename());
				fileNameFilterString = txtSearchField.getText();
				comboFileTypes.setText(filter.getFiletype() == null ? Messages.Filter_NONE : filter.getFiletype());
				fileTypeFilterString = comboFileTypes.getText();
				if(filter.getEncryptionPeriod() == null)
				{
					comboEncryptionDate.setText(Messages.Filter_NONE);
				} else if(filter.getEncryptionPeriod().equals(EncryptionPeriod.WEEK.toString()))
				{
					comboEncryptionDate.setText(Messages.EncryptionDateFilter_WEEK);
				} else if(filter.getEncryptionPeriod().equals(EncryptionPeriod.MONTH.toString()))
				{
					comboEncryptionDate.setText(Messages.EncryptionDateFilter_MONTH);
				} else if(filter.getEncryptionPeriod().equals(EncryptionPeriod.YEAR.toString()))
				{
					comboEncryptionDate.setText(Messages.EncryptionDateFilter_YEAR);
				}
				encryptionDateFilterString = comboEncryptionDate.getText();
				calculateEncryptionDateFilter();
				txtSaveFavorite.setText(filter.getName());
        	}
        };

        loadFavoriteAction.setText(Messages.SideBarView_LoadFavorite);
        loadFavoriteAction.setToolTipText(Messages.SideBarView_LoadFavorite);
        loadFavoriteAction.setImageDescriptor(Activator.getImageDescriptor("icons/favorite.png"));
        
    	deleteFavoriteAction = new Action() {
            @SuppressWarnings("rawtypes")
			public void run()
        	{
            	try
                {
                	StructuredSelection selection;
                	selection = (StructuredSelection)tableViewer.getSelection();
                	
                	if(selection == null)
                		return;
                	
					Iterator iterator = selection.iterator();
	                while(iterator.hasNext())
	                {
	                	Object nextElement = iterator.next();
	                	if(nextElement instanceof FilterFavoriteDob) {
	                		if(((FilterFavoriteDob)nextElement).getName().equalsIgnoreCase(Messages.RESET_FAVORIT))
	                		{
	                			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SideBarView_ErrorDialog_Title, Messages.SideBarView_ErrorDialog_CantDelete);
	                			continue;
	                		}
	                		if(MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SideBarView_WarningDialog_Title, NLS.bind(Messages.SideBarView_WarningDialog_Delete, ((FilterFavoriteDob)nextElement).getName())))
	                    	{
	                    		filterFavoriteDao.deleteFavorite((FilterFavoriteDob)nextElement);
	                    		favorites.remove((FilterFavoriteDob)nextElement);
	                    	}
	                	}
	                }
	                tableViewer.refresh();
                }
                catch(ClassCastException e) {
                	log.error(e.getMessage());
                	e.printStackTrace();
                }
        	}
        };
        
        deleteFavoriteAction.setText(Messages.SideBarView_DeleteFavorite);
        deleteFavoriteAction.setToolTipText(Messages.SideBarView_DeleteFavorite);
        deleteFavoriteAction.setImageDescriptor(Activator.getImageDescriptor("icons/favorite-delete.png"));
	}
	
	private void addListeners()
	{
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick(DoubleClickEvent event)
            {
                loadFavoriteAction.run();
                fireSideBarEvent();
            }
        });
		
		/**
		 * Refresh the static fileNameFilterString every time a key is
		 * released and inform TreeTableView about the change via
		 * fireSideBarEvent.
		 */
		txtSearchField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				fileNameFilterString = txtSearchField.getText();
				fireSideBarEvent();
			}
			
		});
		
		/**
		 * Refresh the static fileTypeFilterString every time the selection
		 * of the comboFileTypes changes and inform TreeTableView about the
		 * change via fireSideBarEvent.
		 */
		comboFileTypes.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileTypeFilterString = comboFileTypes.getText();
				fireSideBarEvent();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		
		/**
		 * Refresh the static encryptionDateFilterString every time the selection
		 * of the comboEncryptionDate changes and inform TreeTableView about the
		 * change via fireSideBarEvent.
		 */
		comboEncryptionDate.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				encryptionDateFilterString = comboEncryptionDate.getText();
				calculateEncryptionDateFilter();
				fireSideBarEvent();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		
		saveButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFavoriteAction.run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		
		loadButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadFavoriteAction.run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		
		deleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteFavoriteAction.run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}
	
	private void createContextMenu()
	{   
        MenuManager popupMenuManager = new MenuManager("PopupMenu");
        IMenuListener listener = new IMenuListener() { 
        public void menuAboutToShow(IMenuManager manager) { 
            manager.add(loadFavoriteAction); 
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
	
	public static void updateFileTypeFilter()
	{
    	comboFileTypes.removeAll();
    	comboFileTypes.add(Messages.Filter_NONE);
    	comboFileTypes.add(Messages.FileTypeFilter_FOLDER);
	    for(String fileType : encryptedFileDao.getAllFileTypes())
	    {
	    	comboFileTypes.add(fileType);
	    }
	    comboFileTypes.setText(Messages.Filter_NONE);
	}
	
	public static void updateEncryptionDateFilter()
	{
		comboEncryptionDate.removeAll();
		comboEncryptionDate.add(Messages.Filter_NONE);
		comboEncryptionDate.add(Messages.EncryptionDateFilter_WEEK);
		comboEncryptionDate.add(Messages.EncryptionDateFilter_MONTH);
		comboEncryptionDate.add(Messages.EncryptionDateFilter_YEAR);

		comboEncryptionDate.setText(Messages.Filter_NONE);
	}
	
	private void calculateEncryptionDateFilter() 
	{
		GregorianCalendar gc = new GregorianCalendar();
		if (encryptionDateFilterString.equalsIgnoreCase(Messages.EncryptionDateFilter_WEEK))
		{
			gc.add(Calendar.DAY_OF_YEAR, -7);
			encryptionDateFilter = new Date(gc.getTimeInMillis());
			eP = EncryptionPeriod.WEEK;
		} else if (encryptionDateFilterString.equalsIgnoreCase(Messages.EncryptionDateFilter_MONTH))
		{
			gc.add(Calendar.MONTH, -1);
			encryptionDateFilter = new Date(gc.getTimeInMillis());
			eP = EncryptionPeriod.MONTH;
		} else if (encryptionDateFilterString.equalsIgnoreCase(Messages.EncryptionDateFilter_YEAR))
		{
			gc.add(Calendar.YEAR, -1);
			encryptionDateFilter = new Date(gc.getTimeInMillis());
			eP = EncryptionPeriod.YEAR;
		}	
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

	@Override
	public void setFocus() {
		
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private enum EncryptionPeriod {
		YEAR, MONTH, WEEK;
	}
}
