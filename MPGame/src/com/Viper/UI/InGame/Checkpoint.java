package com.Viper.UI.InGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JLabel;

import com.Viper.Control.Controller;

@SuppressWarnings("serial")
public class Checkpoint extends JLabel{

	private final int WIDTH = 20;
	
	private boolean _StartFinish = false;
	private int _Length;
	
	
	public Checkpoint(boolean Start, Point location)
	{
		_StartFinish = Start;
		this.setLocation(location);
		
		_Length = Controller.GetController().get_GameController().get_TrackWidth();
		
		this.setSize(_Length, WIDTH);
		this.setOpaque(true);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		if(_StartFinish)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.ORANGE);
		
		super.paintComponent(g);
	}

	public void setStartFinish(boolean b) {
		
	}
}
