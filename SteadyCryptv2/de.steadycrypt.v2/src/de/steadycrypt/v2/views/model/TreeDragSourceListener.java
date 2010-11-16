/**
 * Date: 16.11.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

import org.apache.log4j.Logger;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeDragSourceListener implements DragSourceListener {
	
	private static Logger log = Logger.getLogger(TreeDragSourceListener.class);
	private final TreeItem[] dragSourceItem;
	private Tree tree;
	
	public TreeDragSourceListener(Tree tree) {
		
		this.tree = tree;	    
	    dragSourceItem = new TreeItem[1];
	}

	/**
	 * 
	 */
	public void dragStart(DragSourceEvent event) {
		
	  	log.debug("NOT VALID TYPE #1");
  	  
		TreeItem[] selection = this.tree.getSelection();
		if (selection.length > 0 && selection[0].getItemCount() == 0) 
		{
			event.doit = true;
			dragSourceItem[0] = selection[0];
			log.debug("NOT VALID TYPE doit = true #1");
		}
		else 
		{
			event.doit = false;
			log.debug("NOT VALID TYPE doit = false #1");
		}
	  
		log.debug("NOT VALID TYPE #2");
		
	}

	/**
	 * 
	 */
	public void dragSetData(DragSourceEvent event) {
		
		log.debug("NOT VALID TYPE #3");
		// TODO: was muss event.data sein??
		event.data = dragSourceItem[0].getText();
		log.debug("NOT VALID TYPE #4");
		
	}

	/**
	 * 
	 */
	public void dragFinished(DragSourceEvent event) {
		
		log.debug("NOT VALID TYPE #5");
		if (event.detail == DND.DROP_MOVE)
			dragSourceItem[0].dispose();
	  
		dragSourceItem[0] = null;
	  
		log.debug("NOT VALID TYPE #6");
		
	}
}
