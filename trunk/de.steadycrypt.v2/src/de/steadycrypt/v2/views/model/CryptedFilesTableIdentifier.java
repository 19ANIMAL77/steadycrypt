/**
 * Date: 26.10.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

public enum CryptedFilesTableIdentifier {

	NAME(70), TYPE(70), SIZE(100), DATE(120), PATH(80), FILE(120);
	
	public final int columnWidth;
	
	private CryptedFilesTableIdentifier(int width)
	{
		this.columnWidth = width;
	}	
}
