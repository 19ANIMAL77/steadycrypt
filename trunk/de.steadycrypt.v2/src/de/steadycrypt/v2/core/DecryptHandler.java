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

public class DecryptHandler {
	
	private static DecryptHandler INSTANCE = null;
	private static KeyManager keyman;
	private static Crypter crypter;
    
    
    /**
     * Singleton Pattern
     * Only DecryptHandler itself can call the constructor via getInstance()
     */
	private DecryptHandler(){
		System.out.println("DecryptHandler instantiated");

		// Provide KeyManager
		DecryptHandler.keyman = KeyManager.getInstance();
		System.out.println("Get KeyManager-Instance");
		
		// Create encrypter/decrypter
		DecryptHandler.crypter = new Crypter(keyman.getKey());
		System.out.println("Crypt instance created");
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
			System.out.println(e);
		}
		

		
		System.out.println("Decryption finished");
	}

}
