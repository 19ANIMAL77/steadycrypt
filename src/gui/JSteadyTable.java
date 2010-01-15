/**
 * Date: 08.12.2009
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;

import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;

import core.DbManager;
import core.DecryptHandler;

public class JSteadyTable extends JXTable implements MouseListener {
	
	private static final long serialVersionUID = -8291922467578014173L;
	private static org.apache.log4j.Logger log = Logger.getLogger(JSteadyTable.class);
	private SteadyTableModel model;
	
	public JSteadyTable(SteadyTableModel model) {
		
		super(model);		
		this.model = model;
		addMouseListener(this);
		setColumnControlVisible(true);
				
		this.initTable();
		setLookAndFeel();
	}
	
	@SuppressWarnings("unchecked")
	private void initTable() {

	    setPreferredScrollableViewportSize(new Dimension(625, 70));
	    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    setEditable(false);

	    Highlighter simpleStriping = HighlighterFactory.createSimpleStriping();
	    setHighlighters(simpleStriping);

	    Comparator numberComparator = new Comparator() {
	        public int compare(Object o1, Object o2) {
	        	Long d1 = (Long) o1;
	        	Long d2 = (Long) o2;
	            return d1.compareTo(d2);
	        }
	    };
	    
	    getColumnExt("Groesse").setComparator(numberComparator);
		getColumnExt("Groesse").setVisible(false);
		getColumnExt("Datei-Pfad").setVisible(false);
		getColumnExt("Verschluesselte Datei").setVisible(false);
	    packAll();
	    
	}
	
	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
		}
	}

	/**
	 * Triggered when a row in the table is double clicked.
	 */
	public void mouseClicked(MouseEvent e) {
		{
			if (e.getClickCount() == 2) {
				int viewRowIndex = ((JXTable)e.getSource()).getSelectedRow();
				int modelRowIndex = this.convertRowIndexToModel(viewRowIndex);
				String decfilename = (String)(getModel()).getValueAt(modelRowIndex, 0);
				String encfile = (String)(getModel()).getValueAt(modelRowIndex, 5);
				String ext = (decfilename.lastIndexOf(".")==-1)?"":decfilename.substring(decfilename.lastIndexOf("."),decfilename.length());
				String filename;
				
				JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
				fc.setDialogTitle("Zielverzeichnis waehlen");
				fc.setSelectedFile(new File(System.getProperty("user.dir")+"/"+decfilename));
				int rc = fc.showDialog(null, "Datei speichern");
				
				if (rc == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.getPath().lastIndexOf(".")==-1) {
						filename = file.getPath();
					}
					else {
						filename = (file.getPath().lastIndexOf(".")==-1)?"":file.getPath().substring(0,file.getPath().lastIndexOf("."));
					}
					
					(DecryptHandler.getInstance()).decrypt(System.getProperty("user.dir")+"/sc-files/"+encfile, filename+ext);
					
					log.debug(System.getProperty("user.dir")+"/sc-files/"+encfile);
					log.debug(filename+ext);
					
					(new File(System.getProperty("user.dir")+"/sc-files/"+encfile)).delete();
					
					Statement s = null;
			        try {
						s = DbManager.getConnection().createStatement();
			            
				        s.execute("delete from content where enc_name='"+encfile+"'");

				        DbManager.getConnection().commit();   
					} catch (SQLException sqle) {
						DbManager.printSQLException(sqle);
					}
					
					try {
						model.getData();
						model.fireTableDataChanged();
						JSteadyTaskPane.updateTasks();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
				else {
					log.debug("File chooser cancel button clicked");
				}
			}
			else if ((e.getModifiers() & InputEvent.BUTTON3_MASK)!=0) {
				// get the coordinates of the mouse click
				Point p = e.getPoint();
	 
				// get the row index that contains that coordinate
				int row = rowAtPoint( p );
	 
				// Get the ListSelectionModel of the JTable
				ListSelectionModel model = getSelectionModel();
	 
				// set the selected interval of rows. Using the "rowNumber"
				// variable for the beginning and end selects only that one row.
				model.setSelectionInterval( row, row );
				System.out.println(row);
			}
		}
		
	}

	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

}
