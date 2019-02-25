package com.Viper.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.Viper.Control.Controller;

/**
 * The JPanel that deals with the lobby UI
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class Lobby extends JPanel implements ActionListener{
	
	/**
	 * The location of the vehicles so the system can get the image out for vehicle selection
	 */
	private final String VEHICLELOCATION = "src/imgs/vehicles/";
	
	/**
	 * The location for the maps
	 */
	private final String MAPLOCATION = "src/imgs/maptextures/";
	
	/**
	 * The button to go left on the vehicle selection for player 1
	 */
	private JButton _VehicleLeft1;
	
	/**
	 * The button to go right on the vehicle selection for player 1
	 */
	private JButton _VehicleRight1;
	
	
	/**
	 * The button to go left on the vehicle selection for player 2
	 */
	private JButton _VehicleLeft2;
	
	/**
	 * The button to go right on the vehicle selection for player 2
	 */
	private JButton _VehicleRight2;
	
	/**
	 * The button to start the game if host or ready if not
	 */
	private JButton _Create;
	
	/**
	 * The button to exit the program
	 */
	private JButton _Exit;
	
	/**
	 * Selects the next map to the left
	 */
	private JButton _MapLeft;
	
	/**
	 * Selects the next map to the right
	 */
	private JButton _MapRight;
	
	/**
	 * The Container that contains all the components 
	 */
	private JPanel _MenuContainer;
	
	/**
	 * Label denoting where the player 1s stuff is 
	 */
	private JLabel _Player1Label;
	
	/**
	 * Label denoting where the player 2s stuff is 
	 */
	private JLabel _Player2Label;
	
	/**
	 * Array of file of the possible vehicles
	 */
	private File[] _PossibleVehicles;
	
	/**
	 * The currently shown vehicle when the game starts this is the one that will be chosen for player 1
	 */
	private Integer _ShownVehicles1 = 0;
	
	/**
	 * The currently shown vehicle when the game starts this is the one that will be chosen for player 2
	 */
	private Integer _ShownVehicles2 = 0;
	
	/**
	 * The array of possible maps that can be chosen from
	 */
	private File[] _PossibleMaps;
	
	/**
	 * The currently shown map. This map will be used on creation of the server
	 */
	private Integer _ShownMap = 0;
	
	/**
	 * Creates a lobby UI
	 * @param host If the user is hosting the server
	 */
	public Lobby()
	{
		this.setLayout(null);
		
	    PopulateVehicleSelector();
	    
	    PopulateMapSelector();

		MakeButtons();
		
		PopulateContainer();		
		
		this.add(_MenuContainer);
		this.setVisible(true);
	}
	
	/**
	 * Gets all the files in the vehicle location folder
	 */
	private void PopulateVehicleSelector() {
		File vehicleFiles = new File(VEHICLELOCATION);
		_PossibleVehicles = vehicleFiles.listFiles();
	}
	
	/**
	 * Populates the map with the files in the folder
	 */
	private void PopulateMapSelector() {
		File MapFiles = new File(MAPLOCATION);
		_PossibleMaps = MapFiles.listFiles();
	}
	
	/**
	 * Attempts to read an image from the specified file
	 * @param path The file that the system will attempt to read
	 * @return The image that is read
	 * @throws IOException If the file fails to read the image due to it not existing or read/write error
	 */
	private Image ReadImage(File path) throws IOException
	{
		BufferedImage img = ImageIO.read(path);
		Image i = new ImageIcon(img).getImage();
		return i;
	}
	
	/**
	 * Controls the displaying of the vehicle
	 */
	private void DisplayVehicle(int toChange)
	{
		//change the first player
		if(toChange == 1)
		{
			//Stop out of range errors
			if (_ShownVehicles1 > _PossibleVehicles.length - 1)
			{
				_ShownVehicles1 = 0;
			}
			if (_ShownVehicles1 < 0)
			{
				_ShownVehicles1 = _PossibleVehicles.length - 1;
			}
			
			Controller.GetController().getPlayers().get(0).setSpriteIndex(_ShownVehicles1);
		}
		//change player 2
		else
		{
			//Stop out of range errors
			if (_ShownVehicles2 > _PossibleVehicles.length - 1)
			{
				_ShownVehicles2 = 0;
			}
			if (_ShownVehicles2 < 0)
			{
				_ShownVehicles2 = _PossibleVehicles.length - 1;
			}
			
			Controller.GetController().getPlayers().get(1).setSpriteIndex(_ShownVehicles2);
		}
		
		repaint();
	}
	
	/**
	 * Handles displaying of the correct map
	 */
	private void DisplayMap()
	{
		if (_ShownMap > _PossibleMaps.length - 1)
		{
			_ShownMap = 0;
		}
		if (_ShownMap < 0)
		{
			_ShownMap = _PossibleMaps.length - 1;
		}
		
		repaint();
	}
	
	/**
	 * Repaints the vehicle at the new rotation according to the mouse location 
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		Image i = null;
		try {
			i = ReadImage(_PossibleVehicles[_ShownVehicles1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        g2d.drawImage(i, 160, 120, i.getWidth(null) * 5, i.getHeight(null) * 5, null);
        
		i = null;
		try {
			i = ReadImage(_PossibleVehicles[_ShownVehicles2]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		g2d.drawImage(i, 160, 620, i.getWidth(null) * 5, i.getHeight(null) * 5, null);
		
		i = null;
		
		try {
			i = ReadImage(_PossibleMaps[_ShownMap]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		g2d.drawImage(i, 892, 50, 400, 400, this);
	}

	/**
	 * Populates the container with all the components
	 */
	private void PopulateContainer() {
		_MenuContainer = new JPanel();
		
		_MenuContainer.setBounds(0, 0, 1500, 1000);
		
		_MenuContainer.setLayout(null);
		_MenuContainer.add(_VehicleLeft1);
		_MenuContainer.add(_Create);
		_MenuContainer.add(_Exit);
		_MenuContainer.add(_VehicleRight1);
		_MenuContainer.add(_VehicleLeft2);
		_MenuContainer.add(_VehicleRight2);
		_MenuContainer.add(_Player1Label);
		_MenuContainer.add(_Player2Label);
		_MenuContainer.add(_MapLeft);
		_MenuContainer.add(_MapRight);
		_MenuContainer.setVisible(true);
		_MenuContainer.setOpaque(false);
	}

	/**
	 * Makes all the components
	 */
	private void MakeButtons() {
		_VehicleLeft1 = new JButton("<");
		_VehicleRight1 = new JButton(">");
		_VehicleLeft2 = new JButton("<");
		_VehicleRight2 = new JButton(">");
		_Create = new JButton("Start Game");
		_Exit = new JButton("X");
		_Player1Label = new JLabel("Player 1");
		_Player2Label = new JLabel("Player 2");
		_MapLeft = new JButton("<");
		_MapRight = new JButton(">");
		
		//Set the names
		_VehicleLeft1.setName("Player1Left");
		_VehicleRight1.setName("Player1Right");
		_VehicleLeft2.setName("Player2Left");
		_VehicleRight2.setName("Player2Right");
		_Create.setName("Start Game");
		_Exit.setName("X");
		_MapLeft.setName("MapLeft");
		_MapRight.setName("MapRight");
		
		//Set sizes
		_VehicleLeft1.setSize(50, 50);
		_VehicleRight1.setSize(50, 50);
		_VehicleLeft2.setSize(50, 50);
		_VehicleRight2.setSize(50, 50);
		_Create.setSize(200, 75);
		_Exit.setSize(50, 50);
		_Player1Label.setSize(110, 50);
		_Player2Label.setSize(110, 50);
		_MapLeft.setSize(50, 50);
		_MapRight.setSize(50, 50);
		
		//Set locations
		_VehicleLeft1.setLocation(15, 220);
		_VehicleRight1.setLocation(520, 220);
		_VehicleLeft2.setLocation(15, 720);
		_VehicleRight2.setLocation(520, 720);
		_Create.setLocation(1200, 880);
		_Exit.setLocation(1420, 900);
		_Player1Label.setLocation(250, 50);
		_Player2Label.setLocation(250, 540);
		_MapLeft.setLocation(812, 220);
		_MapRight.setLocation(1322, 220);
		
		//Set fonts
		_VehicleLeft1.setFont(new Font("Consolas",Font.BOLD, 14));
		_VehicleRight1.setFont(new Font("Consolas",Font.BOLD, 14));
		_VehicleLeft2.setFont(new Font("Consolas",Font.BOLD, 14));
		_VehicleRight2.setFont(new Font("Consolas",Font.BOLD, 14));
		_Create.setFont(new Font("Consolas",Font.BOLD, 12));
		_Exit.setFont(new Font("Consolas",Font.BOLD, 12));
		_Player1Label.setFont(new Font("Consolas",Font.BOLD, 12));
		_Player2Label.setFont(new Font("Consolas",Font.BOLD, 12));
		_MapLeft.setFont(new Font("Consolas",Font.BOLD, 14));
		_MapRight.setFont(new Font("Consolas",Font.BOLD, 14));
		
		//Set backgrounds
		_VehicleLeft1.setBackground(Color.WHITE);
		_VehicleRight1.setBackground(Color.WHITE);
		_VehicleLeft2.setBackground(Color.WHITE);
		_VehicleRight2.setBackground(Color.WHITE);
		_Create.setBackground(Color.WHITE);
		_Exit.setBackground(Color.WHITE);
		_MapLeft.setBackground(Color.WHITE);
		_MapRight.setBackground(Color.WHITE);
		
		//Add listeners
		_VehicleLeft1.addActionListener(this);
		_VehicleRight1.addActionListener(this);
		_VehicleLeft2.addActionListener(this);
		_VehicleRight2.addActionListener(this);
		_Create.addActionListener(this);
		_Exit.addActionListener(this);
		_MapLeft.addActionListener(this);
		_MapRight.addActionListener(this);
	}

	/**
	 * Action performed for button presses
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().getClass() == JButton.class )
        {
            //Casting to button
            JButton temp = (JButton)e.getSource();
            //Select the action based on button's text
            switch (temp.getName())
            {
                case "Player1Left":
                	System.out.println("Player1Left");
                	_ShownVehicles1--;
                	DisplayVehicle(1);
                    break;
                case "Player1Right":
                	System.out.println("Player1Right");
                	_ShownVehicles1++;
                	DisplayVehicle(1);
                    break;
                case "Player2Left":
                	System.out.println("Player2Left");
                	_ShownVehicles2--;
                	DisplayVehicle(2);
                    break;
                case "Player2Right":
                	System.out.println("Player2Right");
                	_ShownVehicles2++;
                	DisplayVehicle(2);
                    break;
                case "MapLeft":
                	System.out.println("MapLeft");
                	_ShownMap--;
                	DisplayMap();
                    break;
                case "MapRight":
                	System.out.println("MapRight");
                	_ShownMap++;
                	DisplayMap();
                    break;
                case "Start Game":
                	System.out.println("Start Game");
                	Controller.GetController().set_SelectedMap(_ShownMap);
                	Controller.GetController().StartGame();
                	break;
                case "X":
                    Controller.GetController().ExitProgram();
                	System.out.println("X");
                    break;
            }
        }
		
	}
}
