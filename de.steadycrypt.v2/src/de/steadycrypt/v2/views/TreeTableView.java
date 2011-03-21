/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
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
import de.steadycrypt.v2.bob.EncryptedFolder;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.core.DecryptHandler;
import de.steadycrypt.v2.core.DeleteFileHandler;
import de.steadycrypt.v2.core.FileDropHandler;
import de.steadycrypt.v2.core.SteadyInputValidator;
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
	
	private static Logger log = Logger.getLogger(TreeTableView.class);
    private static IStatusLineManager statusline;
    private ToolBarManager toolBarManager;

    private Action encryptFilesAction;
    private Action newFolderAction;
    private Action exportSelectionAction;
    private Action exportSelectionToOrigPathAction;
    private Action deleteSelectionAction;
    private Action renameAction;
    private Action expandAllAction;
    private Action collapseAllAction;

    private FileDropHandler fileDropHandler;
	private DecryptHandler decryptHandler;
	private DeleteFileHandler deleteFileHandler;
	
	private EncryptedFolderDao encryptedFolderDao;
	private EncryptedFileDao encryptedFileDao;
	private EncryptedFolderDob root;
	
	private Tree tree;
	private TreeViewer treeViewer;
	private ViewerFilter searchFilter;
	private DataTypeFilter dataTypeFilter;
	private EncryptionDateFilter encryptionDateFilter;
	
	private IStructuredSelection oldSelection = new TreeSelection();
	
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
		statusline = getViewSite().getActionBars().getStatusLineManager();
		statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), Messages.StatusLine_DropFilesHint);
		makeActions();
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        Composite content = new Composite(scrolledComposite, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        scrolledComposite.setContent(content);

        toolBarManager = new ToolBarManager();
        toolBarManager.add(encryptFilesAction);
        toolBarManager.add(newFolderAction);
        toolBarManager.add(new Separator("static"));
        toolBarManager.add(exportSelectionAction);
        toolBarManager.add(deleteSelectionAction);
        toolBarManager.add(new Separator("static"));
        toolBarManager.add(expandAllAction);
        toolBarManager.add(collapseAllAction);

        ToolBar toolbar = toolBarManager.createControl(content);
        toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        // Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(content, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);		
		
		// Anpassungen für TreeTable
		tree = treeViewer.getTree();
		
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
		
		// Drag-Part //////////////////////////////////////////////////////////
		DragSource source = new DragSource(tree, DND.DROP_COPY | DND.DROP_MOVE);
	    source.setTransfer(new Transfer[] {TextTransfer.getInstance()});
	    source.addDragListener(new TreeDragSourceListener(tree));
		
		// Drop-Part //////////////////////////////////////////////////////////
	    DropTarget dropTarget = new DropTarget(tree, DND.DROP_COPY | DND.DROP_DEFAULT);   
	    dropTarget.setTransfer(new Transfer[] {TextTransfer.getInstance(), FileTransfer.getInstance()});
	    dropTarget.addDropListener(new TreeDropTargetAdapter(treeViewer, root));

	    addListeners();
        createContextMenu();
        createFiltersAndSorters();
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private void makeActions()
    {
    	encryptFilesAction = new Action() {
        	public void run()
        	{
        		FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.MULTI);
        		String tempPath = fileDialog.open();
        		
        		if(tempPath == null)
        			return;
        		
        		final File path = new File(tempPath);
        		final String[] selectedFiles = fileDialog.getFileNames();
        		
        		EncryptedFolderDob parentFolder = root;
        		
        		if(!treeViewer.getSelection().isEmpty()) {
        			DroppedElement selectedElement = (DroppedElement)((TreeSelection)treeViewer.getSelection()).getFirstElement();
        			if(selectedElement instanceof EncryptedFileDob)
        				parentFolder = ((EncryptedFileDob)selectedElement).getParent();
        			else if(selectedElement instanceof EncryptedFolderDob)
        				parentFolder = (EncryptedFolderDob)selectedElement;
        		}
        		
        		final EncryptedFolderDob parentFolderFinal = parentFolder;
        		
        		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        		try {
        			progressDialog.open();
					progressDialog.run(false, false, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {	        		
			        		monitor.beginTask(Messages.TableView_ProgressMonitorDialog_Encrypt, selectedFiles.length+2);
			        		monitor.worked(1);

							monitor.subTask(Messages.TableView_ProgressMonitor_SubTast_Handler);
			        		if(fileDropHandler == null)
			        			fileDropHandler = new FileDropHandler();
			        		monitor.worked(1);
			        		
			        		fileDropHandler.processData(selectedFiles, path.getParent(), parentFolderFinal, monitor);
			        		
			        		monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					log.error(e.getMessage());
				} catch (InterruptedException e) {
					log.error(e.getMessage());
				}
				setSuccessfullyImported();
	            treeViewer.refresh();
	        	SideBarView.updateFileTypeFilter();
        	}
        };
        
        encryptFilesAction.setToolTipText(Messages.TableView_EncryptFile_Tooltip);
        encryptFilesAction.setImageDescriptor(Activator.getImageDescriptor("icons/encrypt.png"));
        
        newFolderAction = new Action() {
        	public void run()
        	{
        		InputDialog newFolderDialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.TableView_NewFolderDialog_Title, Messages.TableView_NewFolderDialog, "", new SteadyInputValidator());
        		if(newFolderDialog.open() == Window.OK) {
	        		EncryptedFolderDob parentFolder = root;
	        		if(!treeViewer.getSelection().isEmpty()) {
	        			DroppedElement selectedElement = (DroppedElement)((TreeSelection)treeViewer.getSelection()).getFirstElement();
	
	        			if(selectedElement instanceof EncryptedFolderDob)
	        				parentFolder = (EncryptedFolderDob)selectedElement;
	        			else if(selectedElement instanceof EncryptedFileDob)
	        				parentFolder = ((EncryptedFileDob)selectedElement).getParent();
	        		}
	        		parentFolder.addFolder(encryptedFolderDao.addFolder(new EncryptedFolder(newFolderDialog.getValue().trim(), new Date(System.currentTimeMillis()), "", parentFolder)));
    				statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), NLS.bind(Messages.StatusLine_FolderCreated, newFolderDialog.getValue()));
	        		treeViewer.refresh();
        		}
        	}
        };
        
        newFolderAction.setToolTipText(Messages.TableView_NewFolder_Tooltip);
        newFolderAction.setImageDescriptor(Activator.getImageDescriptor("icons/folder_add.png"));
        
    	exportSelectionAction = new Action() {
        	public void run()
        	{
        		if(!treeViewer.getSelection().isEmpty()) {
        			final TreeSelection currentSelection = (TreeSelection)treeViewer.getSelection();
            		
    				DirectoryDialog directoryDialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
    				directoryDialog.setText(Messages.TableView_ExportFileDialog_Title);
    				final String path = directoryDialog.open();

    				if(path!=null) {
	            		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	            		try {
	            			progressDialog.open();
							progressDialog.run(false, false, new IRunnableWithProgress() {
								public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
									monitor.beginTask(Messages.TableView_ProgressMonitorDialog_Decrypt, currentSelection.size()+2);
									monitor.worked(1);

									monitor.subTask(Messages.TableView_ProgressMonitor_SubTast_Handler);
									if(decryptHandler == null)
										decryptHandler = new DecryptHandler();
									monitor.worked(1);
									
									decryptHandler.processData(currentSelection, path, monitor);
									
									monitor.done();
									
								}
							});
						} catch (InvocationTargetException e) {
							log.error(e.getMessage());
						} catch (InterruptedException e) {
							log.error(e.getMessage());
						}

	    				statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), NLS.bind(Messages.StatusLine_Decrypted, remainingHddSpace()));
	        			treeViewer.refresh();
			        	SideBarView.updateFileTypeFilter();
    				}
        		}
        	}
        };

        exportSelectionAction.setText(Messages.TableView_ExportFile);
        exportSelectionAction.setToolTipText(Messages.TableView_ExportFile_Tooltip);
        exportSelectionAction.setImageDescriptor(Activator.getImageDescriptor("icons/export.png"));
        exportSelectionAction.setEnabled(false);
        
    	exportSelectionToOrigPathAction= new Action() {
        	public void run()
        	{
        		if(!treeViewer.getSelection().isEmpty()) {
        			final TreeSelection currentSelection = (TreeSelection)treeViewer.getSelection();

					if(!(((DroppedElement)currentSelection.getFirstElement()).getPath().length() == 0))
					{
						ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		        		try {
		        			progressDialog.open();
							progressDialog.run(false, false, new IRunnableWithProgress() {
								public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
									monitor.beginTask(Messages.TableView_ProgressMonitorDialog_Decrypt, 3);
									monitor.worked(1);

									monitor.subTask(Messages.TableView_ProgressMonitor_SubTast_Handler);
									if(decryptHandler == null)
										decryptHandler = new DecryptHandler();
									monitor.worked(1);
									
									decryptHandler.processData((DroppedElement)currentSelection.getFirstElement(), monitor);
									
									monitor.done();
									
								}
							});
						} catch (InvocationTargetException e) {
							log.error(e.getMessage());
						} catch (InterruptedException e) {
							log.error(e.getMessage());
						}
		
						statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), NLS.bind(Messages.StatusLine_Decrypted, remainingHddSpace()));
		    			treeViewer.refresh();
			        	SideBarView.updateFileTypeFilter();
					}
					else
					{
						MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.TableView_InfoDialog_Title, Messages.TableView_ErrorDialog_SCFolder);
						exportSelectionAction.run();
					}
        		}
        	}
        };

        exportSelectionToOrigPathAction.setText(Messages.TableView_ExportFileToOrigPath);
        exportSelectionToOrigPathAction.setToolTipText(Messages.TableView_ExportFileToOrigPath_Tooltip);
        exportSelectionToOrigPathAction.setImageDescriptor(Activator.getImageDescriptor("icons/export.png"));
        exportSelectionToOrigPathAction.setEnabled(false);
        
        deleteSelectionAction = new Action() {
        	public void run()
        	{
        		if(!treeViewer.getSelection().isEmpty()) {
        			final TreeSelection currentSelection = (TreeSelection)treeViewer.getSelection();
    		    	if(MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.TableView_WarningDialog_Title, Messages.TableView_WarningDialog_Delete))
	        		{
	                    ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	            		try {
	            			progressDialog.open();
	    					progressDialog.run(false, false, new IRunnableWithProgress() {
	    						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {	        		
	    							monitor.beginTask(Messages.TableView_ProgressMonitorDialog_Delete, currentSelection.size()+2);
	    							monitor.worked(1);

	    							monitor.subTask(Messages.TableView_ProgressMonitor_SubTast_Handler);
	    							if(deleteFileHandler == null)
	    								deleteFileHandler = new DeleteFileHandler();
	    							monitor.worked(1);
	    							
		    		        		deleteFileHandler.processData(currentSelection, monitor);
		    	                    	    		        		
		    		        		monitor.done();
	    						}
	    					});
	    				} catch (InvocationTargetException e) {
	    					log.error(e.getMessage());
	    					e.printStackTrace();
	    				} catch (InterruptedException e) {
	    					log.error(e.getMessage());
	    				}

	    				statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), NLS.bind(Messages.StatusLine_TDeleted, remainingHddSpace()));
	        			treeViewer.refresh();
	    	        	SideBarView.updateFileTypeFilter();
	        		}
        		}
        	}
        };
        
        deleteSelectionAction.setText(Messages.TableView_DeleteFile);
        deleteSelectionAction.setToolTipText(Messages.TableView_DeleteFile_Tooltip);
        deleteSelectionAction.setImageDescriptor(Activator.getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
        deleteSelectionAction.setEnabled(false);
        
        renameAction = new Action() {
            @SuppressWarnings("unchecked")
        	public void run()
        	{
            	if(!treeViewer.getSelection().isEmpty()) {
        			Iterator<DroppedElement> selectedElementsIterator = ((TreeSelection)treeViewer.getSelection()).iterator();
        			
        			while(selectedElementsIterator.hasNext())
        			{
        				DroppedElement selectedElement = selectedElementsIterator.next();
        				String nameWithoutExtension = selectedElement instanceof EncryptedFileDob ? selectedElement.getName().substring(0, selectedElement.getName().lastIndexOf(".")) : selectedElement.getName();
	        			InputDialog renameDialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.TableView_RenameDialog_Title, NLS.bind(Messages.TableView_RenameDialog, nameWithoutExtension), nameWithoutExtension, new SteadyInputValidator());
	        			
	        			if(renameDialog.open() == Window.OK) {
		        			if(selectedElement instanceof EncryptedFolderDob) {
		        				selectedElement.setName(renameDialog.getValue());
		        				encryptedFolderDao.updateFolder((EncryptedFolderDob)selectedElement);
		        			}
		        			else if(selectedElement instanceof EncryptedFileDob) {
		        				selectedElement.setName(renameDialog.getValue() + "." + ((EncryptedFileDob)selectedElement).getType());
		        				encryptedFileDao.updateFile((EncryptedFileDob)selectedElement);
		        			}
		        		}
	    			}
	        		treeViewer.refresh();
        		}
        	}
        };
        
        renameAction.setText(Messages.TableView_Rename);
        renameAction.setImageDescriptor(Activator.getImageDescriptor("icons/rename.png"));
        renameAction.setEnabled(false);
        
        expandAllAction = new Action() {
        	public void run()
        	{
        		treeViewer.expandAll();
        	}
        };
        
        expandAllAction.setToolTipText(Messages.TableView_ExpandAll_Tooltip);
        expandAllAction.setImageDescriptor(Activator.getImageDescriptor("icons/expandall.gif"));
        
        collapseAllAction = new Action() {
        	public void run()
        	{
        		for(TreeItem item : treeViewer.getTree().getItems()) {
        			if(item.getItems().length > 0)
        				treeViewer.collapseToLevel(item.getData(), 1);
        		}
        	}
        };
        
        collapseAllAction.setToolTipText(Messages.TableView_CollapseAll_Tooltip);
        collapseAllAction.setImageDescriptor(Activator.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
    }
    
    /**
     * Adding listeners to corresponding GUI elements
     */
    private void addListeners()
    {
    	treeViewer.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick(DoubleClickEvent event)
            {
            	exportSelectionAction.run();
            }
        });
    	
    	treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
    		
			public void selectionChanged(SelectionChangedEvent event) {
			
				boolean forbidden = false;
				DroppedElement elementCurrent;
				DroppedElement lastSelected = null;
				
				IStructuredSelection currentSelection = (IStructuredSelection) event.getSelection();

				exportSelectionAction.setEnabled(currentSelection.size() > 0 ? true : false);
				exportSelectionToOrigPathAction.setEnabled(currentSelection.size() == 1 ? true : false);
				deleteSelectionAction.setEnabled(currentSelection.size() > 0 ? true : false);
				renameAction.setEnabled(currentSelection.size() > 0 ? true : false);
				
				if (!oldSelection.isEmpty() && (currentSelection.size() > oldSelection.size()))
				{
				    // or iterate over all elements
				    for (Iterator<?> iteratorCurrent = currentSelection.iterator(); iteratorCurrent.hasNext();) 
				    {
				        elementCurrent = (DroppedElement) iteratorCurrent.next();
				        
				        if(!selectionContains(oldSelection, elementCurrent))
				        {
				        	lastSelected = elementCurrent;
				        }
				    }

				    if(lastSelected != null)
				    {
			    		if (isForbiddenCombo((DroppedElement) lastSelected, (DroppedElement)oldSelection.getFirstElement()))
				    	{
				    		treeViewer.setSelection(oldSelection, true);
				    		forbidden = true;
				    	}
				    }
				}

				if(!forbidden)
					oldSelection = (IStructuredSelection) event.getSelection();
				
				forbidden = false;
				
			}
		});
    }
    
    /**
     * Checks if this combination is forbidden, if true than no selection is allowed.
     * 
     * @param lastSelected
     * @param selected
     * @return boolean
     */
    private boolean isForbiddenCombo(DroppedElement lastSelected, DroppedElement selected){
    	
    	if(lastSelected.getParent().equals(selected.getParent()))
    	{
    		return false;
    	}
    	else if((lastSelected instanceof EncryptedFileDob) && 
    			(selected instanceof EncryptedFileDob))
    	{
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * Returns true if oldSelection contains element, false if not.
     * 
     * @param oldSelection
     * @param element
     * @return boolean
     */
    private boolean selectionContains(IStructuredSelection oldSelection, DroppedElement element){
    	
    	for (Iterator<?> iterator = oldSelection.iterator(); iterator.hasNext();)
    	{
    		DroppedElement oldElement = (DroppedElement) iterator.next();
    		
    		if(oldElement.getName().equals(element.getName())){
    			return true;
    		}    		
    	}
    	
    	return false;
    }
    
    /**
     * Creates the context menu for the table
     */
    private void createContextMenu()
    {
        MenuManager popupMenuManager = new MenuManager("PopupMenu");
        IMenuListener listener = new IMenuListener() { 
        public void menuAboutToShow(IMenuManager manager) {
            manager.add(exportSelectionAction);
            manager.add(exportSelectionToOrigPathAction);
            manager.add(renameAction);
            manager.add(deleteSelectionAction);
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        };
        popupMenuManager.addMenuListener(listener);
        popupMenuManager.setRemoveAllWhenShown(true);
        getSite().registerContextMenu(popupMenuManager, getSite().getSelectionProvider());
        Menu menu = popupMenuManager.createContextMenu(tree);
        tree.setMenu(menu);
    }
	
	/**
	 * Instantiate all Filters needed.
	 */
	private void createFiltersAndSorters()
	{
		searchFilter = new SearchFilter();
		dataTypeFilter = new DataTypeFilter();
		encryptionDateFilter = new EncryptionDateFilter();
	}
	
    /**
     * Returns the initial input for the table, right after application start.
     */
	private EncryptedFolderDob getInitialInput()
	{
    	root = new EncryptedFolderDob(0, "Root-Folder", new Date(System.currentTimeMillis()), "");

    	encryptedFolderDao = new EncryptedFolderDao();
    	encryptedFileDao = new EncryptedFileDao();
    	
    	getFolderContent(root);
    	
    	return root;
	}
	
	/**
	 * Needed by getInitialInput() to fill all (sub)folders with files.
	 */
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
		if(SideBarView.fileNameFilterString.length() == 0 
				&& SideBarView.encryptionDateFilterString.equalsIgnoreCase(Messages.Filter_NONE) 
				&& SideBarView.fileTypeFilterString.equalsIgnoreCase(Messages.Filter_NONE))
		{
			collapseAllAction.run();	
		}
		else if(SideBarView.fileNameFilterString.length() == 1
				&& SideBarView.encryptionDateFilterString.equalsIgnoreCase(Messages.Filter_NONE) 
				&& SideBarView.fileTypeFilterString.equalsIgnoreCase(Messages.Filter_NONE))
		{
			// do nothing
		} 
		else 
		{
			treeViewer.expandAll();
		}
	}

	public void setFocus() {}
	
	private static String remainingHddSpace()
	{
    	long space = (new File(Messages.getScFolder())).getUsableSpace()/1024/1024/1024;
    	return new BigDecimal(space).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public static void setSuccessfullyImported() {
		statusline.setMessage(Activator.getImageDescriptor("icons/info.png").createImage(), NLS.bind(Messages.StatusLine_Encrypted, remainingHddSpace()));
	}

}