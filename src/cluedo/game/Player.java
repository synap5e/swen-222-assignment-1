package cluedo.game;

import java.util.ArrayList;
import java.util.List;

import cluedo.game.board.Accusation;
import cluedo.game.board.Card;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Suggestion;
import cluedo.game.board.Weapon;
/**
 * 
 * @author Simon Pinfold
 *
 */
public abstract class Player{

	protected Hand hand;
	private String name;

	public Player(String name, Hand h){
		hand = h;
		this.name = name;
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

	public Card selectDisprovingCard(Character character, Card... cards) {
		List<Card> possibleShow = new ArrayList<Card>();
		for (Card c : cards){
			if (hand.hasCard(c)){
				possibleShow.add(c);
			}
		}
		return selectDisprovingCardToShow(character, possibleShow);
	}

	protected abstract Card selectDisprovingCardToShow(Character character, List<Card> possibleShow);

	public abstract void suggestionDisproved(Suggestion suggestion, Character character, Card disprovingCard);

	public abstract void waitForDiceRollOK();
	

}
