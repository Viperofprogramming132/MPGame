package com.Viper.UI.InGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.sound.midi.Receiver;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.Viper.Control.Player;
import com.Viper.Model.Vehicle;

public class InGameLabel extends JLabel{
	
	private Vehicle _VehicleLogic;
	
	private Image _VehicleSprite;
	
	private Player _Player;
	
	public InGameLabel(Player player)
	{
		_Player = player;		
		setOpaque(false);
		setDoubleBuffered(true);
	}
	
	public void Initialise()
	{
		_VehicleLogic = new Vehicle();
		_VehicleLogic.SelfUpdate();
		
		_VehicleLogic.set_VehicleLabel(this);
		_VehicleLogic.setPlayer(_Player);
		
		_Player.set_VehicleLogic(_VehicleLogic);
		_Player.setSprite(this);
	}
	
	public Vehicle get_Vehicle() {
		return _VehicleLogic;
	}

	public void set_Vehicle(Vehicle _Vehicle) {
		this._VehicleLogic = _Vehicle;
	}

	public void setStartImage(int index) {
		try {
			_VehicleSprite = _VehicleLogic.ReadVehicleImage(index);
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
		if(_VehicleSprite.getHeight(null) > _VehicleSprite.getWidth(null))
		{
			setSize(_VehicleSprite.getHeight(null), _VehicleSprite.getHeight(null));
			setBounds(0, 0, _VehicleSprite.getHeight(null), _VehicleSprite.getHeight(null));
		}
		
		
		setLocation(0,0);
		
		setVisible(true);
	}
	
	public void CalcNextFrame()
	{
		_VehicleLogic.Step();
	}
	
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
