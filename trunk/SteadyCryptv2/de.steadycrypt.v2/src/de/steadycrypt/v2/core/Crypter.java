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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.bob.EncryptedFile;
import de.steadycrypt.v2.bob.dob.EncryptedFileDob;
import de.steadycrypt.v2.bob.dob.EncryptedFolderDob;

/**
 * Crypter Object needed to en- and decrypt files.
 */
public class Crypter
{
	private Cipher ecipher;
	private Cipher dcipher;
	
	/**
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
	
	/**
	 * Encrypt the file from inputstream and write the new one into the outputstream
	 * 
	 * @throws IOException 
	 */
	public EncryptedFile encrypt(File currentFile, EncryptedFolderDob parent) throws IOException
	{
		EncryptedFile encryptedFile = new EncryptedFile(currentFile, parent);
		
		FileInputStream inStream = new FileInputStream(currentFile);
		FileChannel inChannel = inStream.getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		CipherOutputStream outStream = new CipherOutputStream(new FileOutputStream(Messages.getScFolder()+encryptedFile.getScFileName()), ecipher);
		
		int numRead = 0;
		
		while ((numRead = inChannel.read(buffer)) >= 0)
		{
			outStream.write(buffer.array(), 0, numRead);
			buffer.clear();
		}
		
		inStream.close();
		outStream.close();
		
		return encryptedFile;
	}
	
	/**
	 * Decrypt the File from inputstream and write the new file into the outputstream
	 * 
	 * @throws IOException
	 */
	public void decrypt(EncryptedFileDob currentFile, String parentPath) throws IOException
	{
		File outputFile = new File(parentPath+"/"+currentFile.getName());
		
		if(outputFile.exists())
		{
			int i = 1;
			
			StringBuilder filename = new StringBuilder();
			filename.append(parentPath);
			filename.append("/"); //$NON-NLS-1$
			filename.append(currentFile.getName().substring(0,currentFile.getName().lastIndexOf(".")));
			filename.append(" "); //$NON-NLS-1$
			filename.append(i);
			filename.append("."); //$NON-NLS-1$
			filename.append(currentFile.getType());
			
			outputFile = new File(filename.toString());
			while(outputFile.exists())
			{
				i++;
				filename = new StringBuilder();
				filename.append(parentPath);
				filename.append("/"); //$NON-NLS-1$
				filename.append(currentFile.getName().substring(0,currentFile.getName().lastIndexOf(".")));
				filename.append(" "); //$NON-NLS-1$
				filename.append(i);
				filename.append("."); //$NON-NLS-1$
				filename.append(currentFile.getType());
				
				outputFile = new File(filename.toString());
			}
		}
		
		CipherInputStream inStream = new CipherInputStream(new FileInputStream(Messages.getScFolder()+currentFile.getScFileName()), dcipher);
		FileOutputStream outStream = new FileOutputStream(outputFile);
		FileChannel outChannel = outStream.getChannel();
		byte[] buf = new byte[1024];
		
		int numRead = 0;
		
		while ((numRead = inStream.read(buf)) >= 0)
		{
			outChannel.write(ByteBuffer.wrap(buf, 0, numRead));
		}
		
		inStream.close();
		outStream.close();
	}
}