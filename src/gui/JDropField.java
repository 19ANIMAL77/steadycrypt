/**
 * Date: 04.12.2009
 * SteadyCrypt Project by Joerg Harr & Marvin Hoffmann
 *
 */

package gui;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import core.FileDropHandler;

public class JDropField extends JLabel {

	private static final long serialVersionUID = 377338260180551198L;
	public JDropField(ImageIcon imageIcon) {
		
		super(imageIcon);
		
		Dimension compSize = new Dimension(175, 175);
		FileDropHandler fdh = new FileDropHandler();
		
		setTransferHandler(fdh);
		setPreferredSize(compSize);
		setMinimumSize(compSize);
		setMaximumSize(compSize);		
		
	}
}
