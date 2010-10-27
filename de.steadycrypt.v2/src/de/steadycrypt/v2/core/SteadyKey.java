/**
 * Date: 27.11.2009
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.log4j.Logger;

@Entity
public class SteadyKey {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "STEADYKEY_ID")
	private long id;
	
	@Column(name="SECRETKEY")
	private SecretKey key = null;
	
	private static org.apache.log4j.Logger log = Logger.getLogger(SteadyKey.class);

	public SteadyKey(){
		
		/** 
		 * Provides a container for the SecretKey object. This is necessary
		 * to persist the key object into the derby by using Hibernate.
		 * Now it is possible to annotations. It is not possible to add them in the SecretKey class because
		 * its hidden in the library. Extending SecretKey is not possible either.
		 */
		this.createKey();
	}
	
	/**
	 * Creates SecretKey Object
	 * 128 bit
	 * AES Algorithm
	 */
	private void createKey() {
		
		KeyGenerator kgen = null;
		
		try {
			kgen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		kgen.init(128);
		
		this.key = kgen.generateKey();	
	}
	
	
	/**
	 * Method returns the SecretKey object which was created by the JCE-framework and stored in the SteadyKey-"container".
	 * @return SecretKey
	 */
	public SecretKey getKey() {
		
		return this.key;
		
	}


}
