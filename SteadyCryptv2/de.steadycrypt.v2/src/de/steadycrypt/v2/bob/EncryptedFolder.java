package de.steadycrypt.v2.bob;

import java.util.ArrayList;
import java.util.List;

import de.steadycrypt.v2.views.model.IDroppedElementVisitor;
import de.steadycrypt.v2.views.model.NullDeltaListener;

public class EncryptedFolder extends DroppedElement {
	
	protected List<EncryptedFolder> folders;
	protected List<EncryptedFile> files;
	
	private static IDroppedElementVisitor adder = new Adder();
	private static IDroppedElementVisitor remover = new Remover();
	
	public EncryptedFolder() {
		folders = new ArrayList<EncryptedFolder>();
		files = new ArrayList<EncryptedFile>();
	}
	
	private static class Adder implements IDroppedElementVisitor {

		public void visitFile(EncryptedFile encryptedFile, Object argument) {
			((EncryptedFolder) argument).addFile(encryptedFile);
		}

		public void visitFolder(EncryptedFolder folder, Object argument) {
			((EncryptedFolder) argument).addFolder(folder);
		}

	}

	private static class Remover implements IDroppedElementVisitor {

		public void visitFile(EncryptedFile encryptedFile, Object argument) {
			((EncryptedFolder) argument).removeFile(encryptedFile);
		}

		public void visitFolder(EncryptedFolder folder, Object argument) {
			((EncryptedFolder) argument).removeFolder(folder);
			folder.addListener(NullDeltaListener.getSoleInstance());
		}

	}
	
	public EncryptedFolder(String name) {
		this();
		this.name = name;
	}
	
	public List<EncryptedFolder> getFolders() {
		return folders;
	}
	
	protected void addFolder(EncryptedFolder folder) {
		folders.add(folder);
		folder.parent = this;
		fireAdd(folder);
	}
	
	protected void addFile(EncryptedFile encryptedFile) {
		files.add(encryptedFile);
		encryptedFile.parent = this;
		fireAdd(encryptedFile);
	}	
	
	public List<EncryptedFile> getFiles() {
		return files;
	}
	
	public void remove(DroppedElement toRemove) {
		toRemove.accept(remover, this);
	}
	
	protected void removeFile(EncryptedFile encryptedFile) {
		files.remove(encryptedFile);
		encryptedFile.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(encryptedFile);
	}
	
	protected void removeFolder(EncryptedFolder folder) {
		folders.remove(folder);
		folder.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(folder);	
	}

	public void add(DroppedElement toAdd) {
		toAdd.accept(adder, this);
	}
	
	/** Answer the total number of items the
	 * receiver contains. */
	public int size() {
		return getFiles().size() + getFolders().size();
	}

	public void accept(IDroppedElementVisitor visitor, Object passAlongArgument) {
		visitor.visitFolder(this, passAlongArgument);
	}

}
