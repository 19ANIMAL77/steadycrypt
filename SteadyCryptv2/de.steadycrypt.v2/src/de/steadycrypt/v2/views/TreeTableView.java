/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.Activator;
import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.core.FileDropHandler;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.EncryptedFolderDao;
import de.steadycrypt.v2.views.model.SideBarListener;
import de.steadycrypt.v2.views.ui.FileFolderSorter;
import de.steadycrypt.v2.views.ui.NoArticleSorter;
import de.steadycrypt.v2.views.ui.SteadyTableIdentifier;
import de.steadycrypt.v2.views.ui.SteadyTreeTableContentProvider;
import de.steadycrypt.v2.views.ui.SteadyTreeTableLabelProvider;
import de.steadycrypt.v2.views.ui.ThreeItemFilter;

public class TreeTableView extends ViewPart implements SideBarListener {
	
	private static Logger log = Logger.getLogger(TreeTableView.class);
	public static String ID = "de.steadycrypt.v2.view.treeTable";
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	protected TreeViewer treeViewer;
	protected SteadyTreeTableLabelProvider labelProvider;

    private ToolBarManager toolBarManager;
	protected Action atLeatThreeItems;
	protected Action filesFoldersAction, noArticleAction;
	protected Action addFileAction, removeAction;
    private Action exportSelectionAction;
    private Action expandAllAction;
    private Action collapseAllAction;
    private Action selectAllAction;
	protected ViewerFilter atLeastThreeFilter;
	protected ViewerSorter filesFoldersSorter, noArticleSorter;


	EncryptedFolderDao encryptedFolderDao;
	EncryptedFileDao encryptedFileDao;
	protected EncryptedFolderDob root;
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public TreeTableView() {
		
		/**
		 * Register me at SideBarListener, hi there.
		 */
		SideBarView.addSideBarListener(this);
		
	}

	public void createPartControl(Composite parent)
	{
		makeActions();
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        Composite content = new Composite(scrolledComposite, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        scrolledComposite.setContent(content);

        this.toolBarManager = new ToolBarManager();
        this.toolBarManager.add(this.exportSelectionAction);
        this.toolBarManager.add(this.expandAllAction);
        this.toolBarManager.add(this.collapseAllAction);
        this.toolBarManager.add(this.selectAllAction);

        ToolBar toolbar = toolBarManager.createControl(content);
        toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        Label horizontalSeparator = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(content, SWT.FULL_SELECTION | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);		
		
		// Anpassungen für TreeTable
		Tree tree = this.treeViewer.getTree();
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.treeViewer.getControl().setLayoutData(gridData);
	
		treeViewer.setUseHashlookup(true);
		
		/*** Tree table specific code starts ***/

		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		for(SteadyTableIdentifier identifier : SteadyTableIdentifier.values())
        {
            new TreeColumn(tree, SWT.NONE).setText(Messages.getSteadyTableColumnTitle(identifier));
            tree.getColumn(identifier.ordinal()).setWidth(identifier.columnWidth);
        }

		/*** Tree table specific code ends ***/ 
		
		// layout the tree viewer below the text field
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(gridData);
		
		treeViewer.setContentProvider(new SteadyTreeTableContentProvider());
		treeViewer.setLabelProvider(new SteadyTreeTableLabelProvider());
		
		root = getInitialInput();
		treeViewer.setInput(root);
		treeViewer.expandToLevel(1);
		
	    DropTarget dropTarget = new DropTarget(tree, DND.DROP_COPY | DND.DROP_DEFAULT);    
	        
	    dropTarget.setTransfer(new Transfer[] {FileTransfer.getInstance() });
	    dropTarget.addDropListener(new DropTargetAdapter()
	    {
	    	FileTransfer fileTransfer = FileTransfer.getInstance();
	 
	    	public void dragEnter(DropTargetEvent event)
	    	{
	    		if (event.detail == DND.DROP_DEFAULT)
	    			event.detail = DND.DROP_COPY;
	    	}
	    	    
		    public void dragOperationChanged(DropTargetEvent event)
		    {
		    	if (event.detail == DND.DROP_DEFAULT)
		    		event.detail = DND.DROP_COPY;
		    }          
	 
	        public void dragOver(DropTargetEvent event)
	        {
	        	
	        }	      
	      
	        public void drop(DropTargetEvent event)
	        {
	        	if (fileTransfer.isSupportedType(event.currentDataType))
	            {
	        		FileDropHandler fileDropHandler = new FileDropHandler();
	        		String[] droppedFileInformation = (String[]) event.data;
	        		
	        		log.info(droppedFileInformation.length + " Files dropt. Handing over to FileDropHandler!");
	        				
	    			try
	    			{
	    				fileDropHandler.processData(droppedFileInformation, root);
	    			}
	    			catch(Exception e)
	    			{
	    				
	    			}
	            }
	        	treeViewer.refresh();
	        }
	    });
	    
	    final Button exportFilesButton = new Button(content, SWT.FLAT);
	    exportFilesButton.setText(Messages.TableView_ExportFile);
        gridData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
        exportFilesButton.setLayoutData(gridData);
        
        exportFilesButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				exportSelectionAction.run();			
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
        
        MenuManager popupMenuManager = new MenuManager("PopupMenu");
        IMenuListener listener = new IMenuListener() { 
        public void menuAboutToShow(IMenuManager manager) { 
            manager.add(exportSelectionAction); 
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); 
            } 
        }; 
        popupMenuManager.addMenuListener(listener); 
        popupMenuManager.setRemoveAllWhenShown(true); 
        getSite().registerContextMenu(popupMenuManager, getSite().getSelectionProvider());
        Menu menu = popupMenuManager.createContextMenu(tree);
        tree.setMenu(menu);
	}
	
	protected void createFiltersAndSorters() {
		atLeastThreeFilter = new ThreeItemFilter();
		filesFoldersSorter = new FileFolderSorter();
		noArticleSorter = new NoArticleSorter();
	}

	protected void hookListeners() {
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// if the selection is empty clear the label
				if(event.getSelection().isEmpty()) {
//					text.setText("");
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					StringBuffer toShow = new StringBuffer();
					for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
						Object domain = (DroppedElement) iterator.next();
						String value = labelProvider.getColumnText(domain,1);
						toShow.append(value);
						toShow.append(", ");
					}
					// remove the trailing comma space pair
					if(toShow.length() > 0) {
						toShow.setLength(toShow.length() - 2);
					}
//					text.setText(toShow.toString());
				}
			}
		});
	}
	
	protected void createActions() {
		
		atLeatThreeItems = new Action("Folders With At Least Three Items") {
			public void run() {
				updateFilter(atLeatThreeItems);
			}
		};
		atLeatThreeItems.setChecked(false);
		
		filesFoldersAction = new Action("Files, Folders") {
			public void run() {
				updateSorter(filesFoldersAction);
			}
		};
		filesFoldersAction.setChecked(false);
		
		noArticleAction = new Action("Ignoring Articles") {
			public void run() {
				updateSorter(noArticleAction);
			}
		};
		noArticleAction.setChecked(false);
		
		addFileAction = new Action("Add EncryptedFile") {
			public void run() {
				addNewFile();
			}			
		};
		addFileAction.setToolTipText("Add a New EncryptedFile");

		removeAction = new Action("Delete") {
			public void run() {
				removeSelected();
			}			
		};
		removeAction.setToolTipText("Delete");		
	}
	
	/** Add a new book to the selected folders.
	 * If a folder is not selected, use the selected
	 * obect's folders. 
	 * 
	 * If nothing is selected add to the root. */
	protected void addNewFile() {
//		EncryptedFolder receivingFolder;
//		if (treeViewer.getSelection().isEmpty()) {
//			receivingFolder = root;
//		} else {
//			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
//			DroppedElement selectedDomainObject = (DroppedElement) selection.getFirstElement();
//			if (!(selectedDomainObject instanceof EncryptedFolder)) {
//				receivingFolder = selectedDomainObject.getParent();
//			} else {
//				receivingFolder = (EncryptedFolder) selectedDomainObject;
//			}
//		}
//		receivingFolder.add(EncryptedFile.newFile());
	}

	/** Remove the selected domain object(s).
	 * If multiple objects are selected remove all of them.
	 * 
	 * If nothing is selected do nothing. */
	protected void removeSelected() {
//		if (treeViewer.getSelection().isEmpty()) {
//			return;
//		}
//		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
//		/* Tell the tree to not redraw until we finish
//		 * removing all the selected children. */
//		treeViewer.getTree().setRedraw(false);
//		for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
//			DroppedElement droppedElement = (DroppedElement) iterator.next();
//			EncryptedFolder parent = droppedElement.getParent();
//			parent.remove(droppedElement);
//		}
//		treeViewer.getTree().setRedraw(true);
	}

	protected void fillMenu(IMenuManager rootMenuManager) {
		IMenuManager filterSubmenu = new MenuManager("Filters");
		rootMenuManager.add(filterSubmenu);
		filterSubmenu.add(atLeatThreeItems);
		
		IMenuManager sortSubmenu = new MenuManager("Sort By");
		rootMenuManager.add(sortSubmenu);
		sortSubmenu.add(filesFoldersAction);
		sortSubmenu.add(noArticleAction);
	}	
	
	protected void updateSorter(Action action) {
		if(action == filesFoldersAction) {
			noArticleAction.setChecked(!filesFoldersAction.isChecked());
			if(action.isChecked()) {
				treeViewer.setSorter(filesFoldersSorter);
			} else {
				treeViewer.setSorter(null);
			}
		} else if(action == noArticleAction) {
			filesFoldersAction.setChecked(!noArticleAction.isChecked());
			if(action.isChecked()) {
				treeViewer.setSorter(noArticleSorter);
			} else {
				treeViewer.setSorter(null);
			}
		}
			
	}
	
	/* Multiple filters can be enabled at a time. */
	protected void updateFilter(Action action) {
		if(action == atLeatThreeItems) {
			if(action.isChecked()) {
				treeViewer.addFilter(atLeastThreeFilter);
			} else {
				treeViewer.removeFilter(atLeastThreeFilter);
			}
		}
//		else if(action == onlyBoardGamesAction) {
//			if(action.isChecked()) {
//				treeViewer.addFilter(onlyBoardGamesFilter);
//			} else {
//				treeViewer.removeFilter(onlyBoardGamesFilter);
//			}
//		}
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private void makeActions()
    {
    	exportSelectionAction = new Action() {
        	public void run()
        	{
        		DirectoryDialog directoryDialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
        		directoryDialog.setText(Messages.TableView_ExportFileDialog_Title);
        		
        		String selectedFolder = directoryDialog.open();
        		log.debug(selectedFolder);
        		treeViewer.refresh();
        	}
        };
        
        exportSelectionAction.setText(Messages.TableView_ExportFile);
        exportSelectionAction.setToolTipText(Messages.TableView_ExportFile_Tooltip);
        exportSelectionAction.setImageDescriptor(Activator.getImageDescriptor("icons/export2.png"));
        
        expandAllAction = new Action() {
        	public void run()
        	{
        		treeViewer.expandAll();
        	}
        };
        
        expandAllAction.setText(Messages.TableView_ExpandAll);
        expandAllAction.setToolTipText(Messages.TableView_ExpandAll_Tooltip);
        expandAllAction.setImageDescriptor(Activator.getImageDescriptor("icons/expandall.gif"));
        
        collapseAllAction = new Action() {
        	public void run()
        	{
        		treeViewer.collapseAll();
    			treeViewer.expandToLevel(1);
        	}
        };
        
        collapseAllAction.setText(Messages.TableView_CollapseAll);
        collapseAllAction.setToolTipText(Messages.TableView_CollapseAll_Tooltip);
        collapseAllAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
        
        selectAllAction = new Action() {
        	public void run()
        	{
        		Tree tree = treeViewer.getTree();
        		TreeItem[] levelItems = tree.getItems();
        		
        		if(levelItems.length > 0)
        		{
        			checkItemsOfLevel(levelItems);
        		}
        	}
        	private void checkItemsOfLevel(TreeItem[] levelItems)
        	{
        		for(TreeItem item : levelItems)
        		{
        			item.setChecked(true);
        			if(item.getItems().length > 0)
        				checkItemsOfLevel(item.getItems());
        		}
        	}
        };
        
        selectAllAction.setText(Messages.TableView_SelectAll);
        selectAllAction.setToolTipText(Messages.TableView_SelectAll_Tooltip);
        selectAllAction.setImageDescriptor(Activator.getImageDescriptor("icons/selectall.gif"));
    }	
	
	public EncryptedFolderDob getInitialInput()
	{
    	root = new EncryptedFolderDob(0, "Root-Folder", new Date(System.currentTimeMillis()), "");

    	encryptedFolderDao = new EncryptedFolderDao();
    	encryptedFileDao = new EncryptedFileDao();
    	
    	getFolderContent(root);
    	
    	return root;
	}
	
	public void getFolderContent(EncryptedFolderDob folder)
	{
		folder.addFiles(encryptedFileDao.getFilesForFolder(folder));
		
		List<EncryptedFolderDob> childFolders = encryptedFolderDao.getFoldersForFolder(folder);
		
		if(childFolders != null && childFolders.size() > 0)
		{
			folder.addFolders(childFolders);
			
			for(EncryptedFolderDob childFolder : childFolders)
			{
				getFolderContent(childFolder);
			}
		}
	}

	/**
	 * Triggered by SideBar
	 */
	public void findItem(String searchString) {
		
		/**
		 * Triggered by SideBarView, done by TreeTableView
		 */
		log.info("Triggered by SideBarView, done by TreeTableView");
		log.info("Search for item: "+ searchString);
		
	}

}
