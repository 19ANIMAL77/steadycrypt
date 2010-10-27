package de.steadycrypt.v2.core;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * DOCME de.gfeh.adcontrol.invoicing.general.AbstractBasicTableColorProvider
 *
 * @author <a href="f.hardy@syzygy.net">Frank Hardy</a>
 *
 */
public abstract class AbstractTableLabelProvider extends LabelProvider 
        implements ITableLabelProvider, ITableColorProvider {
	
    private final SwitchingTableRowColorProvider colorProvider =
        new SwitchingTableRowColorProvider();
    
    /** {@inheritDoc} */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
    
    /** {@inheritDoc} */
    public Color getForeground(Object element, int columnIndex) {
        return this.colorProvider.getForeground(element, columnIndex);
    }
    
    /** {@inheritDoc} */
    public Color getBackground(Object element, int columnIndex) {
        return this.colorProvider.getBackground(element, columnIndex);
    }
}
