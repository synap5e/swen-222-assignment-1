package cluedo.controller.network;

import java.util.List;

import util.json.JsonEntity;
import util.json.JsonList;
import util.json.JsonObject;
import util.json.JsonString;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Card;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/** This class converts from model object to a json representation
 * 
 * @author Simon Pinfold
 *
 */
public class ModelToJson {

	/** Convert a location to a json representation of it
	 * 
	 * @param location the location
	 * @return a json representation of the location
	 */
	public static JsonEntity locationToJson(Location location) {
		JsonObject loc = new JsonObject();
		if (location instanceof Room){
			loc.put("type", "room");
			loc.put("name", ((Room)location).getName());
		} else {
			loc.put("type", "tile");
			JsonList coord = new JsonList();
			coord.append(((Tile)location).getX());
			coord.append(((Tile)location).getY());
			loc.put("location", coord);
		}
		return loc;
	}

	/** Convert a card to a json representation of it
	 * 
	 * @param c the card
	 * @return a json representation of the card
	 */
	public static JsonEntity cardToJson(Card c) {
		return new JsonString(c.getName());
	}
	
	/** Convert a suggestion to a json representation of it
	 * 
	 * @param suggestion the suggestion
	 * @return a json representation of the suggestion
	 */
	public static JsonEntity suggestionToJson(Suggestion suggestion) {
		JsonObject o = new JsonObject();
		o.put("weapon", cardToJson(suggestion.getWeapon()));
		o.put("character", cardToJson(suggestion.getCharacter()));
		return o;
	}
	
	/** Convert an accusation to a json representation of it
	 * 
	 * @param accusation the accusation
	 * @return a json representation of the accusation
	 */
	public static JsonEntity accusationToJson(Accusation accusation) {
		JsonObject o = new JsonObject();
		o.put("weapon", cardToJson(accusation.getWeapon()));
		o.put("character", cardToJson(accusation.getCharacter()));
		o.put("room", cardToJson(accusation.getRoom()));
		return o;
	}
	
	/** Convert a hand to a json representation of it
	 * 
	 * @param hand the hand
	 * @return a json representation of the hand
	 */
	public static JsonEntity handToJson(Hand hand) {
		JsonList jsonhand = new JsonList();
		for (Card c : hand){
			jsonhand.append(ModelToJson.cardToJson(c));
		}
		return jsonhand;
	}

	/** Convert a list of cards to a json list of representations of those cards
	 * 
	 * @param cards the list of cards
	 * @return a json list of representations of those cards
	 */
	public static <T extends Card> JsonEntity cardsToJson(List<T> cards) {
		JsonList r = new JsonList();
		for (T c : cards){
			r.append(cardToJson(c));
		}
		return r;
	}

	/** Convert a list of locations to a json list of representations of those locations
	 * 
	 * @param locations the list of locations
	 * @return a json list of representations of those locations
	 */
	public static JsonEntity locationsToJson(List<Location> locations) {
		JsonList r = new JsonList();
		for (Location l : locations){
			r.append(locationToJson(l));
		}
		return r;
	}

	/** Create a json object representing the locations of all weapons on a board (i.e. a mapping)
	 * 
	 * @param board the board
	 * @return a json representation of all the locations of the weapons
	 */
	public static JsonEntity weaponLocationsToJson(Board board) {
		JsonObject jo = new JsonObject();
		for (Weapon w : board.getWeapons()){
			jo.put(w.getName(), ((Room) board.getLocationOf(w)).getName());
		}
		return jo;
	}

}
