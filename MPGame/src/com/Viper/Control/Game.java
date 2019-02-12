package com.Viper.Control;

import com.Viper.UI.UIControl;

import javafx.collections.ObservableList;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class Game implements ActionListener {

	private UIControl _UI;
	
	private CollisionManagment _CM;
	
	private Timer _FrameTimer;
	
	private ArrayList<Player> _Players;
	
	private Player _LocalPlayer;
	
	private BufferedImage _Map;
	
	private String _MapName;
	
	private ArrayList<Point> _PlayerStartLocations;
	
	private ArrayList<Point> _CheckPointLocations;
	
	private int _TrackWidth;
	
	public Game(ObservableList<Player> _ObPlayers)
	{
		_UI = UIControl.GetInstance(); 
		
		_Players = new ArrayList<Player>(_ObPlayers);
	}
	
	public void StartGame()
	{
		File f = new File("src/imgs/maptextures/");
		File[] files = f.listFiles();
		try {
			set_Map(ImageIO.read(files[Controller.GetController().get_SelectedMap()]));
			_MapName = files[Controller.GetController().get_SelectedMap()].getName();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		openProperties();
		
		_CM = new CollisionManagment(_Map);
		
		_FrameTimer = new Timer(20, this);
		
		_FrameTimer.start();
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		_UI.UpdateIngameScreen();
		
	}

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
	
	public ArrayList<Player> getPlayers()
	{
		return _Players;
	}

	public BufferedImage get_Map() {
		return _Map;
	}

	public void set_Map(BufferedImage _Map) {
		this._Map = _Map;
	}

	public CollisionManagment get_CM() {
		return _CM;
	}
	
	private void openProperties()
	{
		Properties prop = new Properties();
		_PlayerStartLocations = new ArrayList<Point>();
		_CheckPointLocations = new ArrayList<Point>();
		try {
			InputStream in = new FileInputStream("src/properties/" + _MapName + ".properties");
			
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
	
	public ArrayList<Point> get_PlayerStartLocations()
	{
		return _PlayerStartLocations;
	}
	
	public int get_TrackWidth()
	{
		return _TrackWidth;
	}
	
	public ArrayList<Point> get_CheckPointLocations()
	{
		return _CheckPointLocations;
	}
	
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
