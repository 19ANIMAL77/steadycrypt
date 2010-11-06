/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.ui;

public enum SteadyTableIdentifier {

	NAME(280), TYPE(65), SIZE(90), DATE(100), PATH(320);
	
	public final int columnWidth;
	
	private SteadyTableIdentifier(int width)
	{
		this.columnWidth = width;
	}	
}
