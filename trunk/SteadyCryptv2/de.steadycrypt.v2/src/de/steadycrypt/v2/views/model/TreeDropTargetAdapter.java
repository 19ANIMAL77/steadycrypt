/**
 * Date: 16.11.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.core.FileDropHandler;
import de.steadycrypt.v2.dao.EncryptedFileDao;
import de.steadycrypt.v2.dao.EncryptedFolderDao;
import de.steadycrypt.v2.views.SideBarView;

public class TreeDropTargetAdapter extends DropTargetAdapter {
	
	private static Logger log = Logger.getLogger(TreeDropTargetAdapter.class);

	private FileTransfer fileTransfer = FileTransfer.getInstance();
	private Tree tree;
	private TreeViewer treeViewer;
	private EncryptedFolderDob root;
	private EncryptedFolderDob dragOverFolder;

	private EncryptedFolderDao encryptedFolderDao = new EncryptedFolderDao();
	private EncryptedFileDao encryptedFileDao = new EncryptedFileDao();
	private FileDropHandler fileDropHandler = new FileDropHandler();
	
	public TreeDropTargetAdapter(Tree tree, TreeViewer treeViewer, EncryptedFolderDob dragOverFolder)
	{
		this.tree = tree;
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
//			DroppedElement dragOverElement = (DroppedElement)item.getData();
//			log.debug(dragOverElement.getName());
			
			Point pt = PlatformUI.getWorkbench().getDisplay().map(null, tree, event.x, event.y);
			Rectangle bounds = item.getBounds();
			if (pt.y < bounds.y + bounds.height/3) {
				event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
			} 
			else if (pt.y > bounds.y + 2*bounds.height/3) {
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
    		String[] droppedFileInformation = (String[]) event.data;
    		
    		log.debug(droppedFileInformation.length + " Files dropt.");
    		
			TreeItem item = null;
			
			if(event.item != null) {
				item = (TreeItem)event.item;
					
				if(item.getData() instanceof EncryptedFolderDob) {
					dragOverFolder = (EncryptedFolderDob) item.getData();
				}
				else {
					dragOverFolder = ((DroppedElement)item.getData()).getParent();
				}
			}
			
			log.debug("Parent-Folder: "+dragOverFolder.getName());
    		
			try {
				fileDropHandler.processData(droppedFileInformation, dragOverFolder);
			}
			catch(Exception e) {
				log.error("Error at proccessing dropped data. " + e);
			}
        }
    	
    	// This part handles Drag'N'Drop within the tree
    	else 
    	{
        	// DraggedElement within the tree
        	List<DroppedElement> draggedElements = TreeDragSourceListener.draggedDroppedElements;
        	
        	for(DroppedElement draggedElement : draggedElements) {
	    		TreeItem item = null; 
				
				if(event.item != null)
				{
					item = (TreeItem)event.item;
						
					if(item.getData() instanceof EncryptedFolderDob) {
						dragOverFolder = (EncryptedFolderDob) item.getData();
					}
					else {
						dragOverFolder = ((DroppedElement)item.getData()).getParent();
					}
				}
	        	
				if(draggedElement instanceof EncryptedFileDob) {
					EncryptedFileDob draggedFile = (EncryptedFileDob) draggedElement;
		    		draggedElement.getParent().removeFile(draggedFile);
		    		draggedFile.setParent(dragOverFolder);
		    		encryptedFileDao.updateFile(draggedFile);
		    		dragOverFolder.addFile(draggedFile);
				}
				else if(draggedElement instanceof EncryptedFolderDob) {
					EncryptedFolderDob draggedFolder = (EncryptedFolderDob) draggedElement;
		    		draggedElement.getParent().removeFolder(draggedFolder);
		    		draggedFolder.setParent(dragOverFolder);
		    		encryptedFolderDao.updateFolder(draggedFolder);
		    		dragOverFolder.addFolder(draggedFolder);
				}
	    		dragOverFolder=root;
        	}
        }
    	
    	TreeDragSourceListener.draggedDroppedElements.clear();
    	treeViewer.refresh();
    	SideBarView.updateFileTypeFilter();
    }
}
