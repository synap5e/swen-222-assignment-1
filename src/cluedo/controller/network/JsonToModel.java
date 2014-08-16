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
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/** This class provides conversions from Json to the model objects 
 * used by a specific board.
 * 
 * @author Simon Pinfold
 *
 */
public class JsonToModel {
	
	private Board board;

	/** Construct the converter. The cards from b will be used as the return values 
	 * from json.
	 * 
	 * @param b the board to use
	 */
	public JsonToModel(Board b) {
		this.board = b;
	}
	
	/** Convert a json string representing a location into a location model
	 * object from the JsonToModel's board.
	 * 
	 * @param json the json string
	 * @return the location represented by that string
	 */
	public Location jsonToLocation(JsonEntity json) {
		JsonObject jsonOb = (JsonObject) json;
		Location l;
		if (((JsonString) jsonOb.get("type")).value().equals("room")){ // { "type" : "room" ...
			// room locations use the name
			String roomName = ((JsonString) jsonOb.get("name")).value();
			l = (Room) board.getCard(roomName);
		} else { // { "type" : "tile" ...
			// tile locations specify the x,y
			JsonList loc = ((JsonList) jsonOb.get("location")); 
			int x = (int) ((JsonNumber) loc.get(0)).value();
			int y = (int) ((JsonNumber) loc.get(1)).value();
			l = board.getLocation(x, y);
		}
		return l;
	}
	
	/** Convert a json list of location representations to a list of locations.
	 * 
	 */
	public List<Location> jsonToLocations(JsonEntity jsonEntity) {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (JsonEntity locEnt : (JsonList)jsonEntity){
			locations.add(jsonToLocation(locEnt));
		}
		return locations;
	}
	
	/** Convert a json representation of a card to a card model from the 
	 * converters board
	 * 
	 * @param jsonEntity the representation of the card
	 * @return the card
	 */
	@SuppressWarnings("unchecked")
	public <T extends Card> T jsonToCard(JsonEntity jsonEntity) {
		return (T)board.getCard(((JsonString) jsonEntity).value());
	}

	/** Convert a json list of card representations to a list of cards objects
	 * 
	 * @param jsonEntity the list of card representations
	 * @return the list of card objects
	 */
	public <T extends Card> List<T> jsonToCards(JsonEntity jsonEntity) {
		ArrayList<T> cards = new ArrayList<T>();
		for (JsonEntity cardEnt : (JsonList)jsonEntity){
			cards.add(this.<T>jsonToCard(cardEnt));
		}
		return cards;
	}

	/** Convert a json representation of a hand to a hand model object from the 
	 * converters board
	 * 
	 * @param jsonEntity the representation of the hand
	 * @return the hand model object
	 */
	public Hand jsonToHand(JsonEntity jsonEntity) {
		Hand h = new Hand();
		for (Card c : jsonToCards(jsonEntity)){
			h.addCard(c);
		}
		return h;
	}

	/** Convert a json representation of a suggestion to a suggestion model object from the 
	 * converters board
	 * 
	 * @param jsonEntity the representation of the suggestion
	 * @return the hand suggestion object
	 */
	public Suggestion jsonToSuggestion(JsonEntity jsonEntity) {
		return new Suggestion(
				this.<Weapon>jsonToCard(((JsonObject) jsonEntity).get("weapon")), 
				this.<Character>jsonToCard(((JsonObject) jsonEntity).get("character"))
		);
	}

	/** Convert a json representation of an accusation to a suggestion model object from the 
	 * converters board
	 * 
	 * @param jsonEntity the representation of the accusation
	 * @return the hand accusation object
	 */
	public Accusation jsonToAccusation(JsonEntity jsonEntity) {
		return new Accusation(
				this.<Weapon>jsonToCard(((JsonObject) jsonEntity).get("weapon")), 
				this.<Character>jsonToCard(((JsonObject) jsonEntity).get("character")),
				this.<Room>jsonToCard(((JsonObject) jsonEntity).get("room"))
		);
	}

}
