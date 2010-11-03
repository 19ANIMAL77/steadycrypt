/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.EncryptedFolder;

public interface IDroppedElementVisitor
{
	public void visitFolder(EncryptedFolder folder, Object passAlongArgument);
	public void visitFile(EncryptedFile file, Object passAlongArgument);
}
