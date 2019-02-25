package com.Viper.UI.InGame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;

/**
 * The in game panel that controls the map drawing
 * 
 * Has references to the labels that are the vehicles
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class InGame extends JPanel implements KeyListener{
	
	/**
	 * The size of the image in pixel on the X
	 */
	private int WORLD_SIZE_X;
	
	/**
	 * The size of the image in pixel on the Y
	 */
	private int WORLD_SIZE_Y;
	
	/**
	 * The list of all the vehicles
	 */
	private ArrayList<InGameLabel> _VehicleLabels = new ArrayList<>();
	
	/**
	 * The list of all the checkpoints
	 */
	private ArrayList<Checkpoint> _CheckPointLabels = new ArrayList<>();
	
	/**
	 * The image of the map
	 */
	private Image _Map;
	
	/**
	 * Creates a InGame JPanel that will draw the map ready for the game
	 * 
	 * Contains all the vehicles
	 */
	public InGame()
	{		
		Controller.GetController().get_GameController().StartGame();

		addKeyListener(this);
		
		CreateVehicles();
		DrawCheckpoints();
		
		setLayout(null);
		setOpaque(false);
		setFocusable(true);
		
		setVisible(true);
		
		
	}
	
	/**
	 * Draws the checkpoints at the specified locations according to the location got from the properties file
	 */
	private void DrawCheckpoints()
	{
		ArrayList<Point> checkpoints = Controller.GetController().get_GameController().get_CheckPointLocations();
		
		for (Point location : checkpoints)
		{
			Checkpoint cp = new Checkpoint(false, location);
			_CheckPointLabels.add(cp);
			add(cp);
		}
		
		_CheckPointLabels.get(0).setStartFinish(true);
	}
	
	/**
	 * Paints all the components
	 */
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		DrawMap(g);
	}
	
	/**
	 * Draws the map
	 * @param g The graphics of the panel
	 */
	private void DrawMap(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		if(_Map == null)
		{
			_Map = Controller.GetController().get_GameController().get_Map();
			
			WORLD_SIZE_X = this.getWidth();
			WORLD_SIZE_Y = this.getHeight();
		}
		

		g2d.drawImage(_Map, 0, 0, WORLD_SIZE_X, WORLD_SIZE_Y, null);
	}

	/**
	 * Creates all the vehicle sprites for the players
	 */
	private void CreateVehicles() {
		
		ArrayList<Player> temp = Controller.GetController().get_GameController().getPlayers();
		
		for (int i = 0; i < temp.size(); i++)
		{
			_VehicleLabels.add(new InGameLabel(temp.get(i)));
			
			_VehicleLabels.get(i).setLocation(Controller.GetController().get_GameController().get_PlayerStartLocations().get(Controller.GetController().getPlayers().get(i).getID() - 1));
			
			_VehicleLabels.get(i).Initialise(this);
			
			this.add(_VehicleLabels.get(i));
						
			_VehicleLabels.get(i).setStartImage();
			
			_VehicleLabels.get(i).setVisible(true);

			_VehicleLabels.get(i).setLocation(temp.get(i).get_VehicleLogic().get_Location());
		}
		
	}
	
	/**
	 * Makes all the vehicles calculate their next frame and redraws the map around that
	 */
	public void Frame()
	{
		Arrays.stream(_VehicleLabels.toArray()).forEach(vehicle -> {
			if(Controller.GetController().get_GameController() != null)
			{
				((InGameLabel) vehicle).CalcNextFrame();
			}
		});
		
		repaint();		
	}

	/**
	 * When a key is pressed checks if it is:
	 * 
	 * W: To move the vehicle forward
	 * S: To move the vehicle backwards
	 * ESC: To ask the user if they wish to quit
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_W)
		{
			Controller.GetController().getPlayers().get(0).getSprite().get_Vehicle().set_forward(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			Controller.GetController().getPlayers().get(0).getSprite().get_Vehicle().set_backwards(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_D)
		{
			Controller.GetController().getPlayers().get(0).getSprite().get_Vehicle().set_right(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_A)
		{
			Controller.GetController().getPlayers().get(0).getSprite().get_Vehicle().set_left(true);
		}
		
		if(e.getKeyCode() == KeyEvent.VK_I)
		{
			Controller.GetController().getPlayers().get(1).getSprite().get_Vehicle().set_forward(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_K)
		{
			Controller.GetController().getPlayers().get(1).getSprite().get_Vehicle().set_backwards(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_L)
		{
			Controller.GetController().getPlayers().get(1).getSprite().get_Vehicle().set_right(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_J)
		{
			Controller.GetController().getPlayers().get(1).getSprite().get_Vehicle().set_left(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if(Controller.GetController().OpenJOptionsPane("Do you wish to quit?") == JOptionPane.YES_OPTION)
			{
				System.exit(0);
			}
		}
			
	}

	/**
	 * When a key is release checks if it is:
	 * 
	 * W: To stop moving the vehicle forward
	 * S: To stop moving the vehicle backwards
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		
		if(e.getKeyCode() == KeyEvent.VK_W)
		{
			Controller.GetController().getPlayers().get(0).getSprite().get_Vehicle().set_forward(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			Controller.GetController().getPlayers().get(0).getSprite().get_Vehicle().set_backwards(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_D)
		{
			Controller.GetController().getPlayers().get(0).getSprite().get_Vehicle().set_right(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_A)
		{
			Controller.GetController().getPlayers().get(0).getSprite().get_Vehicle().set_left(false);
		}
		
		if(e.getKeyCode() == KeyEvent.VK_I)
		{
			Controller.GetController().getPlayers().get(1).getSprite().get_Vehicle().set_forward(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_K)
		{
			Controller.GetController().getPlayers().get(1).getSprite().get_Vehicle().set_backwards(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_L)
		{
			Controller.GetController().getPlayers().get(1).getSprite().get_Vehicle().set_right(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_J)
		{
			Controller.GetController().getPlayers().get(1).getSprite().get_Vehicle().set_left(false);
		}
		
	}

	/**
	 * Stub override
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	/**
	 * Gets the vehicle labels of all the vehicles
	 * @return All the vehicles labels
	 */
	public ArrayList<InGameLabel> getVehicleLabels()
	{
		return _VehicleLabels;
	}
	
	/**
	 * Gets all the checkpoints
	 * @return All the checkpoint labels
	 */
	public ArrayList<Checkpoint> get_CheckPointLabels()
	{
		return _CheckPointLabels;
	}
}
