/**
 * Date: 28.10.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
    private IStatusLineManager statusline;
	private static EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	private FilterFavoriteDao filterFavoriteDao = new FilterFavoriteDao();
    private List<FilterFavoriteDob> favorites;
	private EncryptionPeriod eP;
    
    private Action saveFavoriteAction;
    private Action loadFavoriteAction;
    private Action deleteFavoriteAction;
    private Action clearFiltersAction;
    
    private Text txtSearchField;
    private Text txtSaveFavorite;
    private static Combo comboFileTypes;
    private Combo comboEncryptionDate;
    private Button saveButton;
    private Button clearButton;
    private Button deleteButton;
    private Table table;
    private TableViewer tableViewer;

	protected static EventListenerList listenerList = new EventListenerList();

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	@Override
	public void createPartControl(Composite parent)
	{
		statusline = getViewSite().getActionBars().getStatusLineManager();
		statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), Messages.StatusLine_DropFilesHint);
		createActions();
		
	    favorites = filterFavoriteDao.getFavorites();		
	
		// General layout / creating FormToolKit
		FormToolkit toolKit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());

		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);

		// Filters Section - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		
		Section filterSection = toolKit.createSection(parent, Section.TITLE_BAR);
		filterSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		filterSection.setText(Messages.SideBarView_Filters);
		
		Composite filterComposite = toolKit.createComposite(filterSection);
		filterComposite.setLayout(new GridLayout(2, true));
		toolKit.paintBordersFor(filterComposite);
		filterSection.setClient(filterComposite);
		
		final Label lblFileName = new Label(filterComposite, SWT.FLAT);
		lblFileName.setText(Messages.SideBarView_NameFilter);
		FontData fontData = lblFileName.getFont().getFontData()[0];
		Font font = new Font(PlatformUI.getWorkbench().getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lblFileName.setFont(font);
		lblFileName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
				
		txtSearchField = new Text(filterComposite, SWT.BORDER);
		txtSearchField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		final Label lblFileTypes = new Label(filterComposite, SWT.FLAT);
		lblFileTypes.setText(Messages.SideBarView_TypeFilter);
		lblFileTypes.setFont(font);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gridData.verticalIndent = 10;
		lblFileTypes.setLayoutData(gridData);

	    comboFileTypes = new Combo(filterComposite, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
	    comboFileTypes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
	    updateFileTypeFilter();
		
		final Label lblDate = new Label(filterComposite, SWT.FLAT);
		lblDate.setText(Messages.SideBarView_DateFilter);
		lblDate.setFont(font);
		lblDate.setLayoutData(gridData);
		
		final Label lblDateExp = new Label(filterComposite, SWT.FLAT);
		lblDateExp.setText(Messages.SideBarView_DateFilterExplanation);
		lblDateExp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		comboEncryptionDate = new Combo(filterComposite, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		comboEncryptionDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		initializeEncryptionDateFilter();

		// Save Filter Part - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		
        Label horizontalSeparator = new Label(filterComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 2, 1));
        horizontalSeparator.setVisible(false);
		
		final Label lblSaveFavorite = new Label(filterComposite, SWT.FLAT);
		lblSaveFavorite.setText(Messages.SideBarView_SaveFavorite);
		lblSaveFavorite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
				
		txtSaveFavorite = new Text(filterComposite, SWT.BORDER);
		txtSaveFavorite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		saveButton = new Button(filterComposite, SWT.FLAT);
		saveButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		saveButton.setText(Messages.SideBarView_SaveFavoriteButton);
		saveButton.setImage(Activator.getImageDescriptor("icons/favorite-add.png").createImage());
		
		// Favorites Section - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

		Section favoritesSection = toolKit.createSection(parent, Section.TITLE_BAR);
		favoritesSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		favoritesSection.setText(Messages.SideBarView_Favorites);
		
		Composite favoritesComposite = toolKit.createComposite(favoritesSection);
		favoritesComposite.setLayout(new GridLayout(2, true));
		toolKit.paintBordersFor(favoritesComposite);
		favoritesSection.setClient(favoritesComposite);

        table = new Table(favoritesComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
        table.setHeaderVisible(false);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        gridData.heightHint = 100;
        table.setLayoutData(gridData);
        
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(186);

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
		
		clearButton = new Button(favoritesComposite, SWT.FLAT);
		clearButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		clearButton.setText(Messages.SideBarView_ClearFiltersButton);
		
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
		// to save current filter settings to favorites - name needs to be specified, if selected name exists, confirmation is required
    	saveFavoriteAction = new Action() {
			public void run() {	
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
						}
					}
					else
					{
						favorites.add(filterFavoriteDao.addFavorite(new FilterFavorite(txtSaveFavorite.getText(), txtSearchField.getText().length() > 0 ? txtSearchField.getText() : null, comboFileTypes.getText().equals(Messages.Filter_NONE) ? null : comboFileTypes.getText(), comboEncryptionDate.getText().equals(Messages.Filter_NONE) ? null : eP.toString())));
					}
					statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), NLS.bind(Messages.StatusLine_Added, txtSaveFavorite.getText()));
					tableViewer.refresh();
				}
        	}
        };
        
        // to load selected favorite
    	loadFavoriteAction = new Action() {
			public void run()
			{	
				FilterFavoriteDob filter;
				filter = (FilterFavoriteDob)((StructuredSelection)tableViewer.getSelection()).getFirstElement();
				
				if(filter == null)
					return;
				
				boolean filetypeExists = false;
				
				if(filter.getFiletype() == null)
					filetypeExists=true;
				else {
					for(String entry : comboFileTypes.getItems()) {
						if(entry.equalsIgnoreCase(filter.getFiletype()))
							filetypeExists=true;
					}
				}
				
				if(!filetypeExists) {
					Object[] bindings = {filter.getFiletype(), filter.getName()};
					if(MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.TableView_InfoDialog_Title, NLS.bind(Messages.SideBarView_WarningDialog_FileTypeDoesntExist, bindings)))
						deleteFavoriteAction.run();
					clearFiltersAction.run();
					return;
				}
				
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
                fireSideBarEvent();
        	}
        };
        
        // to delete the selected favorite - Message dialog asks for confirmation
    	deleteFavoriteAction = new Action() {
            public void run() {
            	try
                {
                	StructuredSelection selection;
                	selection = (StructuredSelection)tableViewer.getSelection();
                	
                	if(selection == null)
                		return;
                	
                	FilterFavoriteDob selectedFavorite = (FilterFavoriteDob)selection.getFirstElement();
	                if(MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.SideBarView_WarningDialog_Title, NLS.bind(Messages.SideBarView_WarningDialog_Delete, selectedFavorite.getName())))
                	{
                		filterFavoriteDao.deleteFavorite(selectedFavorite);
                		favorites.remove(selectedFavorite);
    					statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), NLS.bind(Messages.StatusLine_SDeleted, selectedFavorite.getName()));
                	}
	                tableViewer.refresh();
	                clearFiltersAction.run();
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
        
        // clearing all filters currently set
    	clearFiltersAction = new Action() {
			public void run()
			{
				// clear favorite name text field
				txtSaveFavorite.setText("");
				// clear filename filter
				txtSearchField.setText("");
				fileNameFilterString = txtSearchField.getText();
				// clear file type filter
				comboFileTypes.setText(Messages.Filter_NONE);
				fileTypeFilterString = comboFileTypes.getText();
				// clear encryption date filter
				comboEncryptionDate.setText(Messages.Filter_NONE);
				encryptionDateFilterString = comboEncryptionDate.getText();
				calculateEncryptionDateFilter();
				table.deselectAll();
				fireSideBarEvent();
        	}
        };
	}
	
	/**
	 * Adds all needed listeners to the corresponding gui elements
	 */
	private void addListeners()
	{
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
                loadFavoriteAction.run();
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
		
		clearButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearFiltersAction.run();
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
	
	/**
	 * Creates the context menu for the favorites table
	 */
	private void createContextMenu()
	{   
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
	
	/**
	 * Fills the file type combo with all found file types.
	 */
	public static void updateFileTypeFilter()
	{
		String currentValue = comboFileTypes.getText();
    	comboFileTypes.removeAll();
    	comboFileTypes.add(Messages.Filter_NONE);
    	comboFileTypes.add(Messages.FileTypeFilter_FOLDER);
	    for(String fileType : encryptedFileDao.getAllFileTypes())
	    {
	    	comboFileTypes.add(fileType);
	    }
	    comboFileTypes.setText(!currentValue.equalsIgnoreCase("") ? currentValue : Messages.Filter_NONE);
	}
	
	/**
	 * Fills the encryptionDate filter combo with its values.
	 */
	private void initializeEncryptionDateFilter()
	{
		comboEncryptionDate.removeAll();
		comboEncryptionDate.add(Messages.Filter_NONE);
		comboEncryptionDate.add(Messages.EncryptionDateFilter_WEEK);
		comboEncryptionDate.add(Messages.EncryptionDateFilter_MONTH);
		comboEncryptionDate.add(Messages.EncryptionDateFilter_YEAR);

		comboEncryptionDate.setText(Messages.Filter_NONE);
	}
	
	/**
	 * Converts the selected filter into a date that can be compared to the files/folders
	 * encryption date. Also stores an enum value to eP, which will be written to the database
	 * in case the filter is being saved.
	 */
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
	
	/**
	 * One of those values is written into the database. This enum is required to be able
	 * to keep the filter multi language compatible.
	 */
	private enum EncryptionPeriod {
		YEAR, MONTH, WEEK;
	}
}
