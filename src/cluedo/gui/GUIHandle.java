package cluedo.gui;

import java.util.List;

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
public interface GUIHandle {

	public int getNumberOfPlayers(int min, int max);
	
	public List<String> getHumanNames();
	
	public Character chooseCharacter(String playerName, List<Character> characters, List<Character> availableCharacters);
	
	
	
	/** NOTE: returning rolls the dice - return after the user has selected to roll the dice
	 * 
	 * @param h
	 */
	public void startTurn(Hand h);
	
	
	public Location getDestination(List<Location> possibleLocations);

	
	public boolean hasSuggestion();
	
	public Weapon pickWeapon();
	
	public Character pickCharacter();
	
	
	public boolean hasAccusation();
	
	public Room pickRoom();
	
	
	/**
	 * 
	 * @param character the character who made the suggestion (i.e. the one you must show the card to)
	 * @param possibleShow the list of cards that must show one of to character
	 * @return the card you want to show
	 */
	public Card selectDisprovingCardToShow(Character character,	List<Card> possibleShow);

	/**
	 * 
	 * @param suggestion the suggestion <i>you</i> made
	 * @param characterDisproved the character that disproved your suggestion
	 * @param disprovingCard the card they disproved your suggestion with
	 */
	public void suggestionDisproved(Character characterDisproved, Card disprovingCard);


}
