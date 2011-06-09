/**
 * Date: 26.10.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SteadyKey implements Serializable {
	
	private static final long serialVersionUID = 4958585549423141207L;
	private SecretKey key = null;
	
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
