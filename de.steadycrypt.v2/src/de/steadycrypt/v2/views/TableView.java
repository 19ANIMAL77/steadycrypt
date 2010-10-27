/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.businessmodel.EncryptedFile;
import de.steadycrypt.v2.core.SteadyTableLabelProvider;
import de.steadycrypt.v2.views.model.SteadyTableIdentifier;

public class TableView extends ViewPart {
	
	public static String ID = "de.steadycrypt.v2.view.table";

    // =========================================================================

    private Action newInvoiceAction;

    private TableViewer tableViewer;

    public SimpleDateFormat sdf;
    List<EncryptedFile> files;

	public TableView()
	{
	    this.sdf = new SimpleDateFormat(Messages.DATE_FORMAT);

    	this.files = new ArrayList<EncryptedFile>();
    	
    	for(int i=0 ; i < 10 ; i++) {
    		EncryptedFile file = new EncryptedFile();
    		
    		file.setDate(new Date(0l));
    		file.setFile(i+"ksahldfkjhsaldf.sc");
    		file.setName(i+".jpg");
    		file.setPath("pfad/"+i+".jpg");
    		file.setSize(new Long(i));
    		file.setType(".jpg");
    		
    		files.add(file);
    	}
	}

	@Override
	public void createPartControl(Composite parent)
	{
		makeActions();
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        Composite content = new Composite(scrolledComposite, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        scrolledComposite.setContent(content);

        Label separatorLabel = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Table table = new Table(content, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL);
        table.setHeaderVisible(true);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.heightHint = 200;
        table.setLayoutData(gridData);
        
        for(SteadyTableIdentifier identifier : SteadyTableIdentifier.values())
        {
            new TableColumn(table, SWT.NONE).setText(Messages.getSteadyTableColumnTitle(identifier));
            table.getColumn(identifier.ordinal()).setWidth(identifier.columnWidth);
        }

        this.tableViewer = new TableViewer(table);
        this.tableViewer.setLabelProvider(new SteadyTableLabelProvider()
        {
            public String getColumnText(Object element, int columnIndex)
            {
                EncryptedFile file = (EncryptedFile) element;

                SteadyTableIdentifier identifier = SteadyTableIdentifier.values()[columnIndex];

                String text = null;
                switch(identifier)
                {
                    case DATE:
                        text = sdf.format(file.getDate());
                        break;
                    case FILE:
                        text = file.getFile();
                        break;
                    case NAME:
                        text = file.getName();
                        break;
                    case PATH:
                        text = file.getPath();
                        break;
                    case SIZE:
                        text = Long.toString(file.getSize());
                        break;
                    case TYPE:
                        text = file.getType();
                        break;
                    default:
                        assert false : "Unbekannter Identifier: " //$NON-NLS-1$
                            + identifier;
                }
                return text;
            }

            public Image getColumnImage(Object element, int columnIndex)
            {
                return null;
            }
        });
        this.tableViewer.setContentProvider(new IStructuredContentProvider()
        {
            public Object[] getElements(Object inputElement)
            {                	
                return files.toArray();
            }

            /** {@inheritDoc} */
            public void inputChanged(Viewer viewer, Object oldInput,
                                            Object newInput)
            {
            }

            /** {@inheritDoc} */
            public void dispose()
            {
            }
        });
        this.tableViewer.setInput(files.toArray());
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

    // -------------------------------------------------------------------------

    private void makeActions()
    {
        newInvoiceAction = new Action()
        {
            public void run()
            {
            }
        };
        newInvoiceAction.setText("Hallo");
        newInvoiceAction.setToolTipText("Tooltip");
        newInvoiceAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
            ISharedImages.IMG_TOOL_NEW_WIZARD));
    }

}
