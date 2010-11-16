/**
 * Date: 16.11.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.steadycrypt.v2.bob.DroppedElement;

public class TreeDragSourceListener implements DragSourceListener {
	
	private static Logger log = Logger.getLogger(TreeDragSourceListener.class);
	private List<TreeItem> dragSourceItems;
	private Tree tree;
	
	public static List<DroppedElement> draggedDroppedElements;
	
	public TreeDragSourceListener(Tree tree)
	{		
		this.tree = tree;	    
	    this.dragSourceItems = new ArrayList<TreeItem>();
	    TreeDragSourceListener.draggedDroppedElements = new ArrayList<DroppedElement>();
	}

	public void dragStart(DragSourceEvent event)
	{
		TreeItem[] selection = this.tree.getSelection();
		if (selection.length > 0 && selection[0].getItemCount() >= 0) {
			event.doit = true;
			for(TreeItem currentSelection : selection) {
				dragSourceItems.add(currentSelection);
			}
		}
		else {
			event.doit = false;
		}
	}

	public void dragSetData(DragSourceEvent event)
	{
//		log.debug("NOT VALID TYPE #3");
		// TODO: was muss event.data sein??
//		event.data = (DroppedElement)dragSourceItem[0].getData();
		event.data = "sc";
		for(TreeItem currentDragSourceItem : dragSourceItems) {
			TreeDragSourceListener.draggedDroppedElements.add((DroppedElement)currentDragSourceItem.getData());
		}
	}

	public void dragFinished(DragSourceEvent event)
	{
		if (event.detail == DND.DROP_MOVE) {
			for(TreeItem currentDragSourceItem : dragSourceItems) {
				currentDragSourceItem.dispose();
			}	
		}
	  
		dragSourceItems.clear();
	  
		log.debug("Drag finished...");
	}
}
