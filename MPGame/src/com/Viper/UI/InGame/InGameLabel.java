package com.Viper.UI.InGame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.Viper.Control.Player;
import com.Viper.Model.Vehicle;

/**
 * Creates a label that is the vehicle sprite
 * 
 * Controls rotation of the vehicle but no logic
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class InGameLabel extends JLabel{
	
	/**
	 * Link to the logic of the vehicle
	 */
	private Vehicle _VehicleLogic;
	
	/**
	 * The sprite that should be displayed
	 */
	private Image _VehicleSprite;
	
	/**
	 * The player that this sprite is controlled by
	 */
	private Player _Player;
	
	/**
	 * Creates a InGameLabel which is the sprite of the players vehicles
	 * @param player The player that this sprite belongs to
	 */
	public InGameLabel(Player player)
	{
		_Player = player;		
		setOpaque(false);
	}
	
	/**
	 * Creates the vehicle logic and sets all the data that it needs to
	 * @param game The link back to its parent
	 */
	public void Initialise(InGame game)
	{
		_VehicleLogic = new Vehicle();
		_VehicleLogic.SelfUpdate();
		
		_VehicleLogic.set_VehicleLabel(this);
		_VehicleLogic.setPlayer(_Player);
		
		_Player.set_VehicleLogic(_VehicleLogic);
		_Player.setSprite(this);
		_VehicleLogic.setCurrentGame(game);
	}
	
	/**
	 * @return The logic for the vehicle
	 */
	public Vehicle get_Vehicle() {
		return _VehicleLogic;
	}

	/**
	 * Gets the sprite that should be displayed and sets the label to the correct size to be able to contain it with any rotaion
	 */
	public void setStartImage() {
		try {
			_VehicleSprite = _VehicleLogic.ReadVehicleImage(_Player.getSpriteIndex());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		setIcon(new ImageIcon(_VehicleSprite));
		
		
		if(_VehicleSprite.getWidth(null) > _VehicleSprite.getHeight(null))
		{
			setSize(_VehicleSprite.getWidth(null), _VehicleSprite.getWidth(null));
			setBounds(0, 0, _VehicleSprite.getWidth(null), _VehicleSprite.getWidth(null));
		}
		else if(_VehicleSprite.getHeight(null) > _VehicleSprite.getWidth(null))
		{
			setSize(_VehicleSprite.getHeight(null), _VehicleSprite.getHeight(null));
			setBounds(0, 0, _VehicleSprite.getHeight(null), _VehicleSprite.getHeight(null));
		}
		else
		{
			setSize(_VehicleSprite.getWidth(null), _VehicleSprite.getHeight(null));
			setBounds(0, 0, _VehicleSprite.getWidth(null), _VehicleSprite.getHeight(null));
		}
	
		
		setVisible(true);
	}
	
	/**
	 * Tells the vehicle logic to attempt to move
	 */
	public void CalcNextFrame()
	{
		_VehicleLogic.Step();
	}
	
	/**
	 * Paints the sprite at the rotation using speed over quaility
	 */
	@Override
	public void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D) g;
		
		int x = getWidth() / 2;
		int y = getHeight() / 2;
		
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		
		g2d.rotate(_VehicleLogic.getAngle(), x, y);
		
		super.paintComponent(g);
	}
}
