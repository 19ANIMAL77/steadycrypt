/**
 * Date: 13.12.2009
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class DecryptHandler {
	
	private static org.apache.log4j.Logger log = Logger.getLogger(DbManager.class);
	private static DecryptHandler INSTANCE = null;
	private static KeyManager keyman;
	private static Crypter crypter;
    
    
    /**
     * Singleton Pattern
     * Only DecryptHandler itself can call the constructor via getInstance()
     */
	private DecryptHandler(){
		log.debug("DecryptHandler instantiated");

		// Provide KeyManager
		DecryptHandler.keyman = KeyManager.getInstance();
		log.debug("Get KeyManager-Instance");
		
		// Create encrypter/decrypter
		DecryptHandler.crypter = new Crypter(keyman.getKey());
		log.debug("Crypt instance created");
	}
	
	
	/**
	 * Singleton Pattern for DecryptHandler
	 * @return instance of DecryptHandler
	 */
	public static DecryptHandler getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DecryptHandler();
		}
		return INSTANCE;
	}
    
	
	/**
	 * Method implements decryption process
	 * @param EncryptedFile
	 * @param OutputPath
	 */
	public void decrypt (String EncryptedFile, String OutputPath) {
		
		try {
			FileInputStream finput = new FileInputStream(EncryptedFile);
			FileOutputStream foutput = new FileOutputStream(OutputPath);
			
			crypter.decrypt(finput, foutput);
			
			try {
				finput.close();
				foutput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			
		} catch (FileNotFoundException e) {
			log.error(e);
		}
		

		
		log.debug("Decryption finished");
	}

}
