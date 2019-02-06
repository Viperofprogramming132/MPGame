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

public class Server extends JPanel implements ActionListener{

	private final String MAPLOCATION = "src/imgs/maptextures/";
	
	private JButton _Left;
	private JButton _Right;
	private JButton _Create;
	private JButton _Exit;
	private JPanel _MenuContainer;
	
	private File[] _PossibleMaps;
	private Integer _ShownMap = 0;
	
	public Server()
	{
		this.setLayout(null);
		
	    PopulateMapSelector();
	    
	    DisplayMap();
		
		MakeButtons();
		
		PopulateContainer();
		
		this.add(_MenuContainer);
		this.setVisible(true);
	}
	
	private void PopulateMapSelector() {
		File test = new File(MAPLOCATION);
		
		for (int i = 0; i < test.list().length; i++)
		{
			System.out.println(test.list()[i]);
		}
		_PossibleMaps = test.listFiles();
	}
	
	private Image ReadImage(File path) throws IOException
	{
		BufferedImage img = ImageIO.read(path);
		Image i = new ImageIcon(img).getImage();
		return i;
	}
	
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
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		try {
			g2d.drawImage(ReadImage(_PossibleMaps[_ShownMap]), 92, 50, 400, 400, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	private void MakeButtons() {
		_Left = new JButton("<");
		_Right = new JButton(">");
		_Create = new JButton("Enter Server Lobby");
		_Exit = new JButton("X");
		
		_Left.setSize(50, 50);
		_Right.setSize(50, 50);
		_Create.setSize(200, 75);
		_Exit.setSize(50, 50);
		
		_Left.setLocation(15, 220);
		_Right.setLocation(520, 220);
		_Create.setLocation(200, 475);
		_Exit.setLocation(520, 498);
		
		_Left.setFont(new Font("Consolas",Font.BOLD, 14));
		_Right.setFont(new Font("Consolas",Font.BOLD, 14));
		_Create.setFont(new Font("Consolas",Font.BOLD, 12));
		_Exit.setFont(new Font("Consolas",Font.BOLD, 12));
		
		_Left.setBackground(Color.WHITE);
		_Right.setBackground(Color.WHITE);
		_Create.setBackground(Color.WHITE);
		_Exit.setBackground(Color.WHITE);
		
		_Left.addActionListener(this);
		_Right.addActionListener(this);
		_Create.addActionListener(this);
		_Exit.addActionListener(this);
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