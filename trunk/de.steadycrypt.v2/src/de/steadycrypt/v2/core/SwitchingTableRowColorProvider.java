package de.steadycrypt.v2.core;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Eine ColorProvider Implementierung, die den Zeilenhintergrund in 
 * alternierenden Farben darstellt. 
 *
 * @author <a href="f.hardy@syzygy.net">Frank Hardy</a>
 */
public class SwitchingTableRowColorProvider implements ITableColorProvider {
    
    private final Color oddRowBackgroundColor = 
        Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
        /*Activator.getDefault().getColor(
                Activator.ColorId.ODD_TABLE_ROW_BACKGROUND);*/
    private final Color evenRowBackgroundColor =
        Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    
    private boolean isAtOddRow = false;
    
    // =========================================================================
    
    /** {@inheritDoc} */
    public Color getForeground(Object element, int columnIndex) {
        return null;
    }
    
    /** {@inheritDoc} */
    public Color getBackground(Object element, int columnIndex) {
        if(columnIndex == 0) {
            // Switch row number indicator
            this.isAtOddRow = !this.isAtOddRow;
        }
        return this.isAtOddRow ? 
                this.oddRowBackgroundColor : this.evenRowBackgroundColor;
    }
}
