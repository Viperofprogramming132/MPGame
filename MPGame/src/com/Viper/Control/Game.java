package com.Viper.Control;

import com.Viper.UI.UIControl;

import javafx.collections.ObservableList;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 * The Game controls all the game logic timing, collision management, locations for all the objects
 * 
 * @author Aidan
 *
 */
public class Game implements ActionListener {

	/**
	 * Link to the UI Control so it can update the ingame screen
	 */
	private UIControl _UI;
	
	/**
	 * The collision management for checking for collision to other objects (Map, vehicles or checkpoints)
	 */
	private CollisionManagment _CM;
	
	/**
	 * The Main frame timer controls the FPS otherwise each from would have to be calculated with the time taken since the last frame
	 */
	private Timer _FrameTimer;
	
	/**
	 * Local list of the players this is made when the game starts
	 */
	private ArrayList<Player> _Players;
	
	/**
	 * The player that the player controls
	 */
	private Player _LocalPlayer;
	
	/**
	 * The Map image
	 */
	private BufferedImage _Map;
	
	/**
	 * The name of map used to get the properties file
	 */
	private String _MapName;
	
	/**
	 * List of the start points of all the players
	 */
	private ArrayList<Point> _PlayerStartLocations;
	
	/**
	 * The Checkpoint locations for the map read form properties
	 */
	private ArrayList<Point> _CheckPointLocations;
	
	/**
	 * The width of the track
	 */
	private int _TrackWidth;

	private ArrayList<String> _PossibleMaps;
	
	/**
	 * Creates a game with with the current players
	 * @param ObPlayers The current Players
	 */
	public Game(ObservableList<Player> ObPlayers)
	{
		_UI = UIControl.GetInstance(); 
		
		_Players = new ArrayList<Player>(ObPlayers);
		
		PopulateMapSelector();
	}
	
	/**
	 * Populates the map with the files in the folder
	 */
	private void PopulateMapSelector() {
		BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/properties/PossibleMaps.txt")));
		
		_PossibleMaps = new ArrayList<String>();
		String line = null;
		do
		{
			try {
				line = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(line != null)
			{
				_PossibleMaps.add(line);
			}
		} while (line != null);
	}
	
	/**
	 * Starts the game loading the map
	 * Gets the start locations and checkpoint locations
	 * Starts the timer for the game
	 */
	public void StartGame()
	{
		
		try {
			set_Map(ImageIO.read(this.getClass().getResourceAsStream("/imgs/maptextures/" + _PossibleMaps.get(Controller.GetController().get_SelectedMap()))));
			_MapName = _PossibleMaps.get(Controller.GetController().get_SelectedMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		openProperties();
		
		_CM = new CollisionManagment(_Map);
		
		_FrameTimer = new Timer(20, this);
		
		_FrameTimer.start();
		
	}
	
	/**
	 * The override for the timer to call the update frame
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		_UI.UpdateIngameScreen();
		
	}

	/**
	 * Gets the local player from the list of players
	 * @return The local player
	 */
	public Player GetLocalPlayer() {
		
		if(_LocalPlayer != null)
			return _LocalPlayer;
		
		for (int i = 0; i < _Players.size(); i++)
		{
			if(!_Players.get(i).isRemotePlayer())
			{
				_LocalPlayer = _Players.get(i);
				return _Players.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the list of players
	 * @return The lists of the players 
	 */
	public ArrayList<Player> getPlayers()
	{
		return _Players;
	}

	/**
	 * Gets the Map image
	 * @return The image of the map
	 */
	public BufferedImage get_Map() {
		return _Map;
	}

	/**
	 * sets the map to given buffered image
	 * @param _Map The image to set the map to
	 */
	public void set_Map(BufferedImage _Map) {
		this._Map = _Map;
	}

	/**
	 * Gets the collision management system
	 * @return The current collision management system
	 */
	public CollisionManagment get_CM() {
		return _CM;
	}
	
	/**
	 * Opens the properties file of the current map if it exists
	 * Contains 8 start locations for the 8 players
	 * Contains 2 checkpoint locations the first being the start
	 * Adds them all the the relevent arrays
	 */
	private void openProperties()
	{
		Properties prop = new Properties();
		_PlayerStartLocations = new ArrayList<Point>();
		_CheckPointLocations = new ArrayList<Point>();
		
		try {
			InputStream in = this.getClass().getResourceAsStream("/properties/" + _MapName + ".properties");
			
			prop.load(in);
				
			Point p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("startloc1x"));
			p.y = Integer.parseInt(prop.getProperty("startloc1y"));
			
			_PlayerStartLocations.add(p);
			
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("startloc2x"));
			p.y = Integer.parseInt(prop.getProperty("startloc2y"));
			
			_PlayerStartLocations.add(p);
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("startloc3x"));
			p.y = Integer.parseInt(prop.getProperty("startloc3y"));
			
			_PlayerStartLocations.add(p);
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("startloc4x"));
			p.y = Integer.parseInt(prop.getProperty("startloc4y"));
			
			_PlayerStartLocations.add(p);
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("startloc5x"));
			p.y = Integer.parseInt(prop.getProperty("startloc5y"));
			
			_PlayerStartLocations.add(p);
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("startloc6x"));
			p.y = Integer.parseInt(prop.getProperty("startloc6y"));
			
			_PlayerStartLocations.add(p);
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("startloc7x"));
			p.y = Integer.parseInt(prop.getProperty("startloc7y"));
			
			_PlayerStartLocations.add(p);
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("startloc8x"));
			p.y = Integer.parseInt(prop.getProperty("startloc8y"));
			
			_PlayerStartLocations.add(p);
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("checkpoint1locx"));
			p.y = Integer.parseInt(prop.getProperty("checkpoint1locy"));
			
			_CheckPointLocations.add(p);
			p = new Point();
			
			p.x = Integer.parseInt(prop.getProperty("checkpoint2locx"));
			p.y = Integer.parseInt(prop.getProperty("checkpoint2locy"));
			
			_CheckPointLocations.add(p);
			
			_TrackWidth = Integer.parseInt(prop.getProperty("TrackWidth"));
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the array of the player start locations
	 * @return the List of player start locations as points
	 */
	public ArrayList<Point> get_PlayerStartLocations()
	{
		return _PlayerStartLocations;
	}
	
	/**
	 * Gets the width of the track
	 * @return TrackWidth int being the number of pixels
	 */
	public int get_TrackWidth()
	{
		return _TrackWidth;
	}
	
	/**
	 * Gets the array of checkpoint locations element 0 should be the start/finish
	 * @return A list of points with the checkpoint locations
	 */
	public ArrayList<Point> get_CheckPointLocations()
	{
		return _CheckPointLocations;
	}
	
	/**
	 * Closes all the threads and stops the timer ready for closure of the game
	 */
	public void CloseGame()
	{
		_CM.Close();
		_CM = null;
		_FrameTimer.stop();
		_FrameTimer = null;
		
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
