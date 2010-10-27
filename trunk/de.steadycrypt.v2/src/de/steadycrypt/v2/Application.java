/**
 * Date: 26.10.2010
 * SteadyCrypt v2 Project by Joerg Harr and Marvin Hoffmann
 *
 */

package de.steadycrypt.v2;

import java.io.File;
import java.sql.SQLException;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import de.steadycrypt.v2.core.DbManager;
import de.steadycrypt.v2.core.PasswordInterpreter;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
//		DbManager manager = DbManager.getInstance();
//		manager.startDb();
//
//		File f = new File(System.getProperty("user.dir")+"/sc-files/");
//		
//		if (f.exists()) {
//			manager.connectToDb("steady", PasswordInterpreter.createPassword("crypt"));
//		}
//		else {
//			try {
//				manager.initiateDb("steady", PasswordInterpreter.createPassword("crypt"));
//			}
//			catch (SQLException sqle) {
//				DbManager.printSQLException(sqle);
//			}
//			f.mkdir();
//		}
		
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
