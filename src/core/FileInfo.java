/**
 * Date: 06.11.2009
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;

/**
 * Its necessary to extend the original File object, because we additionally want to save
 * the path and name of the encrypted file, the encryption time and provide an OS independent method to return a file type.
 */
@SuppressWarnings("serial")
public class FileInfo extends File {
	
	private Timestamp encryptionTime;
	private String encryptedFilePath;
	private String encryptedFileName;
	
	/**
	 * A FileInfo object is only created when a file needs to be encrypted.
	 * So, when creating a FileObject the current time is saved into encryptionTime.
	 * @param pathname
	 */
	public FileInfo(String pathname) {
		super(pathname);
		this.encryptionTime = new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * Provides Information such as "txt-File" or "JPG-File".
	 * @return the fileExtension
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public String getFileType() throws MalformedURLException, IOException {
		String filename = this.getName();
		String ext = (filename.lastIndexOf(".")==-1)?"":filename.substring(filename.lastIndexOf(".")+1,filename.length());

		String fileType = ext+"-File";
		
		return fileType;
	}

	/**
	 * Time Format is for example:
	 * 20.11.2009 at 12:08:56
	 * @return the encryptionTime
	 */
	public Timestamp getEncryptionTime() {
		return encryptionTime;
	}

	/**
	 * Generating path for encrypted files by using program-path and adding sub directory "sc-files"
	 * @return encryptedFileName
	 */
	public String getEncryptedFilePath() {
		if (encryptedFilePath == null) {
			encryptedFilePath = System.getProperty("user.dir")+"/sc-files/";
		}
		
		return encryptedFilePath;
	}

	/**
	 * Automatically generates a unique filename by using system time in nanoseconds and add .sc extension.
	 * @return encryptedFileName
	 */
	public String getEncryptedFileName() {
		if (encryptedFileName == null) {
			encryptedFileName = System.nanoTime()+".sc";
		}
		
		return encryptedFileName;
	}

}
