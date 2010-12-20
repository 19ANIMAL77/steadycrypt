/**
 * Date: 16.11.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.core.FileDropHandler;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.EncryptedFolderDao;
import de.steadycrypt.v2.views.SideBarView;
import de.steadycrypt.v2.views.TreeTableView;

public class TreeDropTargetAdapter extends DropTargetAdapter {
	
	private static Logger log = Logger.getLogger(TreeDropTargetAdapter.class);

	private FileTransfer fileTransfer = FileTransfer.getInstance();
	private TreeViewer treeViewer;
	private EncryptedFolderDob root;
	private EncryptedFolderDob dragOverFolder;

	private EncryptedFolderDao encryptedFolderDao = new EncryptedFolderDao();
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	private FileDropHandler fileDropHandler;
	
	public TreeDropTargetAdapter(TreeViewer treeViewer, EncryptedFolderDob dragOverFolder)
	{
		this.treeViewer = treeViewer;
		this.root = dragOverFolder;
		this.dragOverFolder = root;
	}
	
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
		event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
		
		if (event.item != null) {
			TreeItem item = (TreeItem)event.item;
			DroppedElement dragOverElement = (DroppedElement)item.getData();
			
			if (dragOverElement instanceof EncryptedFileDob) {
				event.feedback |= DND.FEEDBACK_INSERT_AFTER;
			} 
			else {
				event.feedback |= DND.FEEDBACK_SELECT;
			}
		}
    }
  
    public void drop(DropTargetEvent event)
    {
    	// Handle Drag'N'Drop from Desktop into tree
    	if (fileTransfer.isSupportedType(event.currentDataType))
        {
    		final String[] droppedFileInformation = (String[]) event.data;
    		
			TreeItem item = null;
			
			if(event.item != null) {
				item = (TreeItem)event.item;
					
				if(item.getData() instanceof EncryptedFolderDob) {
					dragOverFolder = (EncryptedFolderDob) item.getData();
					item.setExpanded(true);
				}
				else {
					dragOverFolder = ((DroppedElement)item.getData()).getParent();
				}
			}
    		
			try {
				ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        		progressDialog.open();
				progressDialog.run(false, false, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {	        		
						monitor.beginTask(Messages.TableView_ProgressMonitorDialog_Encrypt, droppedFileInformation.length);
		        		
		        		if(fileDropHandler == null)
							fileDropHandler = new FileDropHandler();
						fileDropHandler.processData(droppedFileInformation, dragOverFolder, monitor);
	                    	    		        		
		        		monitor.done();
					}
				});
				TreeTableView.setSuccessfullyImported();
			}
			catch(Exception e) {
				log.error("Error at proccessing dropped data. " + e);
			}
			// set dragOverFolder back to root for next drop. Otherwise dropping to root wont be possible.
    		dragOverFolder=root;
        }
    	
    	// This part handles Drag'N'Drop within the tree
    	else 
    	{
        	// DraggedElement within the tree
        	List<DroppedElement> draggedElements = TreeDragSourceListener.draggedDroppedElements;
        	int itemsNotMoved = 0;
        	
        	draggedElements : for(DroppedElement draggedElement : draggedElements) {
	    		TreeItem item = null; 
				
				if(event.item != null)
				{
					item = (TreeItem)event.item;
						
					if(item.getData() instanceof EncryptedFolderDob) {
						dragOverFolder = (EncryptedFolderDob) item.getData();
						item.setExpanded(true);
					}
					else {
						dragOverFolder = ((DroppedElement)item.getData()).getParent();
					}
				}
	        	
				if(draggedElement instanceof EncryptedFileDob) {
					EncryptedFileDob draggedFile = (EncryptedFileDob) draggedElement;
					draggedFile.getParent().removeFile(draggedFile);
		    		draggedFile.setParent(dragOverFolder);
		    		encryptedFileDao.updateFile(draggedFile);
		    		dragOverFolder.addFile(draggedFile);
				}
				else if(draggedElement instanceof EncryptedFolderDob) {
					EncryptedFolderDob draggedFolder = (EncryptedFolderDob) draggedElement;
					
					EncryptedFolderDob currentDragOverFolder = dragOverFolder;
					
					while(!currentDragOverFolder.equals(root)) {
						if(currentDragOverFolder.equals(draggedFolder)) {
							itemsNotMoved++;
							continue draggedElements;
						}
						currentDragOverFolder = currentDragOverFolder.getParent();
					}
					
					draggedFolder.getParent().removeFolder(draggedFolder);
		    		draggedFolder.setParent(dragOverFolder);
		    		encryptedFolderDao.updateFolder(draggedFolder);
		    		dragOverFolder.addFolder(draggedFolder);
				}
				// set dragOverFolder back to root for next drop. Otherwise dropping to root wont be possible.
	    		dragOverFolder=root;
        	}
        	
        	if(itemsNotMoved > 0) {
        		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.TableView_InfoDialog_Title, NLS.bind(Messages.TableView_InfoDialog_CantMove, itemsNotMoved));
        		System.out.println(itemsNotMoved);
        	}
        	
        	itemsNotMoved = 0;
        }
    	
    	TreeDragSourceListener.draggedDroppedElements.clear();
    	treeViewer.refresh();
    	SideBarView.updateFileTypeFilter();
    }
}
