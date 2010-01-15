/**
 * Date: 08.01.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package core;

import java.util.EventListener;

/**
 * This interface should be implemented if you're interrested
 * in being informed of dropped files.
 */
public interface FileDroppedListener extends EventListener {
	
	/**
	 * This function is triggered if a file has been
	 * dropped.
	 * 
	 */
	public void fileDropped();

}
