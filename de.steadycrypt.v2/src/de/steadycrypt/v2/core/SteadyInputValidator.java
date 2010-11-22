/**
 * Date: 17.11.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2.core;

import org.eclipse.jface.dialogs.IInputValidator;

import de.steadycrypt.v2.Messages;

public class SteadyInputValidator implements IInputValidator {

	@Override
	public String isValid(String newText) {
		String[] specialCharacters = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};

		if(newText.length() <= 0)
			return Messages.SteadyInputValidator_Error_NoName;
		else if(newText.length() > 250)
			return Messages.SteadyInputValidator_Error_ToLong;
		else if(newText.startsWith("."))
			return Messages.SteadyInputValidator_Error_DontBeginnWithDot;
		else {
			for(String specialCharacter : specialCharacters) {
				if(newText.contains(specialCharacter))
					return Messages.SteadyInputValidator_Error_SpecialCharacter;
			}
		}
		
		return null;
	}

}
