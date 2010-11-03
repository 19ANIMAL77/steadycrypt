/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.core.FileDropHandler;
import de.steadycrypt.v2.core.SteadyTableLabelProvider;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.views.model.SteadyTableIdentifier;

public class TableView extends ViewPart {
	
	private static Logger log = Logger.getLogger(TableView.class);
	public static String ID = "de.steadycrypt.v2.view.table";

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private SimpleDateFormat sdf = new SimpleDateFormat(Messages.DATE_FORMAT);
	private Table table;
    private List<EncryptedFileDob> model;

    private ToolBarManager toolBarManager;
    private Action exportSelectionAction;
    private Action selectAllAction;

    private TableViewer tableViewer;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public TableView()
	{
	    EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	    this.model = encryptedFileDao.getAllFiles();
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

        this.toolBarManager = new ToolBarManager();
        this.toolBarManager.add(this.exportSelectionAction);
        this.toolBarManager.add(this.selectAllAction);

        ToolBar toolbar = toolBarManager.createControl(content);
        toolbar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

        Label horizontalSeparator = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        this.table = new Table(content, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL);
        table.setHeaderVisible(true);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 200;
        table.setLayoutData(gridData);
        
        for(SteadyTableIdentifier identifier : SteadyTableIdentifier.values())
        {
            new TableColumn(table, SWT.NONE).setText(Messages.getSteadyTableColumnTitle(identifier));
            table.getColumn(identifier.ordinal()).setWidth(identifier.columnWidth);
        }

        this.tableViewer = new TableViewer(table);
        this.tableViewer.setLabelProvider(new SteadyTableLabelProvider()
        {
            public String getColumnText(Object element, int columnIndex)
            {
                EncryptedFile file = (EncryptedFile) element;

                SteadyTableIdentifier identifier = SteadyTableIdentifier.values()[columnIndex];

                String text = null;
                switch(identifier)
                {
                    case DATE:
                        text = sdf.format(file.getDate());
                        break;
                    case NAME:
                        text = file.getName();
                        break;
                    case PATH:
                        text = file.getPath();
                        break;
                    case SIZE:
                        text = Long.toString(file.getSize());
                        break;
                    case TYPE:
                        text = file.getType();
                        break;
                        
                    default:
                        assert false : identifier + " is not a legal identifier!"; //$NON-NLS-1$
                }
                return text;
            }

            public Image getColumnImage(Object element, int columnIndex)
            {
                return null;
            }
        });
        
        this.tableViewer.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements(Object inputElement)
            {                	
                return model.toArray();
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

            public void dispose() { }
        });
        
        this.tableViewer.setInput(this.model.toArray());
		
	    DropTarget dropTarget = new DropTarget(table, DND.DROP_COPY | DND.DROP_DEFAULT);    
	        
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
	        	tableViewer.refresh();
	        }
	    });
	    
	    final Button exportFilesButton = new Button(content, SWT.FLAT);
	    exportFilesButton.setText(Messages.TableView_ExportFile);
        gridData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
        exportFilesButton.setLayoutData(gridData);
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub
	}

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
        		tableViewer.refresh();
        	}
        };
        
        exportSelectionAction.setText(Messages.TableView_ExportFile);
        exportSelectionAction.setToolTipText(Messages.TableView_ExportFile_Tooltip);
        exportSelectionAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        
        selectAllAction = new Action() {
        	public void run()
        	{
        		Table table = tableViewer.getTable();
        		for(TableItem ti : table.getItems()) {
        			ti.setChecked(true);
        		}
        	}
        };
        
        selectAllAction.setText(Messages.TableView_SelectAll);
        selectAllAction.setToolTipText(Messages.TableView_SelectAll_Tooltip);
        selectAllAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
    }

}
