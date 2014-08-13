package cluedo.controller.network;

import java.util.List;

import util.json.JsonEntity;
import util.json.JsonList;
import util.json.JsonNumber;
import util.json.JsonObject;
import util.json.JsonString;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/**
 * 
 * @author Simon Pinfold
 *
 */
public class ModelToJson {

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

	public static JsonEntity cardToJson(Card c) {
		return new JsonString(c.getName());
	}
	public static JsonEntity suggestionToJson(Suggestion suggestion) {
		JsonObject o = new JsonObject();
		o.put("weapon", cardToJson(suggestion.getWeapon()));
		o.put("character", cardToJson(suggestion.getCharacter()));
		return o;
	}
	
	public static JsonEntity accusationToJson(Accusation accusation) {
		JsonObject o = new JsonObject();
		o.put("weapon", cardToJson(accusation.getWeapon()));
		o.put("character", cardToJson(accusation.getCharacter()));
		o.put("room", cardToJson(accusation.getRoom()));
		return o;
	}
	
	public static JsonEntity handToJson(Hand hand) {
		JsonList jsonhand = new JsonList();
		for (Card c : hand){
			jsonhand.append(ModelToJson.cardToJson(c));
		}
		return jsonhand;
	}

	public static <T extends Card> JsonEntity cardsToJson(List<T> cards) {
		JsonList r = new JsonList();
		for (T c : cards){
			r.append(cardToJson(c));
		}
		return r;
	}

	public static JsonEntity locationsToJson(List<Location> locations) {
		JsonList r = new JsonList();
		for (Location l : locations){
			r.append(locationToJson(l));
		}
		return r;
	}

	public static JsonEntity weaponLocationsToJson(Board board) {
		JsonObject jo = new JsonObject();
		for (Weapon w : board.getWeapons()){
			jo.put(w.getName(), ((Room) board.getLocationOf(w)).getName());
		}
		return jo;
	}

}
