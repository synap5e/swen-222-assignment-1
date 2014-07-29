package cluedo.cards;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class Deck {

	private List<Weapon> weapons;

	public void loadCards() throws FileNotFoundException, IOException{
		Properties prop = new Properties();
		prop.load(new FileInputStream("./rules/cards.properties"));
	}

	public static void main(String[] args){

	}

}
