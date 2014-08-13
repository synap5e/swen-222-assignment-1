package cluedo.controller.player;

import java.util.ArrayList;
import java.util.List;

import cluedo.model.Location;
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
public abstract class Player{

	public enum PlayerType { LocalHuman, RemoteHuman, LocalAI };


	protected Hand hand;
	private String name;
	protected Character character;

	public Player(String name, Hand h, Character c){
		hand = h;
		this.name = name;
		this.character = c;
	}

	public String getName(){
		return name;
	}

	public abstract Location getDestination(List<Location> possibleLocations);

	public abstract boolean hasSuggestion();

	public abstract Suggestion getSuggestion();

	public abstract boolean hasAccusation();

	public abstract Accusation getAccusation();

	public boolean canDisprove(Character character, Weapon weapon, Room room) {
		return hand.hasCard(character) || hand.hasCard(weapon) || hand.hasCard(room);
	}

	public Card selectDisprovingCard(Card... cards) {
		assert cards.length > 0;
		List<Card> possibleShow = new ArrayList<Card>();
		for (Card c : cards){
			if (hand.hasCard(c)){
				possibleShow.add(c);
			}
		}
		assert possibleShow.size() > 0;
		return selectDisprovingCardToShow(character, possibleShow);
	}

	protected abstract Card selectDisprovingCardToShow(Character character, List<Card> possibleShow);

	public abstract void suggestionDisproved(Suggestion suggestion, Character character, Card disprovingCard);

	public abstract void waitForDiceRollOK();


}
