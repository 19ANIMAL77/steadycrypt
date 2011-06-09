/**
 * Date: 26.10.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.crypto.SecretKey;

import org.apache.log4j.Logger;

public class KeyManager {
	
	private static SteadyKey steadykey = null;
	
	private static Logger log = Logger.getLogger(KeyManager.class);
	private static KeyManager INSTANCE = null;
	
	private static final int STEADYKEY_ID = 42;
	private static final String WRITE_OBJECT_SQL = "INSERT INTO SteadyKey(STEADYKEY_ID, SECRETKEY) VALUES (?, ?)";
	private static final String READ_OBJECT_SQL = "SELECT SECRETKEY FROM SteadyKey WHERE STEADYKEY_ID = ?";
	
	private KeyManager(){
		log.debug("KeyManager instantiated");
	}
	
	/**
	 * Singleton Pattern for KeyManager
	 * @return instance of KeyManager
	 */
	public static KeyManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new KeyManager();
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
	 * When starting SteadyCrypt for the first time, create a new key and save it to table SteadyKey.
	 */
	public static void writeKeyToDB() {
		
		final SteadyKey steadyKey = new SteadyKey();
		
		try {
			KeyManager.writeJavaObject(DbManager.getConnection(), steadyKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		KeyManager.steadykey = steadyKey;
				
	}
	
	/**
	 * Write object into datebase.
	 * @param conn
	 * @param SteadyKey
	 * @throws Exception
	 */
	public static void writeJavaObject(Connection conn, SteadyKey key) throws Exception {

		PreparedStatement pstmt = conn.prepareStatement(WRITE_OBJECT_SQL);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(key);
		InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

		// set input parameters
		pstmt.setInt(1, STEADYKEY_ID);
		pstmt.setBlob(2, inputStream);
		pstmt.executeUpdate();

		pstmt.close();
		conn.commit();
		
		System.out.println("writeJavaObject: done serializing: " + key.getClass().getName());
	}
	
	/**
	 * 
	 * @param conn
	 * @param id
	 * @return SteadyKey
	 * @throws Exception
	 */
	private static void readJavaObject(Connection conn, int id) throws Exception {
		
		PreparedStatement pstmt = conn.prepareStatement(READ_OBJECT_SQL);
		pstmt.setInt(1, id);
		ResultSet rs = pstmt.executeQuery();
		
		if(!rs.next()) {
			log.error("No Key-Object found in SteadyKey table");
			// InteractiveSplashHandler checks "isFirstStart"
			return;
		}
		
		InputStream is = rs.getBinaryStream(1);
		
		ObjectInputStream ois = null;

		ois = new ObjectInputStream(is);
		
		KeyManager.steadykey = (SteadyKey) ois.readObject();

		rs.close();
		pstmt.close();
		conn.commit();
		
		System.out.println("readJavaObject: done de-serializing: " + KeyManager.steadykey.getClass().getName());
	}
	
	public SecretKey getKey() {
		
		if (KeyManager.steadykey == null) {
			
			try {
				readJavaObject(DbManager.getConnection(), STEADYKEY_ID);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		return KeyManager.steadykey.getKey();
	}
}
