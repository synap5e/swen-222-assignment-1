package cluedo.controller.interaction;

import java.util.List;

import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/** This interface defines the methods used by the GameMaster to get input from 
 * whatever is providing input.
 *
 * Note that the implmentations of these methods may require code that blocks 
 * until all of the required input has been provided.
 * 
 * @author Simon Pinfold
 *
 */
public interface GameInput {

	/** Get the number of players for a game
	 * 
	 * @return the number of players
	 */
	public int getNumberOfPlayers(int min, int max);
	
	/** Get a list of the names of all the human players playing a game
	 *  
	 * @return the list of names
	 */
	public List<String> getHumanNames();
	
	/** Ask a (human) player to pick a character
	 * 
	 * @param playerName the name of the player who must pick
	 * @param allCharacters the list of all characters (picked and unpicked)
	 * @param availableCharacters the list of characters that are avaliable (unpicked)
	 * @return
	 */
	public Character chooseCharacter(String playerName, List<Character> allCharacters, List<Character> availableCharacters);
	
	
	
	/** Signify that the current player's turn is starting. Current player is 
	 * derived from the event fired to the GameListeners that a player's turn
	 * is starting.
	 *  Returning from this method signifies the user is has rolled the dice, 
	 *  and will result in the event being fired giving the result of the dice rolls
	 * 
	 * @param hand the hand of the current player
	 */
	public void startTurn(Hand hand);
	
	/** Get the current player's desired destination from a list of possible destinations
	 * 
	 * @param possibleLocations the destinations that a user may move to
	 * @return the destination that the user wishes to move to
	 */
	public Location getDestination(List<Location> possibleLocations);

	/** 
	 * @return whether the current player has a suggestion they want to make
	 */
	public boolean hasSuggestion();
	
	/**
	 * @return the weapon that the current player has picked
	 */
	public Weapon pickWeapon();
	
	/**
	 * @return the chracter that the current player has picked
	 */
	public Character pickCharacter();
	
	/** 
	 * @return whether the current player has an accusation they want to make
	 */
	public boolean hasAccusation();
	
	public Room pickRoom();


	/** Select which card the player wants to show to suggester 
	 *
	 * @param character
	 * @param suggester the character who made the suggestion (i.e. the one you must show the card to)
	 * @param possibleShow the list of cards that must show one of to character
	 * @return the card the player want to show
	 */
	public Card selectDisprovingCardToShow(Character character,	Character suggester, List<Card> possibleShow);

	/** Alert the current player only that their suggestion was disproved, and how it was disproved
	 *
	 * @param suggestion the suggestion <i>you</i> made
	 * @param characterDisproved the character that disproved your suggestion
	 * @param disprovingCard the card they disproved your suggestion with
	 */
	public void suggestionDisproved(Character characterDisproved, Card disprovingCard);

	public int getNetworkPlayerCount();

	/** Get the name of the client player (networked play only)
	 * 
	 * @return the name of the player
	 */
	public String getSingleName();


}
