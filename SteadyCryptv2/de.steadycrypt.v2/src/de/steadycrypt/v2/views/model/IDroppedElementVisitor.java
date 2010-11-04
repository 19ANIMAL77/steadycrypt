/**
 * Date: 03.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;

public interface IDroppedElementVisitor
{
	public void visitFolder(EncryptedFolderDob folder, Object passAlongArgument);
	public void visitFile(EncryptedFileDob file, Object passAlongArgument);
}
