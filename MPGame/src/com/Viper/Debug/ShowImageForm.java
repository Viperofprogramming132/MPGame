package com.Viper.Debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ShowImageForm {
	
	JFrame frame = new JFrame();
	
	JPanel panel;
	
	JLabel label;
	
	Shape s;
	
	public ShowImageForm(ImageIcon i)
	{
		panel = new JPanel();
		frame.setLocationRelativeTo(null);
		
		label = new JLabel(i);
		
		frame.setSize(i.getIconWidth() + 100, i.getIconHeight() + 100);
		panel.setSize(i.getIconWidth(), i.getIconHeight());
		
		frame.getContentPane().add(panel);
		panel.add(label);
		
		label.setSize(i.getIconWidth(), i.getIconHeight());
		label.setOpaque(false);
		
		label.setVisible(true);
		panel.setVisible(true);
		frame.setVisible(true);
	}
	
	public void updateLabel(ImageIcon i)
	{
		label.setIcon(i);
		label.repaint();
	}
	
	@SuppressWarnings("serial")
	public ShowImageForm(GeneralPath i)
	{
		panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D) g;
				
				g2d.setPaint(Color.BLUE);
				
				g2d.fill(i);
			}
		};
		frame.setLocationRelativeTo(null);
		
		frame.setSize(1000, 1000);
		panel.setSize(1000, 1000);
		
		frame.getContentPane().add(panel);
		
		panel.setVisible(true);
		frame.setVisible(true);
	}
	
	@SuppressWarnings("serial")
	public ShowImageForm(Shape i)
	{
		s = i;
		panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g)
			{
				Graphics2D g2d = (Graphics2D) g;
				
				g2d.setPaint(Color.BLUE);
				
				g2d.fill(s);
			}
		};
		frame.setLocationRelativeTo(null);
		
		frame.setSize(1000, 1000);
		panel.setSize(1000, 1000);
		
		frame.getContentPane().add(panel);
		
		panel.setVisible(true);
		frame.setVisible(true);
	}
	
	public void update(Shape s)
	{
		this.s = s;
	}

}
