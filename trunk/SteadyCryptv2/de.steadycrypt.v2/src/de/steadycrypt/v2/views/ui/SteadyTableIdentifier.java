/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.ui;

public enum SteadyTableIdentifier {

	NAME(391), TYPE(100), SIZE(70), DATE(95);
	
	public final int columnWidth;
	
	private SteadyTableIdentifier(int width)
	{
		this.columnWidth = width;
	}
}
