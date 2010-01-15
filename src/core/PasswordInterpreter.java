/**
 * Date: 14.01.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package core;

import org.apache.log4j.Logger;

public class PasswordInterpreter {

	private static org.apache.log4j.Logger log = Logger.getLogger(PasswordInterpreter.class);
	
	public static String createPassword (String input) {
		
		String output = "";
		
		while (output.length() < 24) {
		
			for ( int i=0; i < input.length(); i++) {
				output = output+output.length()+(input.substring(i,i+1)).toUpperCase()+(input.substring(i, input.length())).toLowerCase();
			}
		
		}
		
		log.debug(output);
		return output;
	}
	
}
