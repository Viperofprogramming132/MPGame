package com.Viper;

import com.Viper.Control.Controller;

public class Application {

	public static void main(String[] args) {
		Controller c = Controller.GetController();
		c.StartApp();
	}

}
