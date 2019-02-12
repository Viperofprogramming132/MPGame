package com.Viper.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.Viper.Control.Controller;

@SuppressWarnings("serial")
public class MainMenu extends JPanel implements ActionListener{

	private JButton _Connect;
	private JButton _Create;
	private JButton _Exit;
	
	private JPanel _MenuContainer;
	
	public MainMenu()
	{
		this.setLayout(null);
		
		MakeMenuButtons();
		
		PopulateContainer();
		
		this.add(_MenuContainer);
		this.setVisible(true);
	}
	
	private void PopulateContainer() {
		_MenuContainer = new JPanel();
		
		_MenuContainer.setBounds(0, 0, 400, 300);
		
		_MenuContainer.setLayout(null);
		_MenuContainer.add(_Connect);
		_MenuContainer.add(_Create);
		_MenuContainer.add(_Exit);
		_MenuContainer.setVisible(true);
	}

	private void MakeMenuButtons() {
		_Connect = new JButton("Connect To Server");
		_Create = new JButton("Create Server");
		_Exit = new JButton("Exit");
		
		_Connect.setSize(300, 75);
		_Create.setSize(300, 75);
		_Exit.setSize(300, 75);
		
		_Connect.setLocation(50, 10);
		_Create.setLocation(50, 100);
		_Exit.setLocation(50, 190);
		
		_Connect.setFont(new Font("Consolas",Font.BOLD, 14));
		_Create.setFont(new Font("Consolas",Font.BOLD, 14));
		_Exit.setFont(new Font("Consolas",Font.BOLD, 14));
		
		_Connect.setBackground(Color.WHITE);
		_Create.setBackground(Color.WHITE);
		_Exit.setBackground(Color.WHITE);
		
		_Connect.addActionListener(this);
		_Create.addActionListener(this);
		_Exit.addActionListener(this);
	}

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
