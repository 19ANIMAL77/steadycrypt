
package de.steadycrypt.v2.views;

import java.io.File;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.splash.AbstractSplashHandler;

import de.steadycrypt.v2.Messages;
import de.steadycrypt.v2.core.DbManager;
import de.steadycrypt.v2.core.PasswordInterpreter;

/**
 * @since 3.3
 * 
 */
public class InteractiveSplashHandler extends AbstractSplashHandler {
	
	private final static int F_LABEL_HORIZONTAL_INDENT = 65;
	private final static int F_BUTTON_WIDTH_HINT = 80;
	private final static int F_BUTTON_WIDTH_HINT_CREATE = 120;
	private final static int F_TEXT_WIDTH_HINT = 195;
	private final static int F_COLUMN_COUNT = 3;
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
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.splash.AbstractSplashHandler#init(org.eclipse.swt.widgets.Shell)
	 */
	public void init(final Shell splash) {
		
		scDirectory = new File(System.getProperty("user.dir")+"/sc-files/");
		
		if (scDirectory.exists())
			isFirstStartUp = false;
		else
		{
			isFirstStartUp = true;
		}
		
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
		if(!isFirstStartUp)
			splash.setDefaultButton(this.fButtonOK);
		else
			splash.setDefaultButton(this.fButtonRegister);
		
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
	private void createUIListeners() {
		
		if(!isFirstStartUp)
		{
			// Create the OK button listeners
			createUIListenersButtonOK();
		}
		else
		{
			// Create the Register button listeners
			createUIListenersButtonRegister();
		}

		// Create the cancel button listeners
		createUIListenersButtonCancel();
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
				handleButtonRegisterWidgetSelected();
			}
		});
	}
	
	/**
	 * 
	 */
	private void handleButtonRegisterWidgetSelected() {
		
		// Prevent of double-Clicks
		fButtonRegister.setEnabled(false);
		
		String regPassword = fTextPassword.getText();
		String regPasswordValidate = fTextPasswordValidate.getText();
		
		// Check for > 6 chars
		// Check for equals entries
		if((regPassword.length() > 6 && regPasswordValidate.length() > 6)
				&& (String.valueOf(regPassword).equals(String.valueOf(regPasswordValidate))))
		{
			
			DbManager manager = DbManager.getInstance();
			manager.startDb();
			
			try 
			{				
				// Initiate the awesome derby
				manager.initiateDb("steady", PasswordInterpreter.createPassword(regPassword));
			}
			catch (SQLException sqle) {
				// Prevent of double-Clicks
				fButtonRegister.setEnabled(true);
				
				DbManager.printSQLException(sqle);
			}
			
			// Create sc-files directory
			scDirectory.mkdir();
			
			// do login
			fAuthenticated = true;
		}
		else
		{
			MessageDialog.openError(
					getSplash(),
					Messages.InteractiveSplashHandler_Error_RegFailed_Title,
					Messages.InteractiveSplashHandler_Error_RegFailed_Message);
		}
	}
	
	/**
	 * 
	 */
	private void createUIListenersButtonOK() {
		fButtonOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleButtonOKWidgetSelected();
			}
		});				
	}

	/**
	 * 
	 */
	private void handleButtonOKWidgetSelected() {
		
		// Prevent of double-Clicks
		fButtonOK.setEnabled(false);
		
		String password = fTextPassword.getText();

		if (password.length() > 6)
		{
			
			DbManager manager = DbManager.getInstance();
			manager.startDb();
			
			/**
			 * User got 3 chances to enter correct password. After that the application exits.
			 * Small protection against brute-force.
			 */
			try 
			{				
				manager.connectToDb("steady", PasswordInterpreter.createPassword(password));
				
				// Login
				fAuthenticated = true;

			}
			catch (SQLException sqle)
			{
	    		
				MessageDialog.openError(getSplash(), Messages.InteractiveSplashHandler_Error_WrongPW_Title, NLS.bind(Messages.InteractiveSplashHandler_Error_WrongPW_Message, loginCount));
	    		
	    		if(loginCount==3)
	    			System.exit(0);
	    		
	    		loginCount++;
	    		
				// Prevent of double-Clicks
				fButtonOK.setEnabled(true);
	    		
	    		DbManager.printSQLException(sqle);
			}
			
		} 
		else 
		{
			MessageDialog.openError(
					getSplash(),
					Messages.InteractiveSplashHandler_Error_AuthFailed_Title,
					Messages.InteractiveSplashHandler_Error_AuthFailed_Message); 
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
		
		// Create the password validate label & text
		if(isFirstStartUp){
			createUILabelPasswordValidate();
			createUITextPasswordValidate();
		}
		
		// Create the blank label
		createUILabelBlank();
		
		if(!isFirstStartUp)
		{
			// Create the OK button
			createUIButtonOK();	
		}
		else
		{
			// Create the Register Button
			createUIButtonRegister();
		}

		
		// Create the cancel button
		createUIButtonCancel();
	}		
	
	/**
	 * 
	 */
	private void createUIButtonCancel() {
		// Create the button
		fButtonCancel = new Button(fCompositeLogin, SWT.PUSH);
		fButtonCancel.setText(Messages.InteractiveSplashHandler_Cancel); //$NON-NLS-1$
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_BUTTON_WIDTH_HINT;	
		data.verticalIndent = 10;
		fButtonCancel.setLayoutData(data);
	}

	/**
	 * 
	 */
	private void createUIButtonOK() {
		// Create the button
		fButtonOK = new Button(fCompositeLogin, SWT.PUSH);
		fButtonOK.setText("OK"); //$NON-NLS-1$
		
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_BUTTON_WIDTH_HINT;
		data.verticalIndent = 10;
		fButtonOK.setLayoutData(data);
	}
	
	/**
	 * 
	 */
	private void createUIButtonRegister() {
		// Create the button
		fButtonRegister = new Button(fCompositeLogin, SWT.PUSH);
		fButtonRegister.setText(Messages.InteractiveSplashHandler_Register); //$NON-NLS-1$
		
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_BUTTON_WIDTH_HINT_CREATE;
		data.verticalIndent = 10;
		fButtonRegister.setLayoutData(data);
	}	

	/**
	 * 
	 */
	private void createUILabelBlank() {
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setVisible(false);
	}

	/**
	 * 
	 */
	private void createUITextPassword() {
		// Create the text widget
		int style = SWT.PASSWORD | SWT.BORDER;
		fTextPassword = new Text(fCompositeLogin, style);
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_TEXT_WIDTH_HINT;
		data.horizontalSpan = 2;
		fTextPassword.setLayoutData(data);		
		fTextPassword.setFocus();
	}
	
	/**
	 * 
	 */
	private void createUITextPasswordValidate() {
		// Create the text widget
		int style = SWT.PASSWORD | SWT.BORDER;
		fTextPasswordValidate = new Text(fCompositeLogin, style);
		// Configure layout data
		GridData data = new GridData(SWT.NONE, SWT.NONE, false, false);
		data.widthHint = F_TEXT_WIDTH_HINT;
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
		// Configure layout data
		GridData data = new GridData();
		data.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;
		label.setLayoutData(data);					
	}

	/**
	 * 
	 */
	private void createUILabelPasswordValidate() {
		// Create the label
		Label label = new Label(fCompositeLogin, SWT.NONE);
		label.setText("&"+Messages.InteractiveSplashHandler_PasswordValidate); //$NON-NLS-1$
		// Configure layout data
		GridData data = new GridData();
		data.horizontalIndent = F_LABEL_HORIZONTAL_INDENT;
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
		GridLayout layout = new GridLayout(F_COLUMN_COUNT, false);
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
