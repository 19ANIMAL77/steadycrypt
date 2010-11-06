/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;

/**
 * Encrypter Object needed to en- and decrypt files.
 */
public class Crypter
{
	public static final String encryptionPath = System.getProperty("user.dir")+"/sc-files/";
	private Cipher ecipher;
	private Cipher dcipher;
	
	/**
	 * 
	 * @param key
	 */
	public Crypter(SecretKey key) {
		
		// Create an 8-byte initialization vector
		byte[] iv = new byte[] {
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
		};
		
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		
		try {
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			// CBC requires an initialization vector
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Buffer used to transport the bytes from one stream to another
	byte[] buf = new byte[1024];
	
	/**
	 * Encrypt the file from inputstream and write the new one into the outputstream
	 * 
	 * @param in
	 * @param out
	 * @throws IOException 
	 */
	public EncryptedFile encrypt(File currentFile, EncryptedFolderDob parent) throws IOException
	{		
		EncryptedFile encryptedFile = new EncryptedFile(currentFile, parent);
		
		InputStream input = new FileInputStream(encryptedFile.getPath());
		OutputStream output = new FileOutputStream(encryptionPath+encryptedFile.getFile());
	
		output = new CipherOutputStream(output, ecipher);
		
		// Read in the cleartext bytes and write to out to encrypt
		int numRead = 0;
		while ((numRead = input.read(buf)) >= 0)
		{
			output.write(buf, 0, numRead);
		}
		
		input.close();
		output.close();
		
		return encryptedFile;
	}
	
	/**
	 * Decrypt the File from inputstream and write the new file into the outputstream
	 * 
	 * @param in
	 * @param out
	 */
	public void decrypt(InputStream in, OutputStream out)
	{		
		try
		{
			// Bytes read from in will be decrypted
			in = new CipherInputStream(in, dcipher);
			
			// Read in the decrypted bytes and write the cleartext to out
			int numRead = 0;			
			while ((numRead = in.read(buf)) >= 0)
			{
				out.write(buf, 0, numRead);
			}
			out.close();
		}
		catch (java.io.IOException e) {
		}
	}
}