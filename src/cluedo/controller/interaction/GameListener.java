package cluedo.controller.interaction;

import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Location;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Suggestion;

/**
 *
 * @author Simon Pinfold
 *
 */
public interface GameListener {

	/** Used to announce what characters are in play, and their location
	 *
	 * @param playerNumber the player's name
	 * @param character the character being played as for this player number
	 * @param humanPlayer if this is a human player
	 */
	public void onCharacterJoinedGame(String playerName, Character character, PlayerType playerType);

	/** Called when a player's turn begins 
	 * 
	 * @param name the name of the player who's turn just began
	 * @param playersCharacter the character of the player who's turn just began
	 */
	public void onTurnBegin(String name, Character playersCharacter);


	/** Called when a suggestion has been made, and has not been disputed by all
	 * other players (this does not include the suggester)
	 * 
	 * @param suggester the character who made the suggestion
	 * @param suggestion the suggestion made
	 * @param room the room the suggestion was made in
	 */
	public void onSuggestionUndisputed(Character suggester, Suggestion suggestion, Room room);
	
	/** Called when a suggestion has been made and has been disputed by some other player
	 * 
	 * @param suggester the character who made the suggestion
	 * @param suggestion the suggestion made
	 * @param room the room the suggestion was made in
	 * @param disprover the character who disproved the suggestion
	 */
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Room room, Character disprover);

	/** Called when an accusation has been made
	 * 
	 * @param accuser the accuser
	 * @param accusation the accusation
	 * @param correct if the accusation was correct
	 */
	public void onAccusation(Character accuser, Accusation accusation, boolean correct);

	/** Called when a weapon moved to a new location
	 * 
	 * @param weapon the weapon
	 * @param newLocation the new location
	 */
	public void onWeaponMove(Weapon weapon, Location newLocation);

	/** Called when a character is moved to a new location
	 *
	 * @param character
	 * @param newLocation
	 */
	public void onCharacterMove(Character character, Location newLocation);

	/** Called when the dice are rolled
	 * 
	 * @param dice1
	 * @param dice2
	 */
	public void onDiceRolled(int dice1, int dice2);

	/** Called when a character wins the game
	 * 
	 * @param name the name of the player who has won
	 * @param playersCharacter the character of the player who has won
	 */
	public void onGameWon(String name, Character playersCharacter);

	/** Called to inform how many network players are still being waited for.
	 * Will be called each time this changes, but not on 0
	 *
	 * @param count the number of network players still being waited for
	 */
	public void waitingForNetworkPlayers(int count);

	/** Called when a player has lost the game
	 * 
	 * @param name the name of the player who lost
	 * @param playersCharacter the character of the player who has lost
	 */
	public void onLostGame(String name, Character playersCharacter);

	/** Called when a suggestion has been made, but no action has been taken yet - 
	 * i.e. no tokens have been moved.
	 * 
	 * @param suggesterPlayerName the name of the player making the suggestion
	 * @param suggester the suggester
	 * @param suggestion the suggestion
	 * @param room the room the suggestion was made in
	 */
	public void onSuggestion(String suggesterPlayerName, Character suggester, Suggestion suggestion, Room room);

}
