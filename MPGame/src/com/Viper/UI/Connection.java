package com.Viper.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.Viper.Control.Controller;

@SuppressWarnings("serial")
public class Connection extends JPanel implements ActionListener{
	
	private JTextField _ServerIP;
	private JLabel _ErrorMessage;
	private JLabel _ServerIPTile;
	private JLabel _TitleLabel;
	private JPanel _Container;
	private JButton _Connect;
	
	public Connection(boolean autoConnect) {
        setLayout(null);
        
        MakeContent();
        PopulateContainer();
                
        this.add(_Container);
        
        if (autoConnect)
        {
        	Connect();
        }
	}
	
	private void PopulateContainer() {
		_Container = new JPanel();
		
		_Container.setBounds(0, 0, 300, 200);
		
		_Container.setLayout(null);
		_Container.add(_TitleLabel);
		_Container.add(_ServerIP);
		_Container.add(_ServerIPTile);
		_Container.add(_Connect);
		_Container.add(_ErrorMessage);
		_Container.setVisible(true);
		_Container.setOpaque(false);
	}
	
	private void MakeContent() {
		_ServerIP = new JTextField("127.0.0.1");
		_TitleLabel = new JLabel("Connection Settings");
		_Connect = new JButton("Connect");		
		
        //Main title settings
        _TitleLabel.setLocation(0, 5);
        _TitleLabel.setSize(300, 20);
        _TitleLabel.setFont(new Font("Consolas",Font.BOLD , 18));
        _TitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        _TitleLabel.setOpaque(true);


        //Server IP title settings
        _ServerIPTile = new JLabel("Server Address");
        _ServerIPTile.setLocation(10, 35);
        _ServerIPTile.setSize(180, 20);
        _ServerIPTile.setFont(new Font("Consolas",Font.BOLD , 14));

        //Server IP text settings
        _ServerIP.setLocation(10, 55);
        _ServerIP.setSize(180, 20);
        _ServerIP.setEditable(true);
        _ServerIP.setBackground(new Color(225, 225, 225));
        _ServerIP.setFont(new Font("Consolas",Font.BOLD , 14));
        
        //Connect Button
        _Connect.setSize(200, 50);
		_Connect.setLocation(10, 100);
		_Connect.setFont(new Font("Consolas",Font.BOLD, 14));
		_Connect.setBackground(Color.WHITE);
		_Connect.addActionListener(this);
		
		//Error Label
		_ErrorMessage = new JLabel();
		_ErrorMessage.setLocation(10, 160);
		_ErrorMessage.setSize(290, 20);
		_ErrorMessage.setFont(new Font("Consolas",Font.PLAIN , 14));
		_ErrorMessage.setForeground(Color.RED);
		
		_ServerIP.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(_ServerIP.getText().contains("127.0.0.1"))
					_ServerIP.setText("");
			}

		});
        
        
        //Adding components to the panel
        setLocation(0, 0);
        setVisible(true);
	}
	
	private void Connect()
	{
		if(Controller.GetController().ConnectToServer(_ServerIP.getText()))
			Controller.GetController().EnterLobby();
		else
			_ErrorMessage.setText("Error: Unable to Connect to server");
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
                case "Connect":
                    Connect();
                	System.out.println("CONNECT");
                    break;
            }
        }
		
	}
}
