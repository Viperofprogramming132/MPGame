package com.Viper.UI.InGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JLabel;

import com.Viper.Control.Controller;

/**
 * The checkpoints that check the player has gone around the map
 * 
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class Checkpoint extends JLabel{

	/**
	 * The width (Height) of the checkpoint
	 */
	private final int WIDTH = 20;
	
	/**
	 * If it is a start/finish line aka the first checkpoint
	 */
	private boolean _StartFinish = false;
	
	/**
	 * The length of the checkpoint
	 */
	private int _Length;
	
	/**
	 * Creates a checkpoint at the specified location
	 * @param Start If the checkpoint is the start line
	 * @param location The location of the checkpoint
	 */
	public Checkpoint(boolean Start, Point location)
	{
		_StartFinish = Start;
		this.setLocation(location);
		
		_Length = Controller.GetController().get_GameController().get_TrackWidth();
		
		this.setSize(_Length, WIDTH);
		this.setOpaque(true);
	}
	
	/**
	 * Draws the colours of the checkpoints different to the start one
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		if(_StartFinish)
			setBackground(Color.WHITE);
		else
			setBackground(Color.DARK_GRAY);
		
		super.paintComponent(g);
	}

	/**
	 * Sets the label as a start finish checkpoint
	 * @param b If the checkpoint is a start/finish
	 */
	public void setStartFinish(boolean b) {
		_StartFinish = true;
	}
}
