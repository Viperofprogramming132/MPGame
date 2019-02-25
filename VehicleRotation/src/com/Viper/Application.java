package com.Viper;

import com.Viper.UI.UIControl;

/**
 * The Application class is the main entrance to the program and will open the main menu when started
 * @author Aidan
 *
 */
public class Application {
	
	/**
	 * The main to enter the program
	 * @param args
	 */
	public static void main(String[] args) {
		UIControl UI = new UIControl();
		UI.OpenScreen();
	}
}
