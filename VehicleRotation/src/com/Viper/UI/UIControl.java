package com.Viper.UI;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Controls the panels for the UI displaying and hiding as needed
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class UIControl extends JFrame{
	
	/**
	 * Contains the panels for display
	 */
	private Container _ContentContainer;
	
	/**
	 * The main content panel
	 */
	private JPanel _Panel;

	/**
	 * Creates a UIControl
	 * 
	 * Sets up the frame
	 */
	public UIControl()
	{
		setTitle("Multiplayer Game");
		setResizable(false);
		setBounds(0,0,400,320);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		_ContentContainer = getContentPane();
	}
	
	/**
	 * displays the Main screen
	 */
	public void OpenScreen() {
		if (_Panel == null)
		{
			_Panel = new Panel();
			_ContentContainer.add(_Panel);
		}
		
		this.setSize(400, 320);
		_Panel.setVisible(true);
		this.setVisible(true);
		_Panel.grabFocus();
		
	}
}
