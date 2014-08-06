package cluedo.game;

import java.util.ArrayList;
import java.util.List;

import cluedo.game.board.Accusation;
import cluedo.game.board.Board;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Room;
import cluedo.game.board.Weapon;
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

	private List<Weapon> weapons;
	private List<Character> characters;
	private List<Room> rooms;
	
	public Dealer(Board board){
		weapons = null;// TODO: board.getWeapons();
		
		characters = null;//TODO: board.getCharacters();
		
		rooms = new ArrayList<Room>(board.getRooms());
		
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
