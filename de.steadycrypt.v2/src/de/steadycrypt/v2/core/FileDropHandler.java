/**
 * Date: 06.11.2009
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.swing.TransferHandler;
import javax.swing.event.EventListenerList;

public class FileDropHandler extends TransferHandler {
	
	private static final long serialVersionUID = 446661369530414547L;

	private KeyManager keyman;
	private Analyzer analyzer;
	private Crypter crypter;
	
	protected static EventListenerList listenerList = new EventListenerList();
	
	public FileDropHandler() {

		// Provide KeyManager
		this.keyman = KeyManager.getInstance();
		System.out.println("Get KeyManager-Instance");
		
		// Create Analyzer
		this.analyzer = new Analyzer();
		System.out.println("Analyzer instance created");
		
	}

	public boolean canImport(TransferSupport supp) {
		// Checks whether the incoming information 'arrived' via a drop
		if (!supp.isDrop()){
			return false;
		}
		
		// return true only if the drop contains a list of files
		return supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
	}
	
	@SuppressWarnings("rawtypes")
	public boolean importData(TransferSupport supp) {
        if (!canImport(supp)) {
            return false;
        }
        
        try {
        	// Get connection parameters
        	Connection conn = DbManager.getConnection();
        	Statement s = conn.createStatement();
        	// Prepare INSERT statement
        	PreparedStatement psInsert = conn.prepareStatement("INSERT INTO CONTENT " +
        													   "(name, type, size, enc_date, org_path, enc_name)" +
        													   " VALUES (?, ?, ?, ?, ?, ?)");
        	// Prepare UPDATE statement
        	PreparedStatement psUpdate = conn.prepareStatement("UPDATE CONTENT SET name=?, type=?, size=?, enc_date=?, org_path=?, enc_name=? WHERE id=?");
        	
        	// fetch the Transferable
        	Transferable t = supp.getTransferable();

        	try {
            	List data = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
            	Iterator i = data.iterator();
            	
        		// Create encrypter/decrypter
        		this.crypter = new Crypter(keyman.getKey());
        		System.out.println("Crypt instance created");
            
            	while (i.hasNext()) {

            		File f = (File)i.next();
            		// Extend File object to FileInfo object to add some more information
            		FileInfo fi = new FileInfo(f.getPath());
            		System.out.println("Encrypt file: "+fi.getPath());
            		
            	
        			if (!analyzer.fileAlreadyExists(fi)) {        
        				
        				// Encrypt
        				FileInputStream finput = new FileInputStream(fi.getPath());
        				FileOutputStream foutput = new FileOutputStream(fi.getEncryptedFilePath()+fi.getEncryptedFileName());
        				
        				crypter.encrypt(finput, foutput);
        				
        				finput.close();
        				foutput.close();
        				
        				System.out.println("Encryption finished");
        					
            			// Writing file information into database
            			psInsert.setString(1, fi.getName());
        		        psInsert.setString(2, fi.getFileType());
        		        psInsert.setLong(3, fi.length());
        		        psInsert.setTimestamp(4, fi.getEncryptionTime());
        		        psInsert.setString(5, fi.getParent());
        		        psInsert.setString(6, fi.getEncryptedFileName());
        		        psInsert.executeUpdate();
                		conn.commit();

                		// comment it when finished TESTING!
                		// Reading all data stored in the steadyDB
//                		ResultSet rs = s.executeQuery("SELECT * FROM content ORDER BY id");
//               				
//                		while(rs.next()) {
//               				log.debug(rs.getString(1)+", "+rs.getString(2)+", "+rs.getString(3)+", "+rs.getString(4)+", "+rs.getString(5)+", "+rs.getString(6)+", "+rs.getString(7));
//                		}
                		
                	    // Attempt to delete source file
                	    boolean success = f.delete();

                	    if (!success)
                	    	System.out.println("Could not delete source file after encryption!");
                	    
                	    fi.setWritable(false);
                		
        			}
        			
        			else {
        				// User dropped a file, which accords in name and size to a file already stored
        				System.out.println("File already exists!");
        				// Select id and name of the stored file
        				ResultSet rs = s.executeQuery("SELECT id, enc_name FROM content WHERE name='"+ fi.getName() +"' AND size="+fi.length());
        				// Save id and name
        				rs.next();
        				int id = rs.getInt(1);
        				String encFileName = rs.getString(2);
        				
    					// Encrypt - overwrites encrypted old file
//        				FileInputStream finput = new FileInputStream(fi.getPath());
//        				FileOutputStream foutput = new FileOutputStream(fi.getEncryptedFilePath()+encFileName);
//        				
//        				crypter.encrypt(finput,foutput);
//        				
//        				finput.close();
//        				foutput.close();
        				
        				System.out.println("Encryption finished, old file overwrited");
    					
        				// Writing new file information into database
        				psUpdate.setString(1, fi.getName());
        				psUpdate.setString(2, fi.getFileType());
        				psUpdate.setLong(3, fi.length());
        				psUpdate.setTimestamp(4, fi.getEncryptionTime());
        				psUpdate.setString(5, fi.getParent());
        				psUpdate.setString(6, encFileName);
        				psUpdate.setInt(7, id);
        				psUpdate.executeUpdate();
            			conn.commit();
            			
                	    // Attempt to delete source file
                	    boolean success = f.delete();

                	    if (!success)
                	    	System.out.println("Could not delete source file after encryption!");

                		// Comment it when TESTING finished!
                		// Reading all data stored in the steadyDB
//                		rs = s.executeQuery("SELECT * FROM content ORDER BY id");
//               				
//                		while(rs.next()) {
//               				log.debug(rs.getString(1)+", "+rs.getString(2)+", "+rs.getString(3)+", "+rs.getString(4)+", "+rs.getString(5)+", "+rs.getString(6)+", "+rs.getString(7));
//                		}
        			}
        			
            		//Inform all added listeners
            		fireFileDroppedEvent();
            	}

            return true;
            
        	}
        
        	catch (UnsupportedFlavorException ufe) {
        		System.out.println("Unsupported File Flavor.");
        	}
          
        	catch (IOException ioe) {
        		System.out.println("Import failed:\n" + ioe);
        	}
        
        }
        catch (SQLException sqle) { DbManager.printSQLException(sqle); }
        
        return true;
	}
	
	/**
	 * Use this method to register a listener which is interested in being
	 * notified about drops.
	 * 
	 * @param listener
	 */
	public static synchronized void addDroppedListener(FileDroppedListener listener)
	{
		listenerList.add(FileDroppedListener.class, listener);
	}
	
	/**
	 * Use this method to remove a previous registered change listener
	 * from this object.
	 * 
	 * @param listener
	 */
	public static synchronized void removeDroppedListener(FileDroppedListener listener)
	{
		listenerList.remove(FileDroppedListener.class, listener);
	}
	
	/**
	 * Internally used method which is triggered if a drop has been
	 * occurred to inform the drop field.
	 */
	private void fireFileDroppedEvent()
	{
		Object[] listeners = listenerList.getListeners(FileDroppedListener.class);
		for(int i = listeners.length-1; i>=0; i-=1)
		{
			((FileDroppedListener)listeners[i]).fileDropped();
		}
	}
}
