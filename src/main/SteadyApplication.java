package main;
/**
 * Date: 04.12.2009
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

import gui.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import core.DbManager;
import core.FileDropHandler;
import core.FileDroppedListener;
import core.PasswordInterpreter;

public class SteadyApplication extends JFrame implements FileDroppedListener {

	private static final long serialVersionUID = 5081868195123627492L;
	private static org.apache.log4j.Logger log = Logger.getLogger(SteadyApplication.class);
	private JPanel leftpanel;
	private JDropField dropfield;
	private JSteadyTaskPane taskPane;
	private JSteadyTable steadytable;
	private SteadyTableModel model;
	private JTopPanel topPanel;
	private JBottomPanel bottomPanel;
	private JScrollPane scrollPane;
	
	private static int loginCount = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		DbManager manager = DbManager.getInstance();
		manager.startDb();
		
		File f = new File(System.getProperty("user.dir")+"/sc-files/");
		if (f.exists()) {
			log.debug("sc-files folder exists");
			
			/**
			 * User got 3 chances to enter correct password. After that the application exits.
			 * Small protection against brute-force.
			 */
			while(loginCount < 4)
			{
				JSteadyLogin login = new JSteadyLogin("login");
				login.setVisible(true);
				
				try {
					manager.connectToDb("steady", PasswordInterpreter.createPassword(String.valueOf(login.getSteadyPassword())));
					break;
				}
				catch (SQLException sqle) {
		    		JOptionPane.showMessageDialog(login,
		    			    "Es wurde kein korrektes Password eingegeben! Versuch "+loginCount+" von 3.",
		    			    "Falsches Passwort",
		    			    JOptionPane.WARNING_MESSAGE);
		    		
		    		if(loginCount==3)
		    			System.exit(0);
		    		
		    		loginCount++;
		    		
		    		login.setVisible(false);
		    		login.dispose();
		    		DbManager.printSQLException(sqle);
				}
			}
		}
		
		else {
			log.debug("sc-files folder does not exist");
			
			JSteadyLogin login = new JSteadyLogin("register");
			login.setVisible(true);
			
			try {
				manager.initiateDb("steady", PasswordInterpreter.createPassword(String.valueOf(login.getSteadyPassword())));
			}
			catch (SQLException sqle) {
				DbManager.printSQLException(sqle);
			}
			f.mkdir();
		}
		
		@SuppressWarnings("unused")
		SteadyApplication app = new SteadyApplication();
		
		/**
		 * User stays but data of table 
		 * content will goes to hell
		 */
//		manager.resetDb();
		
	}
	
	/**
	 * Default constructor, runs at application start
	 * and initializes GUI
	 */
	public SteadyApplication() {
		
		FileDropHandler.addDroppedListener(this);
		
		Dimension compSize = new Dimension(625, 625);
		BorderLayout leftLayout = new BorderLayout();

		leftpanel = new JPanel();
		
		dropfield = new JDropField(new ImageIcon("img/logo_drop.jpg"));
		
		taskPane = new JSteadyTaskPane();
		leftpanel.setLayout(leftLayout);
		leftpanel.add(taskPane, BorderLayout.CENTER);
		leftpanel.add(dropfield, BorderLayout.SOUTH);

		model = new SteadyTableModel();
		steadytable = new JSteadyTable(model);
		
		topPanel = new JTopPanel();
		bottomPanel = new JBottomPanel();
		
		scrollPane = new JScrollPane(steadytable);	
		scrollPane.setPreferredSize(compSize);
		scrollPane.setMinimumSize(compSize);
		scrollPane.setMaximumSize(compSize);
		
		JSplitPane splitPane = new JSplitPane(
			     JSplitPane.HORIZONTAL_SPLIT, 
			     leftpanel,
			     scrollPane);
		
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerLocation(175);
		
		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing (WindowEvent we) {
				SteadyApplication.this.exit(0);
	        }
		});
		
		setSize(800,600);
        setTitle("SteadyCrypt");		
        setVisible(true);
		
	}
	
	/**
	 * Final station to refresh or inform all components that are interested
	 * in knowing when a file has been dropped.
	 * As Parameter no Event is given, because in this case a simple notification
	 * is okay. Normally "fire" sends also a Event-Object, which includes information
	 * about the change(s).
	 * 
	 */
	public void fileDropped() {
		try {
			model.getData();
			model.fireTableDataChanged();
			JSteadyTaskPane.updateTasks();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Default exit function
	 * 
	 * @param status The status is passed to the <code>System.exit</code> function
	 */
	public void exit(int status)
	{
		System.exit(status);
	}
}
