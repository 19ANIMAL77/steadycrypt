/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.ui;

import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import de.steadycrypt.v2.bob.DroppedElement;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;
import de.steadycrypt.v2.views.model.DeltaEvent;
import de.steadycrypt.v2.views.model.IDeltaListener;

@SuppressWarnings("rawtypes")
public class SteadyTreeTableContentProvider implements ITreeContentProvider, IDeltaListener {
	
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;

	@Override
	public void dispose() { }

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null)
		{
			removeListenerFrom((EncryptedFolderDob)oldInput);
		}
		if(newInput != null)
		{
			addListenerTo((EncryptedFolderDob)newInput);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child box of the given box. */
	protected void removeListenerFrom(EncryptedFolderDob folder)
	{
		folder.removeListener(this);
		for (Iterator iterator = folder.getFolders().iterator(); iterator.hasNext();)
		{
			EncryptedFolderDob aFolder = (EncryptedFolderDob) iterator.next();
			removeListenerFrom(aFolder);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(EncryptedFolderDob folder)
	{
		folder.addListener(this);
		for (Iterator iterator = folder.getFolders().iterator(); iterator.hasNext();)
		{
			EncryptedFolderDob aFolder = (EncryptedFolderDob) iterator.next();
			addListenerTo(aFolder);
		}
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if(parentElement instanceof EncryptedFolderDob)
		{
			EncryptedFolderDob encryptedFolder = (EncryptedFolderDob)parentElement;
			return concat(encryptedFolder.getFolders().toArray(), encryptedFolder.getFiles().toArray());
		}
		return EMPTY_ARRAY;
	}
	
	protected Object[] concat(Object[] folders, Object[] files)
	{
		Object[] both = new Object[folders.length + files.length];
		System.arraycopy(folders, 0, both, 0, folders.length);
		System.arraycopy(files, 0, both, folders.length, files.length);		
		return both;
	}

	@Override
	public Object getParent(Object element)
	{
		if(element instanceof DroppedElement)
		{
			return ((DroppedElement)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;
	}

	@Override
	public void add(DeltaEvent event)
	{
		Object folder = ((DroppedElement)event.receiver()).getParent();
		viewer.refresh(folder, false);
	}

	@Override
	public void remove(DeltaEvent event)
	{
		add(event);		
	}

}
