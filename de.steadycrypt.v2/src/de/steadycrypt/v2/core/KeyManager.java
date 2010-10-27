/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.crypto.SecretKey;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class KeyManager {
	
	private SteadyKey steadykey = null;
	private static KeyManager INSTANCE = null;
	
	final EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate");
	final EntityManager em = emf.createEntityManager();
	
	private KeyManager(){
		System.out.println("KeyManager instantiated");
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
	 * When starting SteadyCrypt for the first time, create a new key and save it to table SteadyKey via Hibernate.
	 */
	public void writeKeyToDB() {
		
		final SteadyKey steadyKey = new SteadyKey();
		em.persist(steadyKey);
				
	}
	
	/**
	 * Read SteadyKey-object from database and store in private steadyKey attribute.
	 */
	public void readKeyFromDB() throws SQLException {
		
		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		Connection conn = DbManager.getConnection();
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM SteadyKey");
		
		if(!rs.next()) {
			writeKeyToDB();
		}		
		
		@SuppressWarnings("rawtypes")
		final List keys = em.createQuery("select sk from SteadyKey as sk").getResultList();
		System.out.println(keys.size() + " key(s) found");
		for (final Object sk : keys) {
			steadykey = (SteadyKey) sk;
		}
		
		tx.commit();
		em.close();
		emf.close();		
	}
	
	public SecretKey getKey() {
		
		if (steadykey == null) {
			try { readKeyFromDB(); }
			catch (SQLException sqle) { DbManager.printSQLException(sqle); }
		}
		
		return steadykey.getKey();
	}
	
}
