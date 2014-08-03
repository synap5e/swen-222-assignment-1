package cluedo.game.cards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import sun.org.mozilla.javascript.json.JsonParser;
import cluedo.util.json.JsonEntity;
import cluedo.util.json.JsonList;
import cluedo.util.json.JsonObject;
import cluedo.util.json.JsonParseException;
import cluedo.util.json.JsonString;
import cluedo.util.json.MinimalJson;

public class Deck {

	private List<WeaponCard> weapons;
	private List<CharacterCard> characters;
	private List<RoomCard> rooms;
	
	private static final String cardsPath = "./rules/cards.json";

	public void loadCards() throws FileNotFoundException, JsonParseException{
		JsonObject cardsDescription = MinimalJson.parseJson(new File(cardsPath));
	
		weapons = new ArrayList<WeaponCard>();
		for (JsonEntity jse : ((JsonList)cardsDescription.get("weapons"))){
			weapons.add(new WeaponCard(((JsonString)jse).value()));
		}
		
		characters = new ArrayList<CharacterCard>();
		for (JsonEntity jse : ((JsonList)cardsDescription.get("people"))){
			characters.add(new CharacterCard(((JsonString)jse).value()));
		}
		
		rooms = new ArrayList<RoomCard>();
		for (JsonEntity jse : ((JsonList)cardsDescription.get("rooms"))){
			rooms.add(new RoomCard(((JsonString)jse).value()));
		}
		
	}

	public static void main(String[] args) throws FileNotFoundException, JsonParseException{
		new Deck().loadCards();
	}

}
