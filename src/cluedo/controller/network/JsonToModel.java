package cluedo.controller.network;

import java.util.ArrayList;
import java.util.List;

import util.json.JsonEntity;
import util.json.JsonList;
import util.json.JsonNumber;
import util.json.JsonObject;
import util.json.JsonString;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Hand;

/**
 * 
 * @author Simon Pinfold
 *
 */
public class JsonToModel {
	
	private Board board;

	public JsonToModel(Board b) {
		this.board = b;
	}
	
	public Location jsonToLocation(JsonEntity json) {
		JsonObject jsonOb = (JsonObject) json;
		Location l;
		if (((JsonString) jsonOb.get("type")).value().equals("room")){
			String roomName = ((JsonString) jsonOb.get("name")).value();
			l = (Room) board.getCard(roomName);
		} else {
			JsonList loc = ((JsonList) jsonOb.get("location"));
			int x = (int) ((JsonNumber) loc.get(0)).value();
			int y = (int) ((JsonNumber) loc.get(1)).value();
			l = board.getLocation(x, y);
		}
		return l;
	}
	
	public List<Location> jsonToLocations(JsonEntity jsonEntity) {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (JsonEntity locEnt : (JsonList)jsonEntity){
			locations.add(jsonToLocation(locEnt));
		}
		return locations;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Card> T jsonToCard(JsonEntity jsonEntity) {
		return (T)board.getCard(((JsonString) jsonEntity).value());
	}

	public <T extends Card> List<T> jsonToCards(JsonEntity jsonEntity) {
		ArrayList<T> cards = new ArrayList<T>();
		for (JsonEntity cardEnt : (JsonList)jsonEntity){
			cards.add(this.<T>jsonToCard(cardEnt));
		}
		return cards;
	}

	public Hand jsonToHand(JsonEntity jsonEntity) {
		Hand h = new Hand();
		for (Card c : jsonToCards(jsonEntity)){
			h.addCard(c);
		}
		return h;
	}

}
