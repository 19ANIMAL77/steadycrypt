/**
 * Date: 14.01.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

public class PasswordInterpreter {
	
	public static String createPassword (String input) {
		
		String output = "";
		
		while (output.length() < 24) {
		
			for ( int i=0; i < input.length(); i++) {
				if (i%2 == 0) {
					output = output+output.length()+(input.substring(i,i+1)).toUpperCase()+input.substring(i, input.length());					
				}
				else {
					output = output+output.length()+(input.substring(i,i+1)).toLowerCase()+input.substring(i, input.length());
				}
			}
		
		}
		
//		log.debug(output);
		return output;
	}
	
}
