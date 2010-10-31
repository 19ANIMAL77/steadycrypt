/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.core.Crypter;
import de.steadycrypt.v2.core.KeyManager;
import de.steadycrypt.v2.core.SteadyTableLabelProvider;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.views.model.SteadyTableIdentifier;

public class TableView extends ViewPart {
	
	private static Logger log = Logger.getLogger(TableView.class);
	public static String ID = "de.steadycrypt.v2.view.table";

    // =========================================================================

	public SimpleDateFormat sdf;
	
	private KeyManager keyman;
	private Crypter crypter;
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();

    private Action newInvoiceAction;

    private TableViewer tableViewer;

    // =========================================================================

	public TableView()
	{
	    this.sdf = new SimpleDateFormat(Messages.DATE_FORMAT);
	    this.keyman = KeyManager.getInstance();
	    this.crypter = new Crypter(keyman.getKey());
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

        final Table table = new Table(content, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL);
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
                    case FILE:
                        text = file.getFile();
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
                return encryptedFileDao.getAllFiles();
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

            public void dispose() { }
        });
        
        this.tableViewer.setInput(this.encryptedFileDao.getAllFiles());
		
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
	             	String[] droppedFileInformation = (String[]) event.data;
	             
	                for (int i = 0 ; i < droppedFileInformation.length ; i++)
		            {
	                	EncryptedFile newEncryptedFile = new EncryptedFile(new File(droppedFileInformation[i]));
		            	System.out.println(newEncryptedFile.getPath()+newEncryptedFile.getName());
		            	
		            	// Encrypt
        				try
        				{
        					FileInputStream finput = new FileInputStream(newEncryptedFile.getPath());
        					FileOutputStream foutput = new FileOutputStream(EncryptedFile.encryptionPath+newEncryptedFile.getFile());
        				
        					crypter.encrypt(finput, foutput);
        				
	        				finput.close();
	        				foutput.close();
        				}
        				catch(IOException e)
        				{
        					
        				}
        				
        				log.debug("Encryption finished");
        					
            			encryptedFileDao.addFile(newEncryptedFile);
		            }
	            }
	        	tableViewer.refresh();
	        }
	    });
	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub
	}

    // -------------------------------------------------------------------------

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

}
