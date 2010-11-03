/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob;

import java.sql.Date;

import de.steadycrypt.v2.views.model.DeltaEvent;
import de.steadycrypt.v2.views.model.IDeltaListener;
import de.steadycrypt.v2.views.model.IModelVisitor;
import de.steadycrypt.v2.views.model.NullDeltaListener;

public abstract class DroppedElement {
	
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
	private EncryptedFolder parent;
	
	protected String name;
	protected Date date;
	protected String path;
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public DroppedElement(String name, Date date, String path) {
		this.name = name;
		this.date = date;
		this.path = path;
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}
	
	public void removeListener(IDeltaListener listener) {
		if(this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}
	
	public abstract void accept(IModelVisitor visitor, Object passAlongArgument);

	protected void fireAdd(Object added) {
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) {
		listener.remove(new DeltaEvent(removed));
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public EncryptedFolder getParent() {
		return parent;
	}

	public void setParent(EncryptedFolder parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
