/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2;

import org.eclipse.osgi.util.NLS;

import de.steadycrypt.v2.views.ui.SteadyTableIdentifier;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "de.steadycrypt.v2.messages"; //$NON-NLS-1$

    // =========================================================================

    public static String DATE_FORMAT;
    public static String FileTypeFilter_FOLDER;
    public static String FileTypeFilter_NONE;

    public static String CryptedFilesTableIdentifier_NAME;
    public static String CryptedFilesTableIdentifier_TYPE;
    public static String CryptedFilesTableIdentifier_SIZE;
    public static String CryptedFilesTableIdentifier_DATE;
    public static String CryptedFilesTableIdentifier_PATH;
    
    public static String TableView_ExportFile;
    public static String TableView_ExportFile_Tooltip;
    public static String TableView_ExportFileDialog_Title;
    public static String TableView_ExpandAll;
    public static String TableView_ExpandAll_Tooltip;
    public static String TableView_CollapseAll;
    public static String TableView_CollapseAll_Tooltip;
    public static String TableView_SelectAll;
    public static String TableView_SelectAll_Tooltip;

    public static String SideBarView_Filters;
    public static String SideBarView_NameFilter;
    public static String SideBarView_TypeFilter;
    public static String SideBarView_DateFilter;
    public static String SideBarView_DateFilterFrom;
    public static String SideBarView_DateFilterTo;
    public static String SideBarView_SaveFavorite;
    public static String SideBarView_SaveFavoriteButton;
    public static String SideBarView_Favorites;
    public static String SideBarView_DeleteFavorite;

    // =========================================================================

    public static String getSteadyTableColumnTitle(SteadyTableIdentifier identifier)
    {
        String title = null;
        switch(identifier)
        {
            case DATE:
                title = CryptedFilesTableIdentifier_DATE;
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
                assert false : identifier + " is not a legal identifier!"; //$NON-NLS-1$
        }

        return title;
    }
    
    static {
    	// initialize resource bundle
    	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }


}
