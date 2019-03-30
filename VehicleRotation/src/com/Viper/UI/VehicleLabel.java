package com.Viper.UI;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * The label that displays the image of the vehicle
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class VehicleLabel extends JLabel implements ActionListener{

	/**
	 * If the vehicle should turn left
	 */
	private boolean _Left = false;
	
	/**
	 * If the vehicle should turn right
	 */
	private boolean _Right = false;
	
	/**
	 * The vehicle that is currently being displayed 1-16
	 * 
	 * 1 90 degrees 2 112.5 and so on
	 */
	private int _displayedImage = 1;
	
	
	/**
	 * The timer that causes smooth turning
	 */
	Timer _rotateTimer;
	
	/**
	 * Creates a vehicle label with the images from the given vehicle location
	 * @param vehicleLocation The location of the images to use to create the vehicle
	 */
	public VehicleLabel()
	{
		this.setSize(50,50);
		this.setVisible(true);
		
		_rotateTimer = new Timer(33, this);
		_rotateTimer.start();
	}
	
	/**
	 * Attempts to read an image from the specified file
	 * @param location The file that the system will attempt to read
	 * @return The image that is read
	 * @throws IOException If the file fails to read the image due to it not existing or read/write error
	 */
	private Image ReadImage(String location) throws IOException
	{
		BufferedImage img = ImageIO.read(this.getClass().getResourceAsStream(location));
		Image i = new ImageIcon(img).getImage();
		return i;
	}
	
	/**
	 * @return the _Left
	 */
	public boolean is_Left() {
		return _Left;
	}
	/**
	 * @param _Left the _Left to set
	 */
	public void set_Left(boolean _Left) {
		this._Left = _Left;
	}
	/**
	 * @return the _Right
	 */
	public boolean is_Right() {
		return _Right;
	}
	/**
	 * @param _Right the _Right to set
	 */
	public void set_Right(boolean _Right) {
		this._Right = _Right;
	}

	/**
	 * Timer tick
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(_Left && !_Right)
			_displayedImage--;
		if(_Right && !_Left)
			_displayedImage++;
		
		drawVehicle();
	}

	/**
	 * Draws the vehicle to the label
	 */
	private void drawVehicle() {
		
		//Ensures no overflow or out of range
		if(_displayedImage == 16)
			_displayedImage = 1;
		if(_displayedImage == 0)
			_displayedImage = 16;
		
		
		//Sets the icon based off the selected vehicle
		try {
			setIcon(new ImageIcon(ReadImage("/Car" + _displayedImage + ".png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
