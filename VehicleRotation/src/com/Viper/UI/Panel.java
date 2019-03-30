package com.Viper.UI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

/**
 * The Panel that displays the vehicle
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class Panel extends JPanel implements KeyListener{

	/**
	 * The vehicle that is to be displayed
	 */
	private VehicleLabel Label;
	
	/**
	 * Creates a panel which is used to display the vehicle rotation
	 */
	public Panel()
	{
		setFocusable(true);
		setOpaque(false);
		addKeyListener(this);
		Label = new VehicleLabel();
		
		add(Label);
	}
	
	/**
	 * Key Listener for any key presses while the form is focused
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		
		if (e.getKeyCode() == KeyEvent.VK_D) 
		{
			Label.set_Right(true);
		}
		if (e.getKeyCode() == KeyEvent.VK_A)
		{
			Label.set_Left(true);
		}
		
	}

	/**
	 * Key Listener for any key releases while the form is focused
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_D) 
		{
			Label.set_Right(false);
		}
		if (e.getKeyCode() == KeyEvent.VK_A)
		{
			Label.set_Left(false);
		}
		
	}

	/**
	 * Stub override method required with KeyListener
	 */
	@Override
	public void keyTyped(KeyEvent e) {		
	}

}
