package com.Viper;

import com.Viper.Control.Controller;

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
		Controller c = Controller.GetController();
		c.StartApp();
	}

}
