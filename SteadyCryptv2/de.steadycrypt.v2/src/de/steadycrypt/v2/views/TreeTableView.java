package de.steadycrypt.v2.views;

import java.sql.Date;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.EncryptedFolder;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.views.ui.FileFolderSorter;
import de.steadycrypt.v2.views.ui.NoArticleSorter;
import de.steadycrypt.v2.views.ui.SteadyTableIdentifier;
import de.steadycrypt.v2.views.ui.SteadyTreeTableContentProvider;
import de.steadycrypt.v2.views.ui.SteadyTreeTableLabelProvider;
import de.steadycrypt.v2.views.ui.ThreeItemFilter;

/**
 * Insert the type's description here.
 * @see ViewPart
 */
public class TreeTableView extends ViewPart {
	
	public static String ID = "de.steadycrypt.v2.view.treeTable";
	
	protected TreeViewer treeViewer;
	protected Text text;
	protected SteadyTreeTableLabelProvider labelProvider;
	
	protected Action atLeatThreeItems;
	protected Action filesFoldersAction, noArticleAction;
	protected Action addFileAction, removeAction;
	protected ViewerFilter atLeastThreeFilter;
	protected ViewerSorter filesFoldersSorter, noArticleSorter;
	
	protected EncryptedFolder root;
	
	/**
	 * The constructor.
	 */
	public TreeTableView() {
	}

	/*
	 * @see IWorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent)
	{
		/* Create a grid layout object so the text and treeviewer
		 * are layed out the way I want. */
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);
		
		/* Create a "label" to display information in. I'm
		 * using a text field instead of a lable so you can
		 * copy-paste out of it. */
		text = new Text(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
		// layout the text field above the treeviewer
		GridData layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(layoutData);
		
		// Create the tree viewer as a child of the composite parent
		treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);		
		
		// Anpassungen für TreeTable
		Tree tree = this.treeViewer.getTree();
		
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
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
		layoutData = new GridData();
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		treeViewer.getControl().setLayoutData(layoutData);
		
		// Create menu, toolbars, filters, sorters.
		createFiltersAndSorters();
		createActions();
		createMenus();
		createToolbar();
		hookListeners();
		
		treeViewer.setContentProvider(new SteadyTreeTableContentProvider());
		labelProvider = new SteadyTreeTableLabelProvider();
		treeViewer.setLabelProvider(labelProvider);
		
		treeViewer.setInput(getInitalInput());
		treeViewer.expandAll();
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
					text.setText("");
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
					text.setText(toShow.toString());
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
		if (treeViewer.getSelection().isEmpty()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
		/* Tell the tree to not redraw until we finish
		 * removing all the selected children. */
		treeViewer.getTree().setRedraw(false);
		for (Iterator<?> iterator = selection.iterator(); iterator.hasNext();) {
			DroppedElement droppedElement = (DroppedElement) iterator.next();
			EncryptedFolder parent = droppedElement.getParent();
			parent.remove(droppedElement);
		}
		treeViewer.getTree().setRedraw(true);
	}
	
	protected void createMenus() {
		IMenuManager rootMenuManager = getViewSite().getActionBars().getMenuManager();
		rootMenuManager.setRemoveAllWhenShown(true);
		rootMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillMenu(mgr);
			}
		});
		fillMenu(rootMenuManager);
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
	
	protected void createToolbar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(addFileAction);
		toolbarManager.add(removeAction);
	}
	
	
	public EncryptedFolder getInitalInput()
	{
    	EncryptedFolder root = new EncryptedFolder("Root-Folder", new Date(System.currentTimeMillis()), "C:");
    	EncryptedFolder sub1 = new EncryptedFolder("Sub-Folder-1", new Date(System.currentTimeMillis()), "C:");
    	EncryptedFolder sub2 = new EncryptedFolder("Sub-Folder-2", new Date(System.currentTimeMillis()), "C:");
    	EncryptedFolder subsub = new EncryptedFolder("Sub-Sub-Folder", new Date(System.currentTimeMillis()), "C:");
    	
    	root.add(sub1);
    	root.add(sub2);
    	sub2.add(subsub);
    	
    	EncryptedFileDao efd = new EncryptedFileDao();
    	
    	for(Object ef : efd.getAllFiles())
    	{
    		sub1.addFile((EncryptedFile)ef);
    	}
    	
    	return root;
	}

	/*
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {}

}
