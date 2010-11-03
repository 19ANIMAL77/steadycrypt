package de.steadycrypt.v2.bob;

import de.steadycrypt.v2.views.model.DeltaEvent;
import de.steadycrypt.v2.views.model.IDeltaListener;
import de.steadycrypt.v2.views.model.IDroppedElementVisitor;
import de.steadycrypt.v2.views.model.NullDeltaListener;

public abstract class DroppedElement {
	
	protected EncryptedFolder parent;
	protected String name;
	protected String authorGivenName, authorSirName;	
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
	
	protected void fireAdd(Object added) {
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) {
		listener.remove(new DeltaEvent(removed));
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public EncryptedFolder getParent() {
		return parent;
	}
	
	/* The receiver should visit the toVisit object and
	 * pass along the argument. */
	public abstract void accept(IDroppedElementVisitor visitor, Object passAlongArgument);
	
	public String getName() {
		return name;
	}
	
	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}
	
	public DroppedElement(String title, String authorGivenName, String authorSirName) {
		this.name = title;
		this.authorGivenName = authorGivenName;
		this.authorSirName = authorSirName;
	}
	
	public DroppedElement() {
	}	
	
	public void removeListener(IDeltaListener listener) {
		if(this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}

	public String authorGivenName() {
		return authorGivenName;
	}


	public String authorSirName() {
		return authorSirName;
	}

	public String getTitle() {
		return name;
	}


}
