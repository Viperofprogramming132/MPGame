package com.Viper.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.Viper.Control.Controller;

/**
 * The Panel that deals with the UI of hosting a server
 * 
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class Server extends JPanel implements ActionListener{

	/**
	 * The location for the maps
	 */
	private final String MAPLOCATION = "src/imgs/maptextures/";
	
	/**
	 * The button that gets the previous map
	 */
	private JButton _Left;
	
	/**
	 * The button that gets the next map
	 */
	private JButton _Right;
	
	/**
	 * The button that creates the server
	 */
	private JButton _Create;
	
	/**
	 * The button that exits the application
	 */
	private JButton _Exit;
	
	/**
	 * The Container the contains all the components
	 */
	private JPanel _MenuContainer;
	
	/**
	 * The array of possible maps that can be chosen from
	 */
	private ArrayList<String> _PossibleMaps;
	
	/**
	 * The currently shown map. This map will be used on creation of the server
	 */
	private Integer _ShownMap = 0;
	
	/**
	 * Creates a Server UI used to host a game
	 */
	public Server()
	{
		this.setLayout(null);
		
	    _PossibleMaps = Controller.GetController().PopulateMapSelector();
	    
	    DisplayMap();
		
		MakeButtons();
		
		PopulateContainer();
		
		this.add(_MenuContainer);
		this.setVisible(true);
	}
	

	
	/**
	 * Reads and image from the specified file
	 * @param path The File to read the image from
	 * @return The image that was read
	 * @throws IOException If the file fails to read the image due to it not existing or read/write error
	 */
	private Image ReadImage(String path) throws IOException
	{
		path = "/imgs/maptextures/" + path;
		BufferedImage img = ImageIO.read(this.getClass().getResourceAsStream(path));
		Image i = new ImageIcon(img).getImage();
		return i;
	}
	
	/**
	 * Handles displaying of the correct map
	 */
	private void DisplayMap()
	{
		if (_ShownMap > _PossibleMaps.size() - 1)
		{
			_ShownMap = 0;
		}
		if (_ShownMap < 0)
		{
			_ShownMap = _PossibleMaps.size() - 1;
		}
		
		repaint();
	}
	
	/**
	 * Paints the correct map in the middle of the screen at a scaled size
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		Image i = null;
		
		try {
			i = ReadImage(_PossibleMaps.get(_ShownMap));
		} catch (IOException e) {
			e.printStackTrace();
		}
		g2d.drawImage(i, 92, 50, 400, 400, this);
	}

	/**
	 * Populates the container with all the components
	 */
	private void PopulateContainer() {
		_MenuContainer = new JPanel();
		
		_MenuContainer.setBounds(0, 0, 600, 600);
		
		_MenuContainer.setLayout(null);
		_MenuContainer.add(_Left);
		_MenuContainer.add(_Create);
		_MenuContainer.add(_Exit);
		_MenuContainer.add(_Right);
		_MenuContainer.setVisible(true);
		_MenuContainer.setOpaque(false);
	}

	/**
	 * Makes all the components
	 */
	private void MakeButtons() {
		_Left = new JButton("<");
		_Right = new JButton(">");
		_Create = new JButton("Enter Server Lobby");
		_Exit = new JButton("X");
		
		//Sets the size
		_Left.setSize(50, 50);
		_Right.setSize(50, 50);
		_Create.setSize(200, 75);
		_Exit.setSize(50, 50);
		
		//Sets the location
		_Left.setLocation(15, 220);
		_Right.setLocation(520, 220);
		_Create.setLocation(200, 475);
		_Exit.setLocation(520, 498);
		
		//Sets the font
		_Left.setFont(new Font("Consolas",Font.BOLD, 14));
		_Right.setFont(new Font("Consolas",Font.BOLD, 14));
		_Create.setFont(new Font("Consolas",Font.BOLD, 12));
		_Exit.setFont(new Font("Consolas",Font.BOLD, 12));
		
		//Sets the background
		_Left.setBackground(Color.WHITE);
		_Right.setBackground(Color.WHITE);
		_Create.setBackground(Color.WHITE);
		_Exit.setBackground(Color.WHITE);
		
		//Adds action listeners
		_Left.addActionListener(this);
		_Right.addActionListener(this);
		_Create.addActionListener(this);
		_Exit.addActionListener(this);
	}

	/**
	 * The action when buttons are pressed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().getClass() == JButton.class )
        {
            //Casting to button
            JButton temp = (JButton)e.getSource();
            //Select the action based on button's text
            switch (temp.getText())
            {
                case "<":
                	System.out.println("<");
                	_ShownMap--;
                	DisplayMap();
                    break;
                case ">":
                	System.out.println(">");
                	_ShownMap++;
                	DisplayMap();
                    break;
                case "Enter Server Lobby":
                	System.out.print("Enter game lobby");
                	Controller.GetController().set_SelectedMap(_ShownMap);
                	Controller.GetController().StartServer();
                	Controller.GetController().ConnectToGame(true);
                	break;
                case "X":
                    Controller.GetController().ExitProgram();
                	System.out.println("X");
                    break;
            }
        }
		
	}
	
}