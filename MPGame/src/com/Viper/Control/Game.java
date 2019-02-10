package com.Viper.Control;

import com.Viper.Debug.ShowImageForm;
import com.Viper.UI.UIControl;

import javafx.collections.ObservableList;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class Game implements ActionListener {

	private UIControl _UI;
	
	private CollisionManagment _CM;
	
	private Timer _FrameTimer;
	
	private ArrayList<Player> _Players;
	
	private Player _LocalPlayer;
	
	private BufferedImage _Map;
	
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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

}
