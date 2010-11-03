/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.bob;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import de.steadycrypt.v2.views.model.IModelVisitor;
import de.steadycrypt.v2.views.model.NullDeltaListener;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EncryptedFolder extends DroppedElement {

	private List folders;
	private List files;

	private static IModelVisitor adder = new Adder();
	private static IModelVisitor remover = new Remover();

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/**
	 * Used when a new folder was dropped.
	 * 
	 * @param newFile
	 */
	public EncryptedFolder(File newFile) {
		super(newFile.getName(), new Date(System.currentTimeMillis()), newFile
				.getPath());
		this.folders = new ArrayList();
		this.files = new ArrayList();
	}

	/**
	 * Used when the content table is being read.
	 * 
	 * @param name
	 * @param date
	 * @param path
	 */
	public EncryptedFolder(String name, Date date, String path) {
		super(name, date, path);
		this.folders = new ArrayList();
		this.files = new ArrayList();
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public List getFolders() {
		return folders;
	}

	public void setFolders(List folders) {
		this.folders = folders;
	}

	public List getFiles() {
		return files;
	}

	public void setFiles(List files) {
		this.files = files;
	}

	public void addFolder(EncryptedFolder encryptedFolder)
	{
		this.folders.add(encryptedFolder);
		encryptedFolder.setParent(this);
		fireAdd(encryptedFolder);
	}

	public void addFile(EncryptedFile encryptedFile)
	{
		this.files.add(encryptedFile);
		encryptedFile.setParent(this);
		fireAdd(encryptedFile);
	}

	public void removeFolder(EncryptedFolder encryptedFolder)
	{
		this.folders.remove(encryptedFolder);
		encryptedFolder.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(encryptedFolder);
	}

	public void removeFile(EncryptedFile encryptedFile)
	{
		this.files.remove(encryptedFile);
		encryptedFile.addListener(NullDeltaListener.getSoleInstance());
		fireRemove(encryptedFile);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public void add(DroppedElement toAdd) {
		toAdd.accept(adder, this);
	}

	public void remove(DroppedElement toRemove)
	{
		toRemove.accept(remover, this);
	}

	public int size()
	{
		return getFolders().size() + getFiles().size();
	}
	
	public void accept(IModelVisitor visitor, Object passAlongArgument)
	{
		visitor.visitFolder(this, passAlongArgument);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private static class Adder implements IModelVisitor
	{
		public void visitFolder(EncryptedFolder folder, Object argument)
		{
			((EncryptedFolder) argument).addFolder(folder);
		}

		public void visitFile(EncryptedFile file, Object argument)
		{
			((EncryptedFolder) argument).addFile(file);
		}
	}

	private static class Remover implements IModelVisitor
	{
		public void visitFolder(EncryptedFolder folder, Object argument)
		{
			((EncryptedFolder) argument).removeFolder(folder);
		}

		public void visitFile(EncryptedFile file, Object argument)
		{
			((EncryptedFolder) argument).removeFile(file);
		}
	}

}
