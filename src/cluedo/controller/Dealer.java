package cluedo.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import util.json.JsonEntity;
import util.json.JsonList;
import util.json.JsonObject;
import util.json.JsonString;
import cluedo.model.Board;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
/** This class is responsible for creating an accusation and dealing the 
 * hands of the players in the game.
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

	/** Create a random accusation.
	 * 
	 * @return the accusation created
	 */
	public Accusation createAccusation() {
		return new Accusation(
			weapons.remove(random.nextInt(weapons.size())),
			characters.remove(random.nextInt(characters.size())),
			rooms.remove(random.nextInt(rooms.size()))
		);
	}

	/** Return a list of numberOfPlayers hands, randomly selected and equal
	 * size. The hands cannot contain the cards used in the accusation created.
	 * 
	 * @param numberOfPlayers the number of players, and therefore number of hands to create
	 * @return the list of hands
	 */
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
			hands.get(i++ % numberOfPlayers).addCard(deck.remove(random.nextInt(deck.size())));
		}
		
		return hands;
	}

}
