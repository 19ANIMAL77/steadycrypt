/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class SteadyTableColorProvider implements ITableColorProvider {
    
    private final Color oddRowBackgroundColor = 
        Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);

    private final Color evenRowBackgroundColor =
        Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    
    private boolean isAtOddRow = false;
    
    // =========================================================================
    
    public Color getForeground(Object element, int columnIndex) {
        return null;
    }
    
    public Color getBackground(Object element, int columnIndex) {
        if(columnIndex == 0) {

            this.isAtOddRow = !this.isAtOddRow;
        }
        return this.isAtOddRow ? 
                this.oddRowBackgroundColor : this.evenRowBackgroundColor;
    }
}
