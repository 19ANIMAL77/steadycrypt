/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.sql.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
import de.steadycrypt.v2.core.DecryptHandler;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.EncryptedFolderDao;
import de.steadycrypt.v2.views.model.SideBarListener;
import de.steadycrypt.v2.views.model.TreeDragSourceListener;
import de.steadycrypt.v2.views.model.TreeDropTargetAdapter;
import de.steadycrypt.v2.views.ui.DataTypeFilter;
import de.steadycrypt.v2.views.ui.EncryptionDateFilter;
import de.steadycrypt.v2.views.ui.SearchFilter;
import de.steadycrypt.v2.views.ui.SteadyTableIdentifier;
import de.steadycrypt.v2.views.ui.SteadyTreeTableContentProvider;
import de.steadycrypt.v2.views.ui.SteadyTreeTableLabelProvider;

public class TreeTableView extends ViewPart implements SideBarListener {
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(TreeTableView.class);
    private ToolBarManager toolBarManager;

    private Action exportSelectionAction;
    private Action deleteSelectionAction;
    private Action expandAllAction;
    private Action collapseAllAction;
    private Action selectAllAction;

	private DecryptHandler decryptHandler = new DecryptHandler();
	
	private EncryptedFolderDao encryptedFolderDao;
	private EncryptedFileDao encryptedFileDao;
	private EncryptedFolderDob root;
	private List<DroppedElement> checkedElements;
	
	private TreeViewer treeViewer;
	private ViewerFilter searchFilter;
	private DataTypeFilter dataTypeFilter;
	private EncryptionDateFilter encryptionDateFilter;
	
	public static String ID = "de.steadycrypt.v2.view.treeTable";

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

        toolBarManager = new ToolBarManager();
        toolBarManager.add(exportSelectionAction);
        toolBarManager.add(expandAllAction);
        toolBarManager.add(collapseAllAction);
        toolBarManager.add(selectAllAction);

        ToolBar toolbar = toolBarManager.createControl(content);
        toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        Label horizontalSeparator = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(content, SWT.FULL_SELECTION | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);		
		
		// Anpassungen für TreeTable
		Tree tree = treeViewer.getTree();
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		treeViewer.getControl().setLayoutData(gridData);
	
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
		
		treeViewer.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick(DoubleClickEvent event)
            {
                decryptHandler.processData((TreeSelection)event.getSelection());
	        	treeViewer.refresh();
	        	SideBarView.updateFileTypeFilter();
            }
        });
		
		// Drag-Part //////////////////////////////////////////////////////////
		DragSource source = new DragSource(tree, DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK);
	    // TODO: welchen TransferType muss man wählen?
	    source.setTransfer(new Transfer[] {TextTransfer.getInstance()});
	    source.addDragListener(new TreeDragSourceListener(tree));
		
		// Drop-Part //////////////////////////////////////////////////////////
	    DropTarget dropTarget = new DropTarget(tree, DND.DROP_COPY | DND.DROP_DEFAULT);    
	    // TODO: und welchen TransferType muss man hier wählen?
	    dropTarget.setTransfer(new Transfer[] {TextTransfer.getInstance(), FileTransfer.getInstance()});
	    dropTarget.addDropListener(new TreeDropTargetAdapter(tree, treeViewer));

	    
	    final Button exportFilesButton = new Button(content, SWT.FLAT);
	    exportFilesButton.setText(Messages.TableView_ExportFile);
        gridData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
        exportFilesButton.setLayoutData(gridData);
        
        exportFilesButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
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
        
        createFiltersAndSorters();
	}
	
	/**
	 * Instantiate all Filters needed.
	 */
	private void createFiltersAndSorters() {
		searchFilter = new SearchFilter();
		dataTypeFilter = new DataTypeFilter();
		encryptionDateFilter = new EncryptionDateFilter();
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private void makeActions()
    {
    	exportSelectionAction = new Action() {
        	public void run()
        	{
                decryptHandler.processData((TreeSelection)treeViewer.getSelection());
                treeViewer.refresh();
	        	SideBarView.updateFileTypeFilter();
        	}
        };
        
        exportSelectionAction.setText(Messages.TableView_ExportFile);
        exportSelectionAction.setToolTipText(Messages.TableView_ExportFile_Tooltip);
        exportSelectionAction.setImageDescriptor(Activator.getImageDescriptor("icons/export2.png"));
        
        deleteSelectionAction = new Action() {
        	public void run()
        	{
                decryptHandler.processData((TreeSelection)treeViewer.getSelection());
                treeViewer.refresh();
	        	SideBarView.updateFileTypeFilter();
        	}
        };
        
        deleteSelectionAction.setText(Messages.TableView_ExportFile);
        deleteSelectionAction.setToolTipText(Messages.TableView_ExportFile_Tooltip);
        deleteSelectionAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_REMOVEALL));
        
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

    public List<DroppedElement> getCheckedElements()
    {
        return checkedElements;
    }
	
	private EncryptedFolderDob getInitialInput()
	{
    	root = new EncryptedFolderDob(0, "Root-Folder", new Date(System.currentTimeMillis()), "");

    	encryptedFolderDao = new EncryptedFolderDao();
    	encryptedFileDao = new EncryptedFileDao();
    	
    	getFolderContent(root);
    	
    	return root;
	}
	
	private void getFolderContent(EncryptedFolderDob folder)
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
	 * Triggered by SideBarView
	 */
	public void doSearch(){
		treeViewer.addFilter(searchFilter);
		treeViewer.addFilter(dataTypeFilter);
		treeViewer.addFilter(encryptionDateFilter);
	}

	public void setFocus() {}

}