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
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.EncryptedFolder;
import de.steadycrypt.v2.core.FileDropHandler;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.views.ui.SteadyTableIdentifier;
import de.steadycrypt.v2.views.ui.SteadyTreeTableContentProvider;
import de.steadycrypt.v2.views.ui.SteadyTreeTableLabelProvider;

public class TreeTableView_old extends ViewPart {
	
	private static Logger log = Logger.getLogger(TreeTableView_old.class);
	public static String ID = "de.steadycrypt.v2.view.treeTable";

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private List<DroppedElement> model;

    private Action newInvoiceAction;

    private TreeViewer treeViewer;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public TreeTableView_old()
	{
//	    EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
//	    this.model = encryptedFileDao.getAllFiles();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		makeActions();
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        Composite content = new Composite(scrolledComposite, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        scrolledComposite.setContent(content);

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
		
		TableLayout tableLayout = new TableLayout();
		int nColumns = 3;
		int weight = 100 / nColumns;
		for (int i = 0; i < nColumns; i++) {
			tableLayout.addColumnData(new ColumnWeightData(weight));
		}

		tree.setLayout(tableLayout);
		
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
	        				
	    			model.addAll(fileDropHandler.processData(droppedFileInformation));
	            }
	        	treeViewer.refresh();
	        }
	    });
	}

	@Override
	public void setFocus() { }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private void makeActions()
    {
        newInvoiceAction = new Action() {
        	public void run() { }
        };
        
        newInvoiceAction.setText("Hallo");
        newInvoiceAction.setToolTipText("Tooltip");
        newInvoiceAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
            ISharedImages.IMG_TOOL_NEW_WIZARD));
    }
    
    private EncryptedFolder getInitialInput()
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

}
