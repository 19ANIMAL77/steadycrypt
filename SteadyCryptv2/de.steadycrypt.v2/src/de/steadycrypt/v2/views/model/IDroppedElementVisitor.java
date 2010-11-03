package de.steadycrypt.v2.views.model;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.EncryptedFolder;



public interface IDroppedElementVisitor {
	
	public void visitFolder(EncryptedFolder encryptedFolder, Object passAlongArgument);
	public void visitFile(EncryptedFile encryptedFile, Object passAlongArgument);
	
}
