package cluedo.controller.interaction;

import java.util.List;

import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/** THIS IS BLOCKING!!!!!1111ONE
 * 
 * @author Simon Pinfold
 *
 */
public interface GameInput {

	public int getNumberOfPlayers(int min, int max);
	
	public List<String> getHumanNames();
	
	public Character chooseCharacter(String playerName, List<Character> allCharacters, List<Character> availableCharacters);
	
	
	
	/** NOTE: returning rolls the dice - return after the user has selected to roll the dice
	 * 
	 * @param hand
	 */
	public void startTurn(Hand hand);
	
	
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

	public int getNetworkPlayerCount();

	/** Used for network play
	 * 
	 * @return
	 */
	public String getSingleName();


}
