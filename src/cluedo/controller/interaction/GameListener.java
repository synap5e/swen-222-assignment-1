package cluedo.controller.interaction;

import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Location;
import cluedo.model.card.Character;
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
	
	public void onTurnBegin(String name, Character playersCharacter);

	
	
	public void onSuggestionUndisputed(Character suggester, Suggestion suggestion);
	
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Character disprover);
	
	public void onAccusation(Character accuser, Accusation accusation, boolean correct);

	
	public void onWeaponMove(Weapon weapon, Location room);

	/** Called when a character is moved to a room because they were suggested as murder in that room
	 * 
	 * @param character
	 * @param room
	 */
	public void onCharacterMove(Character character, Location room);

	public void onDiceRolled(int roll);


	public void onGameWon(String name, Character playersCharacter);

}
