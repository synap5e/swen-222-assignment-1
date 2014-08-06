package cluedo.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cluedo.game.board.Accusation;
import cluedo.game.board.Board;
import cluedo.game.board.Card;
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
	private Random random = new Random();
	
	public Dealer(Board board){
		weapons = new ArrayList<Weapon>(board.getWeapons());
		characters = new ArrayList<Character>(board.getCharacters());
		rooms = new ArrayList<Room>(board.getRooms());
	}

	public Accusation createAccusation() {
		return new Accusation(
			weapons.remove(random.nextInt(weapons.size())),
			characters.remove(random.nextInt(characters.size())),
			rooms.remove(random.nextInt(rooms.size()))
		);
	}

	public List<Hand> dealHands(int numberOfPlayers) {
		List<Hand> hands = new ArrayList<Hand>();
		for (int i =0; i<numberOfPlayers;i++){
			hands.add(new Hand());
		}
		
		List<Card> deck = new ArrayList<Card>();
		deck.addAll(weapons);
		deck.addAll(characters);
		deck.addAll(rooms);
		
		int i=0;
		while(!deck.isEmpty()){
			hands.get(i++ % numberOfPlayers).addCard(deck.remove(deck.size()));
		}
		
		return hands;
	}

}
