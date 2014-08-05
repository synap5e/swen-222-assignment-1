package cluedo.game.cards;

import java.util.ArrayList;
import java.util.List;

import cluedo.util.json.JsonEntity;
import cluedo.util.json.JsonList;
import cluedo.util.json.JsonObject;
import cluedo.util.json.JsonString;
/**
 * 
 * @author Simon Pinfold
 *
 */
public class Dealer {

	private List<WeaponCard> weapons;
	private List<CharacterCard> characters;
	private List<RoomCard> rooms;
	
	public Dealer(JsonObject cardsDefs){
		weapons = new ArrayList<WeaponCard>();
		for (JsonEntity jse : ((JsonList)cardsDefs.get("weapons"))){
			weapons.add(new WeaponCard(((JsonString)jse).value()));
		}
		
		characters = new ArrayList<CharacterCard>();
		for (JsonEntity jse : ((JsonList)cardsDefs.get("people"))){
			characters.add(new CharacterCard(((JsonString)jse).value()));
		}
		
		rooms = new ArrayList<RoomCard>();
		for (JsonEntity jse : ((JsonList)cardsDefs.get("rooms"))){
			rooms.add(new RoomCard(((JsonString)jse).value()));
		}
		
	}

	public Accusation createAccusation() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Hand> dealHands(int numberOfPlayers) {
		// TODO Auto-generated method stub
		return null;
	}

}
