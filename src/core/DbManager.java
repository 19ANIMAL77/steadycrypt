/**
 * Date: 20.11.2009
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package core;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DbManager {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(DbManager.class);
	private static DbManager INSTANCE = null;
    private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private static String protocol = "jdbc:derby:steadyDB";
    private static Connection conn = null;
    
    
    /**
     * Singleton Pattern
     * Only DbManager itself can call the constructor via getInstance()
     */
	private DbManager(){
		log.debug("DbManager instantiated");
	}
	
	
	/**
	 * Singleton Pattern for DbManager
	 * @return instance of DbManager
	 */
	public static DbManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DbManager();
		}
		return INSTANCE;
	}
	
	
	/**
	 * Override clone. Clone is in Singleton not supported.
	 */
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
    

	/**
     * Loads Derby Drivers
     */
    public void startDb() {
    	/**
         *  The JDBC driver is loaded by loading its class.
         *  If you are using JDBC 4.0 (Java SE 6) or newer, JDBC drivers may
         *  be automatically loaded, making this code optional.
         *
         *  In an embedded environment, this will also start up the Derby
         *  engine (though not any databases), since it is not already
         *  running.
         */
        try {
            Class.forName(driver).newInstance();
            log.info("Loaded the appropriate driver");
        } catch (ClassNotFoundException cnfe) {
            log.error("\nUnable to load the JDBC driver " + driver);
            log.error("Please check your CLASSPATH.");
        } catch (InstantiationException ie) {
            log.error("\nUnable to instantiate the JDBC driver " + driver);
        } catch (IllegalAccessException iae) {
            log.error("\nNot allowed to access the JDBC driver " + driver);
        }
    }
    
    
    /**
     * This method is called only once, when starting the program for the first time.
     * Creating Derby-Database "steadyDB", creating table "content" according to the type chosen here.
     * @throws SQLException 
     */
    public void initiateDb(String username, String password) throws SQLException {
    	Properties props = new Properties();
        props.put("derby.connection.requireAuthentication", "true");
        props.put("derby.authentication.provider", "BUILTIN");
        props.put("user", username);
        props.put("password", password);
        props.put("encryptionProvider", "org.bouncycastle.jce.provider.BouncyCastleProvider");
        props.put("encryptionAlgorithm", "AES/CBC/NoPadding");        
            
        // The directory steadyDB will be created under the current directory
            
        DbManager.conn = DriverManager.getConnection(protocol + ";create=true;dataEncryption=true;" + "bootPassword="+password, props);
        log.trace("Connected to and created database");
        DbManager.conn.setAutoCommit(false);
        
        Statement s = null;
        s = DbManager.conn.createStatement();
            
        // Creating table "content" which contains information about the stored files 
        s.execute("CREATE TABLE content (" +
        		"id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
        		"name VARCHAR(250)," +
        		"type VARCHAR(30)," +
        		"size BIGINT," +
        		"enc_date TIMESTAMP," +
        		"org_path LONG VARCHAR," +
        		"enc_name VARCHAR(40))");
        log.info("Created table CONTENT");
            
        // Creating table "keys" which contains the SecretKey object(s)
        s.execute("CREATE TABLE SteadyKey (" +
        		"STEADYKEY_ID bigint NOT NULL GENERATED ALWAYS AS IDENTITY," +
        		"SECRETKEY varchar(255) for bit data," +
        		"PRIMARY KEY (STEADYKEY_ID))");
        log.info("Created table KEYS");
        DbManager.conn.commit();        
    }
    
    
    /**
     * Establishes a database connection
     */
    public void connectToDb(String username, String password) throws SQLException {
    	Properties props = new Properties();
        props.put("derby.connection.requireAuthentication", "true");
        props.put("derby.authentication.provider", "BUILTIN");
        props.put("user", username);
        props.put("password", password);
        props.put("encryptionProvider", "org.bouncycastle.jce.provider.BouncyCastleProvider");
        props.put("encryptionAlgorithm", "AES/CBC/NoPadding");

        DbManager.conn = DriverManager.getConnection(protocol + ";dataEncryption=true;" + "bootPassword="+password, props);
        log.trace("Connected to database");
        DbManager.conn.setAutoCommit(false);
    }
    
    
    /**
     * Implemented for testing. Allows to caller to drop the "content" table.
     */
    public void resetDb() {
    	try {
        	Statement s = null;
            s = DbManager.conn.createStatement();
            s.execute("delete from content");
            DbManager.conn.commit();
        }
        catch (SQLException sqle) { printSQLException(sqle); }
        log.info("Table Content deleted");
    }
	
    
    /**
	 * @return Connection
	 */
	public static Connection getConnection() {
		return conn;
	}

	
    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the Exception.
        while (e != null)
        {
            log.error("\n----- SQLException -----");
            log.error("\n  SQL State:  " + e.getSQLState());
            log.error("\n  Error Code: " + e.getErrorCode());
            log.error("\n  Message:    " + e.getMessage());
            e = e.getNextException();
        }
    }

}