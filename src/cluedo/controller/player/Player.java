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
/** A player of the game Cluedo. Server side representation.
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

	/** Get the player's desired destination from a list of possible destinations
	 * 
	 * @param possibleLocations the destinations that the player may move to
	 * @return the destination that the player wishes to move to
	 */
	public abstract Location getDestination(List<Location> possibleLocations);

	/** 
	 * @return whether the player has a suggestion they want to make
	 */
	public abstract boolean hasSuggestion();

	/**
	 * @return the players suggestion. The room the player currently resides in 
	 * will be used as the room for this suggestion.
	 */
	public abstract Suggestion getSuggestion();

	/** 
	 * @return whether the player has an accusation they want to make
	 */
	public abstract boolean hasAccusation();

	/**
	 * @return the players accusation
	 */
	public abstract Accusation getAccusation();

	/** Whether the player can disprove a suggestion
	 * 
	 * @param character the character suggested
	 * @param weapon the weapon suggested
	 * @param room the room suggested
	 * @return whether the player can disprove a suggestion
	 */
	public final boolean canDisprove(Character character, Weapon weapon, Room room) {
		return hand.hasCard(character) || hand.hasCard(weapon) || hand.hasCard(room);
	}

	/** Select which card of a suggestion the player wishes to show to 
	 * disprove that suggestion.
	 * They must hold the card that they are disproving with
	 * 
	 * @param cards the cards of the suggestion
	 * @return the card they are disproving with
	 */
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

	@Override
	public String toString() {
		return name + " (" + character + ")";
	}
	
	protected abstract Card selectDisprovingCardToShow(Character character, List<Card> possibleShow);

	public abstract void suggestionDisproved(Suggestion suggestion, Character character, Card disprovingCard);

	public abstract void waitForDiceRollOK();


}
