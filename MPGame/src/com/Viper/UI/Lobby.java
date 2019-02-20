package com.Viper.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;

/**
 * The JPanel that deals with the lobby UI
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class Lobby extends JPanel implements ActionListener, MouseMotionListener{
	
	/**
	 * The location of the vehicles so the system can get the image out for vehicle selection
	 */
	private final String VEHICLELOCATION = "src/imgs/vehicles/";
	
	/**
	 * The button to go left on the vehicle selection
	 */
	private JButton _Left;
	
	/**
	 * The button to go right on the vehicle selection
	 */
	private JButton _Right;
	
	/**
	 * The button to start the game if host or ready if not
	 */
	private JButton _Create;
	
	/**
	 * The button to exit the program
	 */
	private JButton _Exit;
	
	/**
	 * The Container that contains all the components 
	 */
	private JPanel _MenuContainer;
	
	/**
	 * The List view of all the player names and if they are ready or not
	 */
	private JList<Player> _PlayerView;
	
	/**
	 * The Chat box
	 */
	private JTextArea _Chat;
	
	/**
	 * The box to type a message into for the chat
	 */
	private JTextField _Message;
	
	/**
	 * The button to send a message to the chat
	 */
	private JButton _Send;
	
	/**
	 * The scroll pane allowing for scrolling of the chat and automatically scrolling to the bottom
	 */
	private JScrollPane _ChatScrollPane;
	
	/**
	 * The Players name (Changeable)
	 */
	private JTextField _PlayerName;
	
	/**
	 * The label to tell the user this is their name and to change it
	 */
	private JLabel _PlayerNameLabel;
	
	/**
	 * Array of file of the possible vehicles
	 */
	private File[] _PossibleVehicles;
	
	/**
	 * The currently shown vehicle when the game starts this is the one that will be chosen
	 */
	private Integer _ShownVehicles = 0;
	
	/**
	 * If the user of the lobby is the host
	 */
	private final boolean _Host;
	
	/**
	 * The list that is input to the JList
	 */
	private DefaultListModel<Player> _PlayerListModel;
	
	/**
	 * The angle the vehicle is rotated to
	 */
	private double _imageAngleRad = 0;
	
	/**
	 * Creates a lobby UI
	 * @param host If the user is hosting the server
	 */
	public Lobby(boolean host)
	{
		this.setLayout(null);
		
		addMouseMotionListener(this);
		
		_Host = host;
		
	    PopulateVehicleSelector();
	    
	    DisplayVehicle();
	    
		PopulatePlayerView();
		
		MakeButtons();
		
		PopulateContainer();
		
		ListCurrentPlayers();
		
		
		
		this.add(_MenuContainer);
		this.setVisible(true);
	}
	
	/**
	 * List the current players that are in the lobby to the chat on player join
	 */
	private void ListCurrentPlayers() {
		ArrayList<Player> players = new ArrayList<>(Controller.GetController().getPlayers());
		
		String playerList;
		
		playerList = players.get(0).getName();
		players.remove(0);
		for (Player p : players)
		{
			playerList += ", " + p.getName();
		}
		
		AppendChat(playerList);
	}

	/**
	 * Populates the player view that displays the current players
	 */
	private void PopulatePlayerView()
	{
		if(_PlayerView == null)
		{
			_PlayerListModel = new DefaultListModel<Player>();
			_PlayerView = new JList<Player>(_PlayerListModel);
		}
		
		Controller.GetController().getPlayers().forEach(s -> _PlayerListModel.addElement(s));
		
		_PlayerView.setLocation(585, 15);
		_PlayerView.setSize(200, 500);
		
	}
	
	/**
	 * Gets all the files in the vehicle location folder
	 */
	private void PopulateVehicleSelector() {
		File vehicleFiles = new File(VEHICLELOCATION);
		_PossibleVehicles = vehicleFiles.listFiles();
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
	private void DisplayVehicle()
	{
		//Stop out of range errors
		if (_ShownVehicles > _PossibleVehicles.length - 1)
		{
			_ShownVehicles = 0;
		}
		if (_ShownVehicles < 0)
		{
			_ShownVehicles = _PossibleVehicles.length - 1;
		}
		
		Controller.GetController().getClient().getLocalPlayer().setSpriteIndex(_ShownVehicles);
		
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
			i = ReadImage(_PossibleVehicles[_ShownVehicles]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		
		int cx = (i.getWidth(null) * 5) / 2;
		int cy = (i.getHeight(null) * 5)/ 2;
		
        AffineTransform oldAT = g2d.getTransform();
        g2d.translate(cx+160, cy+140);
        g2d.rotate(_imageAngleRad);
        g2d.translate(-cx, -cy);
        g2d.drawImage(i, 0, 0, i.getWidth(null) * 5, i.getHeight(null) * 5, null);
        g2d.setTransform(oldAT);
	}

	/**
	 * Populates the container with all the components
	 */
	private void PopulateContainer() {
		_MenuContainer = new JPanel();
		
		_MenuContainer.setBounds(0, 0, 1500, 600);
		
		_MenuContainer.setLayout(null);
		_MenuContainer.add(_Left);
		_MenuContainer.add(_Create);
		_MenuContainer.add(_Exit);
		_MenuContainer.add(_Right);
		_MenuContainer.add(_ChatScrollPane);
		_MenuContainer.add(_Message);
		_MenuContainer.add(_Send);
		_MenuContainer.add(_PlayerView);
		_MenuContainer.add(_PlayerName);
		_MenuContainer.add(_PlayerNameLabel);
		_MenuContainer.setVisible(true);
		_MenuContainer.setOpaque(false);
	}

	/**
	 * Makes all the components
	 */
	private void MakeButtons() {
		_Left = new JButton("<");
		_Right = new JButton(">");
		if(_Host)
			_Create = new JButton("Start Game");
		else
			_Create = new JButton("Ready");
		_Exit = new JButton("X");
		_Send = new JButton("Send");
		_Message = new JTextField("Write A Message to send to Chat");
		_Chat = new JTextArea("Welcome To the server Current Players are: ");
		_ChatScrollPane = new JScrollPane(_Chat);
		_PlayerName = new JTextField(Controller.GetController().getClient().getLocalPlayer().getName());
		_PlayerNameLabel = new JLabel("Your Player Name (Change It):");
		
		_ChatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Set sizes
		_Left.setSize(50, 50);
		_Right.setSize(50, 50);
		_Create.setSize(200, 75);
		_Exit.setSize(50, 50);
		_Send.setSize(100, 30);
		_Message.setSize(500, 30);
		_Chat.setSize(600, 500);
		_ChatScrollPane.setSize(600, 500);
		_PlayerName.setSize(100, 30);
		_PlayerNameLabel.setSize(300, 30);
		
		//Set locations
		_Left.setLocation(15, 220);
		_Right.setLocation(520, 220);
		_Create.setLocation(200, 475);
		_Exit.setLocation(520, 498);
		_Send.setLocation(1380, 525);
		_Message.setLocation(880, 525);
		_Chat.setLocation(880, 15);
		_ChatScrollPane.setLocation(880, 15);
		_PlayerName.setLocation(200, 30);
		_PlayerNameLabel.setLocation(150, 5);
		
		//Set fonts
		_Left.setFont(new Font("Consolas",Font.BOLD, 14));
		_Right.setFont(new Font("Consolas",Font.BOLD, 14));
		_Create.setFont(new Font("Consolas",Font.BOLD, 12));
		_Exit.setFont(new Font("Consolas",Font.BOLD, 12));
		_Send.setFont(new Font("Consolas",Font.PLAIN, 12));
		_Message.setFont(new Font("Consolas",Font.PLAIN, 12));
		_Chat.setFont(new Font("Consolas",Font.PLAIN, 12));
		_ChatScrollPane.setFont(new Font("Consolas",Font.PLAIN, 12));
		_PlayerName.setFont(new Font("Consolas",Font.PLAIN, 12));
		_PlayerNameLabel.setFont(new Font("Consolas",Font.PLAIN, 12));
		
		//Set backgrounds
		_Left.setBackground(Color.WHITE);
		_Right.setBackground(Color.WHITE);
		_Create.setBackground(Color.WHITE);
		_Exit.setBackground(Color.WHITE);
		_Send.setBackground(Color.WHITE);
		_Message.setBackground(Color.WHITE);
		_Chat.setBackground(Color.WHITE);
		_ChatScrollPane.setBackground(Color.WHITE);
		_PlayerName.setBackground(Color.WHITE);
		
		//Add listeners
		_Left.addActionListener(this);
		_Right.addActionListener(this);
		_Create.addActionListener(this);
		_Exit.addActionListener(this);
		_Send.addActionListener(this);
		_PlayerName.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				//Call changed to stop repetition
				changedUpdate(e);
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				Controller.GetController().UpdatePlayer(_PlayerName.getText());
			}
		});

		//Autoscroll
		_ChatScrollPane.setAutoscrolls(true);
		_Chat.setAutoscrolls(true);
		
		
		//Setup chat
		_Chat.setEditable(false);
		_Chat.setLineWrap(true);
		_Message.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(_Message.getText().contains("Write A Message to send to Chat"))
					_Message.setText("");
			}

		});
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
            switch (temp.getText())
            {
                case "<":
                	System.out.println("<");
                	_ShownVehicles--;
                	DisplayVehicle();
                    break;
                case ">":
                	System.out.println(">");
                	_ShownVehicles++;
                	DisplayVehicle();
                    break;
                case "Start Game":
                	System.out.println("Start Game");
                	Controller.GetController().SendReady();
                	Controller.GetController().SendGameStart();
                	break;
                case "X":
                    Controller.GetController().ExitProgram();
                	System.out.println("X");
                    break;
                case "Ready":
                	Controller.GetController().SendReady();
                	System.out.println("Ready");
                	break;
                case "Send":
                	Controller.GetController().getClient().SendChatMessage(_Message.getText());
                	AppendChat(Controller.GetController().getClient().getLocalPlayer().getName() + ": " + _Message.getText());
                	_Message.setText("");
            }
        }
		
	}

	/**
	 * Stub override
	 */
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	/**
	 * Gets the mouse location according to the location of the centre of the vehicle selection 
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
        double dx = e.getX() - 300;
        double dy = e.getY() - 250;
        _imageAngleRad = Math.atan2(dy, dx);
        repaint();
	}
	
	/**
	 * Appends the message to the end of the chat
	 * @param toAppend The message to add to the chat
	 */
	public void AppendChat(String toAppend)
	{
		_Chat.append("\n" + toAppend);
		_Chat.setCaretPosition(_Chat.getDocument().getLength());
	}
	
	/**
	 * Updates the player list with the player
	 * @param p The player to update or add
	 */
	public void UpdateList(Player p)
	{
		boolean match = false;
		for (int i = 0; i < _PlayerListModel.size(); i++)
		{
			if (_PlayerListModel.get(i).getID() == p.getID())
			{
				_PlayerListModel.set(i, p);
				match = true;
			}
		}
		
		if(!match)
			_PlayerListModel.addElement(p);
	}
}
