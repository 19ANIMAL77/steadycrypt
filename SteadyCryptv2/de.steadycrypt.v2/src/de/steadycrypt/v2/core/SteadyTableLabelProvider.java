/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public abstract class SteadyTableLabelProvider extends LabelProvider 
        implements ITableLabelProvider, ITableColorProvider {
	
    private final SteadyTableColorProvider colorProvider =
        new SteadyTableColorProvider();
    
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
    
    public Color getForeground(Object element, int columnIndex) {
        return this.colorProvider.getForeground(element, columnIndex);
    }
    
    public Color getBackground(Object element, int columnIndex) {
        return this.colorProvider.getBackground(element, columnIndex);
    }
}
