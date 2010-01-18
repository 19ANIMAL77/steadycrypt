/**
 * Date: 12.01.2010
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import org.jdesktop.swingx.util.WindowUtils;

public class JSteadyLogin extends JDialog {
	
	private static final long serialVersionUID = -4167400055532896519L;
	
	private char[] typedPassword;
	
	private JButton btnLogin;
	private JButton btnCancel;
	
	private JPanel panelTop;
	private JPanel panelCenter;
	private JPanel panelBottom;
	
	private JPasswordField txtInput;
	private JPasswordField txtInputConfirm;
	
	private JLabel lblInput;
	private JLabel lblInputConfirm;
	private JLabel bannerTop;
	
	private String regLog;

	public JSteadyLogin(String registerOrLogin){
		
		setModal(true);
		setTitle("Login");
		this.regLog = registerOrLogin;
		
		/**
		 * So that nobody could get access by terminating the login dialog.
		 */
		addWindowListener(new WindowAdapter(){
			public void windowClosing (WindowEvent we) {
				System.exit(0);
	        }
		});
		
		setPreferredSize(new Dimension(400, 230));
		
		bannerTop = new JLabel(new ImageIcon("img/"+registerOrLogin+".jpg"));
		
		btnLogin = new JButton("Login");
		btnLogin.setPreferredSize(new Dimension(130, 30));
		btnLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				setValidatedText();
			}
		});
		
		btnCancel = new JButton("Abbrechen");
		btnCancel.setPreferredSize(new Dimension(130, 30));
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		panelTop = new JPanel();
		panelCenter = new JPanel();
		panelBottom = new JPanel();
		
		txtInput = new JPasswordField();
		txtInput.setLocation(250, 20);
		txtInput.setPreferredSize(new Dimension(130, 25));
		txtInput.setSize(130, 25);
		txtInputConfirm = new JPasswordField();
		txtInputConfirm.setLocation(250, 55);
		txtInputConfirm.setPreferredSize(new Dimension(130, 25));
		txtInputConfirm.setSize(130, 25);
		
		if(registerOrLogin=="login") {
			lblInput = new JLabel("Passwort eingeben:");
			lblInput.setLocation(10, 20);
			lblInput.setSize(240, 25);
		}
		else if(registerOrLogin=="register"){
			lblInput = new JLabel("WICHTIG - Passwort jetzt festlegen:");
			lblInput.setLocation(10, 20);
			lblInput.setSize(240, 25);
			lblInputConfirm = new JLabel("Passwort wiederholen:");
			lblInputConfirm.setLocation(95, 55);
			lblInputConfirm.setSize(240, 25);
		}
		
		BorderLayout layout = new BorderLayout();
		FlowLayout flowTop = new FlowLayout();
		FlowLayout flowBottom = new FlowLayout();
		setLayout(layout);
		
		panelTop.setLayout(flowTop);
		panelTop.add(bannerTop);
		
		panelCenter.setLayout(null);
		panelCenter.add(lblInput);
		panelCenter.add(txtInput);
		
		if(registerOrLogin=="register"){
			panelCenter.add(lblInputConfirm);
			panelCenter.add(txtInputConfirm);
		}
		
		panelBottom.setLayout(flowBottom);
		panelBottom.add(btnLogin);
		panelBottom.add(btnCancel);
				
		add(panelTop, BorderLayout.NORTH);
		add(panelCenter, BorderLayout.CENTER);
		add(panelBottom, BorderLayout.SOUTH);
		
        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                txtInput.requestFocusInWindow();
            }
        });
		
		getRootPane().setDefaultButton(btnLogin);
		pack();
		setResizable(false);
		setLocation(WindowUtils.getPointForCentering(this));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public void setValidatedText() {
    	if(regLog == "register" && txtInput.getPassword().length < 6){
    		JOptionPane.showMessageDialog(this,
    			    "Passwort sollte mindestens 6 Zeichen lang sein.",
    			    "Passwort kleiner als 6 Zeichen",
    			    JOptionPane.WARNING_MESSAGE);
    	}
    	else {
    		if(txtInput.getPassword().length != 0){
        		typedPassword = txtInput.getPassword();
        		setVisible(false);
    		}
    		else {
        		JOptionPane.showMessageDialog(this,
        			    "Bitte Passwort eingeben.",
        			    "Kein Passwort eingegeben",
        			    JOptionPane.WARNING_MESSAGE);
    		}
    	}
    }
    
    public char[] getSteadyPassword() {
    	return typedPassword;
    }

}
