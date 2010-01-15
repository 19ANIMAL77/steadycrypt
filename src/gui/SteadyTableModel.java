/**
 * Date: 13.12.2009
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package gui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import core.DbManager;

public class SteadyTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -218487078480256335L;
	private static org.apache.log4j.Logger log = Logger.getLogger(SteadyTableModel.class);
	
	private static String[] columns = { "Name", "Datei-Typ", "Groesse", "Verschluesselt am", "Datei-Pfad", "Verschluesselte Datei" };
	
	private Connection conn = DbManager.getConnection();
	private Statement stmt;
	private ResultSet rs;
	private int db_columns;
	private Vector<Vector<Object>> allRows;
	private Vector<?> row;
	
	public SteadyTableModel() {
		
		log.debug("SteadyTableModel is instanciated");
		
		try {
			getData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getData() throws SQLException {
		
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT name, type, size, enc_date, org_path, enc_name FROM content ORDER BY id desc");
		ResultSetMetaData myMetaData =  rs.getMetaData();
		db_columns = myMetaData.getColumnCount();
		allRows = new Vector<Vector<Object>>();
		while(rs.next()){
			Vector<Object> newRow = new Vector<Object>();
			for(int i = 1; i <= db_columns; i++){
				newRow.addElement(rs.getObject(i));
			}
		allRows.addElement(newRow);
		}
		
	}
	
	public int getColumnCount() {
		return db_columns;
	}

	public String getColumnName(int column) {
		return columns[column];
	}

	public int getRowCount() {
		return allRows.size();
	}

	/**
	 * From here the table gets the data it wants
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		
	     row = (Vector<?>) allRows.elementAt(rowIndex);
	     return row.elementAt(columnIndex);
		
	}
	
//	@SuppressWarnings("unchecked")
//	public Class getColumnClass(int col){
//		  return getValueAt(0, col).getClass();
//	}
	public boolean isCellEditable(int row, int col){ 
		return false;
	}
}
