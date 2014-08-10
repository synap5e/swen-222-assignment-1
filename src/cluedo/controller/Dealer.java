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
			hands.get(i++ % numberOfPlayers).addCard(deck.remove(random.nextInt(deck.size())));
		}
		
		return hands;
	}

}
