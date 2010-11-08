/**
 * Date: 04.11.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.views.model;

import java.util.EventListener;

/**
 * This interface should be implemented if you're interested
 * in being informed of SideBar Events.
 */
public interface SideBarListener extends EventListener {
	
	/**
	 * This Event is triggered by my Button experiment
	 */
	public void doSearch();

}
