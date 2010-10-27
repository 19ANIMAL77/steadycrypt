/**
 * Date: 26.10.2010
 * SteadyCrypt Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2;

import org.eclipse.osgi.util.NLS;

import de.steadycrypt.v2.views.model.CryptedFilesTableIdentifier;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "de.steadycrypt.v2.messages"; //$NON-NLS-1$

    // =========================================================================
    
    public static String DATE_FORMAT;

    public static String CryptedFilesTableIdentifier_NAME;
    public static String CryptedFilesTableIdentifier_TYPE;
    public static String CryptedFilesTableIdentifier_SIZE;
    public static String CryptedFilesTableIdentifier_DATE;
    public static String CryptedFilesTableIdentifier_PATH;
    public static String CryptedFilesTableIdentifier_FILE;

    // =========================================================================

    public static String getCryptedFilesTableColumnTitle(CryptedFilesTableIdentifier identifier)
    {
        String title = null;
        switch(identifier)
        {
            case DATE:
                title = CryptedFilesTableIdentifier_DATE;
                break;
            case FILE:
                title = CryptedFilesTableIdentifier_FILE;
                break;
            case NAME:
                title = CryptedFilesTableIdentifier_NAME;
                break;
            case PATH:
                title = CryptedFilesTableIdentifier_PATH;
                break;
            case SIZE:
                title = CryptedFilesTableIdentifier_SIZE;
                break;
            case TYPE:
                title = CryptedFilesTableIdentifier_TYPE;
                break;

            default:
                assert false : "Unbekannter Identifier: " //$NON-NLS-1$
                    + identifier;
        }

        return title;
    }
    
    static {
    	// initialize resource bundle
    	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }


}
