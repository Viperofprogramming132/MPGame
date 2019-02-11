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
import javax.swing.text.DefaultCaret;

import com.Viper.Control.Controller;
import com.Viper.Control.Player;

import javafx.scene.control.ListView;

public class Lobby extends JPanel implements ActionListener, MouseMotionListener{
	
	private final String VEHICLELOCATION = "src/imgs/vehicles/";
	
	private JButton _Left;
	private JButton _Right;
	private JButton _Create;
	private JButton _Exit;
	private JPanel _MenuContainer;
	private JList<Player> _PlayerView;
	private JTextArea _Chat;
	private JTextField _Message;
	private JButton _Send;
	private JScrollPane _ChatScrollPane;
	private JTextField _PlayerName;
	private JLabel _PlayerNameLabel;
	
	private File[] _PossibleVehicles;
	private Integer _ShownVehicles = 0;
	private final boolean _Host;
	
	private double _imageAngleRad = 0;
	
	private Image _backgroundImage;
	
	
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

	private void PopulatePlayerView()
	{
		if(_PlayerView == null)
			_PlayerView = new JList(Controller.GetController().getObPlayers().toArray());
		
		
	}
	
	private void getBackgroundMap(int map)
	{
		File files = new File("src/imgs/maptextures/");
		
		try {
			_backgroundImage = ReadImage(files.listFiles()[map]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void PopulateVehicleSelector() {
		File test = new File(VEHICLELOCATION);
		
		for (int i = 0; i < test.list().length; i++)
		{
			System.out.println(test.list()[i]);
		}
		_PossibleVehicles = test.listFiles();
	}
	
	private Image ReadImage(File path) throws IOException
	{
		BufferedImage img = ImageIO.read(path);
		Image i = new ImageIcon(img).getImage();
		return i;
	}
	
	private void DisplayVehicle()
	{
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
		
		_Left.setSize(50, 50);
		_Right.setSize(50, 50);
		_Create.setSize(200, 75);
		_Exit.setSize(50, 50);
		_Send.setSize(100, 30);
		_Message.setSize(500, 30);
		_Chat.setSize(600, 500);
		_ChatScrollPane.setSize(600, 500);
		_PlayerName.setSize(100, 30);
		
		_Left.setLocation(15, 220);
		_Right.setLocation(520, 220);
		_Create.setLocation(200, 475);
		_Exit.setLocation(520, 498);
		_Send.setLocation(1380, 525);
		_Message.setLocation(880, 525);
		_Chat.setLocation(880, 15);
		_ChatScrollPane.setLocation(880, 15);
		_PlayerName.setLocation(200, 15);
		
		_Left.setFont(new Font("Consolas",Font.BOLD, 14));
		_Right.setFont(new Font("Consolas",Font.BOLD, 14));
		_Create.setFont(new Font("Consolas",Font.BOLD, 12));
		_Exit.setFont(new Font("Consolas",Font.BOLD, 12));
		_Send.setFont(new Font("Consolas",Font.PLAIN, 12));
		_Message.setFont(new Font("Consolas",Font.PLAIN, 12));
		_Chat.setFont(new Font("Consolas",Font.PLAIN, 12));
		_ChatScrollPane.setFont(new Font("Consolas",Font.PLAIN, 12));
		_PlayerName.setFont(new Font("Consolas",Font.PLAIN, 12));
		
		_Left.setBackground(Color.WHITE);
		_Right.setBackground(Color.WHITE);
		_Create.setBackground(Color.WHITE);
		_Exit.setBackground(Color.WHITE);
		_Send.setBackground(Color.WHITE);
		_Message.setBackground(Color.WHITE);
		_Chat.setBackground(Color.WHITE);
		_ChatScrollPane.setBackground(Color.WHITE);
		_PlayerName.setBackground(Color.WHITE);
		
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

		_ChatScrollPane.setAutoscrolls(true);
		_Chat.setAutoscrolls(true);
		
		
		
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

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
        double dx = e.getX() - 300;
        double dy = e.getY() - 250;
        _imageAngleRad = Math.atan2(dy, dx);
        repaint();
		
	}
	
	public void AppendChat(String toAppend)
	{
		_Chat.append("\n" + toAppend);
		_Chat.setCaretPosition(_Chat.getDocument().getLength());
	}
}
