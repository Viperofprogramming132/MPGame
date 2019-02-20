package com.Viper.UI.InGame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
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
public class InGame extends JPanel implements ActionListener, KeyListener, MouseMotionListener{
	
	/**
	 * The size of the image in pixel on the X
	 */
	private int WORLD_SIZE_X;
	
	/**
	 * The size of the image in pixel on the Y
	 */
	private int WORLD_SIZE_Y;
	
	/**
	 * The size of the screen so the camera scrolls with the player on the X
	 */
	private final int VIEWPOINT_SIZE_X = 1500;
	
	/**
	 * The size of the screen so the camera scrolls with the player on the Y
	 */
	private final int VIEWPOINT_SIZE_Y = 1000;

	/**
	 * The angle that the mouse is pointing to from the local players vehicle
	 */
	private double _imageAngleRad;
	
	/**
	 * The link to the local player
	 */
	private Player _nonRemotePlayer;
	
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
	 * The cameras X coordinate
	 */
	private int _camX;
	
	/**
	 * The cameras Y coordinate
	 */
	private int _camY;
	
	/**
	 * Creates a InGame JPanel that will draw the map ready for the game
	 * 
	 * Contains all the vehicles
	 * @param nonRemotePlayer The player that is the local player for the user to control
	 */
	public InGame(Player nonRemotePlayer)
	{
		_nonRemotePlayer = nonRemotePlayer;
		
		Controller.GetController().get_GameController().StartGame();
		
		addMouseMotionListener(this);
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
	 * Draws the map and transforms it with the player
	 * Uses a system laid out:
	 * https://gamedev.stackexchange.com/questions/44256/how-to-add-a-scrolling-camera-to-a-2d-java-game?fbclid=IwAR1B_fd0fsW1EtDklCge7D8VsdFOdWQ9TBtHCs_IxpkgyLXenniI9M2Hnoc
	 * @param g The graphics of the panel
	 */
	private void DrawMap(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform tx = new AffineTransform();
		if(_Map == null)
		{
			_Map = Controller.GetController().get_GameController().get_Map();
			
			WORLD_SIZE_X = _Map.getWidth(null) * 8;
			WORLD_SIZE_Y = _Map.getHeight(null) * 8;
		}
		
		int offsetMaxX = WORLD_SIZE_X - VIEWPOINT_SIZE_X;
		int offsetMaxY = WORLD_SIZE_Y - VIEWPOINT_SIZE_Y;
		int offsetMinX = 0;
		int offsetMinY = 0;
		
		_camX = (_nonRemotePlayer.getSprite().getX() + (_nonRemotePlayer.getSprite().getWidth() / 2)) - VIEWPOINT_SIZE_X / 2;
		_camY = (_nonRemotePlayer.getSprite().getY() + (_nonRemotePlayer.getSprite().getHeight() / 2)) - VIEWPOINT_SIZE_Y / 2;
		
		if (_camX > offsetMaxX)
			_camX = offsetMaxX;
		if (_camY > offsetMaxY)
			_camY = offsetMaxY;
		if (_camX < offsetMinX)
			_camX = offsetMinX;
		if (_camY < offsetMinY)
			_camY = offsetMinY;
		
		tx.translate(-_camX, -_camY);
		
		g2d.transform(tx);

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
			
			_VehicleLabels.get(i).Initialise(this);
			
			this.add(_VehicleLabels.get(i));
						
			_VehicleLabels.get(i).setStartImage();
			
			_VehicleLabels.get(i).setVisible(true);
			
			_VehicleLabels.get(i).setLocation(Controller.GetController().get_GameController().get_PlayerStartLocations().get(_nonRemotePlayer.getID() - 1));
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
	 * Stub override
	 */
	@Override
	public void mouseDragged(MouseEvent e) {		
	}

	/**
	 * Get the angle the mouse is compared to the player in radians
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		this.requestFocus();
		
        double dx = (e.getX() - (_nonRemotePlayer.getSprite().getX() + (_nonRemotePlayer.getSprite().getWidth() / 2))) + _camX;
        double dy = (e.getY() - (_nonRemotePlayer.getSprite().getY() + (_nonRemotePlayer.getSprite().getHeight() / 2))) + _camY;
        _imageAngleRad = Math.atan2(dy, dx);

        _nonRemotePlayer.getSprite().get_Vehicle().setAngle(_imageAngleRad);
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
			_nonRemotePlayer.getSprite().get_Vehicle().set_forward(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			_nonRemotePlayer.getSprite().get_Vehicle().set_backwards(true);
		}
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if(Controller.GetController().OpenJOptionsPane("Do you wish to quit?") == JOptionPane.YES_OPTION)
			{
				Controller.GetController().Disconnect(true);
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
			_nonRemotePlayer.getSprite().get_Vehicle().set_forward(false);
		}
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			_nonRemotePlayer.getSprite().get_Vehicle().set_backwards(false);
		}
		
	}

	/**
	 * Stub override
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	/**
	 * Stub override
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
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
