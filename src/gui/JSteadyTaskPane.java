/**
 * Date: 20.12.2009
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import core.DbManager;

public class JSteadyTaskPane extends JPanel {
	
	private static final long serialVersionUID = -6502536261412659369L;
	private static JXTaskPane statistics;
	private JXTaskPane credits;
	private JXTaskPaneContainer taskPaneContainer;
	private static JLabel statisticLabel;
	private JLabel creditLabel;
	
	public JSteadyTaskPane(){
		
		Dimension compSize = new Dimension(175, 640);
		
		statistics = new JXTaskPane();
		credits = new JXTaskPane();
		
		// a container to put all JXTaskPane together
		taskPaneContainer = new JXTaskPaneContainer();
		taskPaneContainer.setPreferredSize(compSize);
		taskPaneContainer.setMinimumSize(compSize);
		taskPaneContainer.setMaximumSize(compSize);
		taskPaneContainer.setBackground(Color.BLACK);
		
		Connection conn = DbManager.getConnection();
    	Statement s;
    	int anzahl = 0;
    	int groesse = 0;
    	String einheit_g = "KB";
    	String einheit_s = "KB";
    	long space = (new File(System.getProperty("user.dir"))).getUsableSpace()/1024;

		try {
			s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT COUNT(id) as Anzahl FROM content");
			rs.next();
			anzahl = rs.getInt(1);
		} catch (SQLException sqle) {
			DbManager.printSQLException(sqle);
		}
		try {
			s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT SUM(size) as Groesse FROM content");
			rs.next();
			groesse = rs.getInt(1)/1024;
		} catch (SQLException sqle) {
			DbManager.printSQLException(sqle);
		}
		
		/**
		 * JXTaskPane Statistics
		 * When changing anything here, update updateTask() function too!
		 */
		statistics.setTitle("Statistiken");
		
		// add components to the Statistics taskPane
		if (groesse >= 10000) {
			groesse /= 1024;
			einheit_g = "MB";
		}
		if (space >= 10000) {
			space /= 1024;
			einheit_s = "MB";
		}
		if (space >= 10000) {
			space /= 1024;
			einheit_s = "GB";
		}
		statisticLabel = new JLabel("<HTML><BODY>Anzahl Dateien:<BR>"+anzahl+"<BR><BR>" +
				"Datenvolumen:<BR>ca. "+groesse+" "+einheit_g+"<BR><BR>" +
				"Verf&uuml;gbar:<BR>ca. "+space+" "+einheit_s+"<BR>" +
				"</BODY></HTML>");
		
		statistics.add(statisticLabel);
		 
		taskPaneContainer.add(statistics);		
		this.add(taskPaneContainer);
				
		
		/**
		 * JXTaskPane Credits
		 */
		credits.setTitle("Credits");
		
		// add components to the Credits taskPane
		creditLabel = new JLabel("<HTML><BODY>J&ouml;rg Harr<BR>Marvin Hoffmann<BR>HdM Stuttgart</BODY></HTML>");
		credits.add(creditLabel);
		 
		taskPaneContainer.add(credits);		
		this.add(taskPaneContainer);
		
	}
	
	public static void updateTasks() {
		Connection conn = DbManager.getConnection();
    	Statement s;
    	int anzahl = 0;
    	int groesse = 0;
    	String einheit_g = "KB";
    	String einheit_s = "KB";
    	long space = (new File(System.getProperty("user.dir"))).getUsableSpace()/1024;

		try {
			s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT COUNT(id) as Anzahl FROM content");
			rs.next();
			anzahl = rs.getInt(1);
		} catch (SQLException sqle) {
			DbManager.printSQLException(sqle);
		}
		try {
			s = conn.createStatement();
			ResultSet rs = s.executeQuery("SELECT SUM(size) as Groesse FROM content");
			rs.next();
			groesse = rs.getInt(1)/1024;
		} catch (SQLException sqle) {
			DbManager.printSQLException(sqle);
		}
		
		/**
		 * Update statistics
		 */
		if (groesse >= 10000) {
			groesse /= 1024;
			einheit_g = "MB";
		}
		if (space >= 10000) {
			space /= 1024;
			einheit_s = "MB";
		}
		if (space >= 10000) {
			space /= 1024;
			einheit_s = "GB";
		}
		
		statisticLabel.setText("<HTML><BODY>Anzahl Dateien:<BR>"+anzahl+"<BR><BR>" +
				"Datenvolumen:<BR>ca. "+groesse+" "+einheit_g+"<BR><BR>" +
				"Verf&uuml;gbar:<BR>ca. "+space+" "+einheit_s+"<BR>" +
				"</BODY></HTML>");
		
	}
	
}
