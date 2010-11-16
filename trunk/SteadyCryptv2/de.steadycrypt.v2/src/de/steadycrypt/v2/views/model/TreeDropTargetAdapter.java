/**
 * Date: 16.11.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

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
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.core.FileDropHandler;
import de.steadycrypt.v2.views.SideBarView;

public class TreeDropTargetAdapter extends DropTargetAdapter {
	
	private static Logger log = Logger.getLogger(TreeDropTargetAdapter.class);

	private FileTransfer fileTransfer = FileTransfer.getInstance();
	private Tree tree;
	private EncryptedFolderDob dragOverFolder;
	private FileDropHandler fileDropHandler = new FileDropHandler();
	private TreeViewer treeViewer;
	
	
	public TreeDropTargetAdapter(Tree tree, TreeViewer treeViewer){
		
		this.tree = tree;
		this.treeViewer = treeViewer;
		
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
		if (event.item != null) 
		{
			TreeItem item = (TreeItem)event.item;
//			DroppedElement dragOverElement = (DroppedElement)item.getData();
//			log.debug(dragOverElement.getName());					
			
			Point pt = PlatformUI.getWorkbench().getDisplay().map(null, this.tree, event.x, event.y);
			Rectangle bounds = item.getBounds();
			if (pt.y < bounds.y + bounds.height/3) 
			{
				event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
			} 
			else if (pt.y > bounds.y + 2*bounds.height/3) 
			{
				event.feedback |= DND.FEEDBACK_INSERT_AFTER;
			} 
			else 
			{
				event.feedback |= DND.FEEDBACK_SELECT;
			}
		}
    }
  
    public void drop(DropTargetEvent event)
    {
    	
    	if (fileTransfer.isSupportedType(event.currentDataType))
        {
    		String[] droppedFileInformation = (String[]) event.data;
    		
    		log.debug(droppedFileInformation.length + " Files dropt.");
    		
			TreeItem item = (TreeItem)event.item;
			dragOverFolder = ((DroppedElement)item.getData()).getParent();
			log.debug("Parent-Folder: "+dragOverFolder);
    		
			try
			{
				fileDropHandler.processData(droppedFileInformation, dragOverFolder);
			}
			catch(Exception e)
			{
				log.error("Error at proccessing dropped data. " + e);
			}
        }
    	
    	treeViewer.refresh();
    	SideBarView.updateFileTypeFilter();
    }
	
	
}
