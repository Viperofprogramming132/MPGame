package com.Viper.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.Viper.Control.Controller;

/**
 * The main menu that the application is opened to this has the options to connect or host a game
 * @author Aidan
 *
 */
@SuppressWarnings("serial")
public class MainMenu extends JPanel implements ActionListener{

	/**
	 * The button that take the user to the connection screen
	 */
	private JButton _Connect;
	
	/**
	 * The button that takes the user to the Server screen
	 */
	private JButton _Create;
	
	/**
	 * The button to exit the program
	 */
	private JButton _Exit;
	
	/**
	 * The container that contains the components
	 */
	private JPanel _MenuContainer;
	
	/**
	 * Creates a main menu and displays it
	 */
	public MainMenu()
	{
		this.setLayout(null);
		
		MakeMenuButtons();
		
		PopulateContainer();
		
		this.add(_MenuContainer);
		this.setVisible(true);
	}
	
	/**
	 * Populates the container with all the components
	 */
	private void PopulateContainer() {
		_MenuContainer = new JPanel();
		
		_MenuContainer.setBounds(0, 0, 400, 300);
		
		_MenuContainer.setLayout(null);
		_MenuContainer.add(_Connect);
		_MenuContainer.add(_Create);
		_MenuContainer.add(_Exit);
		_MenuContainer.setVisible(true);
	}

	/**
	 * Makes the components to be displayed
	 */
	private void MakeMenuButtons() {
		_Connect = new JButton("Connect To Server");
		_Create = new JButton("Create Server");
		_Exit = new JButton("Exit");
		
		//Set size
		_Connect.setSize(300, 75);
		_Create.setSize(300, 75);
		_Exit.setSize(300, 75);
		
		//Set location
		_Connect.setLocation(50, 10);
		_Create.setLocation(50, 100);
		_Exit.setLocation(50, 190);
		
		//Set font
		_Connect.setFont(new Font("Consolas",Font.BOLD, 14));
		_Create.setFont(new Font("Consolas",Font.BOLD, 14));
		_Exit.setFont(new Font("Consolas",Font.BOLD, 14));
		
		//Set background
		_Connect.setBackground(Color.WHITE);
		_Create.setBackground(Color.WHITE);
		_Exit.setBackground(Color.WHITE);
		
		//Add action listeners
		_Connect.addActionListener(this);
		_Create.addActionListener(this);
		_Exit.addActionListener(this);
	}

	/**
	 * The action listener to listen for button presses
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		System.out.println("Button Pressed?");
		if (e.getSource().getClass() == JButton.class )
        {
            //Casting to button
            JButton temp = (JButton)e.getSource();

            //Select the action based on button's text
            switch (temp.getText())
            {
                case "Connect To Server":
                    Controller.GetController().ConnectToGame(false);
                	System.out.println("CONNECT");
                    break;
                case "Create Server":
                    Controller.GetController().HostGame();
                	System.out.println("CREATE");
                    break;
                case "Exit":
                    Controller.GetController().ExitProgram();
                	System.out.println("EXIT");
                    break;
            }
        }
		
	}
	
}
