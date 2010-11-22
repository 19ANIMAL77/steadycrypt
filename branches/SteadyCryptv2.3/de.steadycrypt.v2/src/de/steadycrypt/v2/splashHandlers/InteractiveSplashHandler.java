
package de.steadycrypt.v2.splashHandlers;

import java.io.File;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.splash.AbstractSplashHandler;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.core.Crypter;
import de.steadycrypt.v2.core.DbManager;
import de.steadycrypt.v2.core.PasswordInterpreter;

/**
 * @since 3.3
 * 
 */
public class InteractiveSplashHandler extends AbstractSplashHandler {
	
	private final static int F_COLUMN_COUNT = 4;
	private final static int MIN_PASSWORD_LENGTH = 7;
	private static int loginCount = 1;
	
	private static boolean isFirstStartUp = true;
	
	private Composite fCompositeLogin;
	private Text fTextPassword;
	private Text fTextPasswordValidate;
	
	private Button fButtonRegister;
	private Button fButtonOK;
	private Button fButtonCancel;
	private boolean fAuthenticated;
	
	private File scDirectory;
	
	/**
	 * 
	 */
	public InteractiveSplashHandler() {
		fCompositeLogin = null;
		fTextPassword = null;
		fTextPasswordValidate = null;
		fButtonRegister = null;
		fButtonOK = null;
		fButtonCancel = null;
		fAuthenticated = false;
		scDirectory = null;
	}
	
	public void init(final Shell splash)
	{
		scDirectory = new File(Crypter.encryptionPath);
		isFirstStartUp = !scDirectory.exists();
		
		// Store the shell
		super.init(splash);
		// Configure the shell layout
		configureUISplash();
		// Create UI
		createUI();		
		// Create UI listeners
		createUIListeners();
		// Force the splash screen to layout
		splash.layout(true);
		
		// Set default button
		splash.setDefaultButton(isFirstStartUp ? this.fButtonRegister : this.fButtonOK);
		
		// Keep the splash screen visible and prevent the RCP application from 
		// loading until the close button is clicked.
		doEventLoop();
	}
	
	/**
	 * 
	 */
	private void doEventLoop() {
		Shell splash = getSplash();
		while (fAuthenticated == false) {
			if (splash.getDisplay().readAndDispatch() == false) {
				splash.getDisplay().sleep();
			}
		}
	}

	/**
	 * 
	 */
	private void createUIListeners()
	{	
		if(!isFirstStartUp) {
			// Create the OK button listeners
			createUIListenersButtonOK();
		} else {
			// Create the Register button listeners
			createUIListenersButtonRegister();
		}

		// Create the cancel button listeners
		createUIListenersButtonCancel();
		// Create ESC-key-listener for text fields
		createUIListenersTextPassword();
	}

	/**
	 * 
	 */
	private void createUIListenersButtonCancel() {
		fButtonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleButtonCancelWidgetSelected();
			}
		});
	}

	/**
	 * 
	 */
	private void createUIListenersTextPassword() {
		fTextPassword.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.ESC)
					handleButtonCancelWidgetSelected();
			}
			public void keyPressed(KeyEvent e) {
			}
		});
		if(isFirstStartUp) {
			fTextPasswordValidate.addKeyListener(new KeyListener() {
				public void keyReleased(KeyEvent e) {
					if(e.keyCode == SWT.ESC)
						handleButtonCancelWidgetSelected();
				}
				public void keyPressed(KeyEvent e) {
				}
			});
		}
	}

	/**
	 * 
	 */
	private void handleButtonCancelWidgetSelected() {
		// Abort the loading of the RCP application
		getSplash().getDisplay().close();
		System.exit(0);		
	}
	
	/**
	 * 
	 */
	private void createUIListenersButtonRegister() {
		fButtonRegister.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Prevent from double-Clicks
				fButtonRegister.setEnabled(false);
				handleButtonRegisterWidgetSelected();
			}
		});
	}
	
	/**
	 * 
	 */
	private void handleButtonRegisterWidgetSelected()
	{
		String regPassword = fTextPassword.getText();
		String regPasswordValidate = fTextPasswordValidate.getText();
		
		// Check for MIN_PASSWORD_LENGTH
		// Check for equals entries
		if((regPassword.length() >= MIN_PASSWORD_LENGTH && regPasswordValidate.length() >= MIN_PASSWORD_LENGTH)
				&& (String.valueOf(regPassword).equals(String.valueOf(regPasswordValidate))))
		{
			DbManager manager = DbManager.getInstance();
			manager.startDb();
			
			try {				
				// Initiate the awesome derby
				manager.initiateDb("steady", PasswordInterpreter.createPassword(regPassword));
			} catch (SQLException sqle) {
				// Prevent of double-Clicks
				fButtonRegister.setEnabled(true);
				DbManager.printSQLException(sqle);
			}
			
			// Create sc-files directory
			scDirectory.mkdir();
			// do login
			fAuthenticated = true;
		} else {
			MessageDialog.openError(getSplash(), Messages.InteractiveSplashHandler_Error_RegFailed_Title, Messages.InteractiveSplashHandler_Error_RegFailed_Message);
		}
	}
	
	/**
	 * 
	 */
	private void createUIListenersButtonOK() {
		fButtonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Prevent from double-Clicks
				fButtonOK.setEnabled(false);
				handleButtonOKWidgetSelected();
			}
		});				
	}

	/**
	 * 
	 */
	private void handleButtonOKWidgetSelected()
	{
		String password = fTextPassword.getText();

		DbManager manager = DbManager.getInstance();
		manager.startDb();
		
		/**
		 * User has got three tries to enter correct password. After that the application exits.
		 * Small protection against brute-force.
		 */
		try  {				
			manager.connectToDb("steady", PasswordInterpreter.createPassword(password));
			
			// Login
			fAuthenticated = true;
		} catch (SQLException sqle) {
			// Prevent from double-Clicks
			fButtonOK.setEnabled(true);
			
			MessageDialog.openError(getSplash(), Messages.InteractiveSplashHandler_Error_WrongPW_Title, NLS.bind(Messages.InteractiveSplashHandler_Error_WrongPW_Message, loginCount));
    		
    		if(loginCount==3)
    			System.exit(0);
    		
    		loginCount++;
    		
    		DbManager.printSQLException(sqle);
		}
	}
	
	/**
	 * 
	 */
	private void createUI() {
		// Create the login panel
		createUICompositeLogin();
		// Create the blank spanner
		createUICompositeBlank();
		// Create the password label
		createUILabelPassword();
		// Create the password text widget
		createUITextPassword();
		
		if(!isFirstStartUp) {
			// Create the OK button
			createUIButtonOK();	
			createUILabelBlank();
		} else {
			// Create the Register Button
			createUIButtonRegister();
			createUILabelPasswordValidate();
			createUITextPasswordValidate();
		}
		
		// Create the cancel button
		createUIButtonCancel();
	}		
	
	/**
	 * 
	 */
	private void createUIButtonCancel() {
		// Create the button
		fButtonCancel = new Button(fCompositeLogin, SWT.FLAT);
		fButtonCancel.setText(Messages.InteractiveSplashHandler_Cancel);
		// Configure layout data
		GridData data = new GridData(SWT.FILL, SWT.NONE, false, false);
		fButtonCancel.setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createUIButtonOK() {
		// Create the button
		fButtonOK = new Button(fCompositeLogin, SWT.FLAT);
		fButtonOK.setText("OK"); //$NON-NLS-1$
		
		// Configure layout data
		GridData data = new GridData(SWT.FILL, SWT.NONE, false, false);
		fButtonOK.setLayoutData(data);
	}
	
	/**
	 * 
	 */
	private void createUIButtonRegister() {
		// Create the button
		fButtonRegister = new Button(fCompositeLogin, SWT.FLAT);
		fButtonRegister.setText(Messages.InteractiveSplashHandler_Register);
		
		// Configure layout data
		GridData data = new GridData(SWT.FILL, SWT.NONE, false, false);
		fButtonRegister.setLayoutData(data);
	}	

	/**
	 * 
	 */
	private void createUILabelBlank() {
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false, 3, 1));
		label.setVisible(false);
	}

	/**
	 * 
	 */
	private void createUITextPassword() {
		// Create the text widget
		fTextPassword = new Text(fCompositeLogin, SWT.PASSWORD | SWT.BORDER);
		// Configure layout data
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = 2;
		fTextPassword.setLayoutData(data);		
		fTextPassword.setFocus();
	}
	
	/**
	 * 
	 */
	private void createUITextPasswordValidate() {
		// Create the text widget
		fTextPasswordValidate = new Text(fCompositeLogin, SWT.PASSWORD | SWT.BORDER);
		// Configure layout data
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.horizontalSpan = 2;
		fTextPasswordValidate.setLayoutData(data);		
	}

	/**
	 * 
	 */
	private void createUILabelPassword() {
		// Create the label
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setText("&"+Messages.InteractiveSplashHandler_Password); //$NON-NLS-1$
		label.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		// Configure layout data
		GridData data = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		label.setLayoutData(data);					
	}

	/**
	 * 
	 */
	private void createUILabelPasswordValidate() {
		// Create the label
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setText("&"+Messages.InteractiveSplashHandler_PasswordValidate); //$NON-NLS-1$
		label.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		// Configure layout data
		GridData data = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		label.setLayoutData(data);					
	}
	
	/**
	 * 
	 */
	private void createUICompositeBlank() {
		Composite spanner = new Composite(fCompositeLogin, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = F_COLUMN_COUNT;
		spanner.setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createUICompositeLogin() {
		// Create the composite
		fCompositeLogin = new Composite(getSplash(), SWT.BORDER);
		GridLayout layout = new GridLayout(F_COLUMN_COUNT, true);
		fCompositeLogin.setLayout(layout);		
	}

	/**
	 * 
	 */
	private void configureUISplash() {
		// Configure layout
		FillLayout layout = new FillLayout(); 
		getSplash().setLayout(layout);
		// Force shell to inherit the splash background
		getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);
	}
	
}
