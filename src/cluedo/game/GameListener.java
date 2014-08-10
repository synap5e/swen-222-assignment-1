package cluedo.game;

import cluedo.game.board.Accusation;
import cluedo.game.board.Character;
import cluedo.game.board.Location;
import cluedo.game.board.Suggestion;
import cluedo.game.board.Weapon;

public interface GameListener {
	
	/** Used to announce what characters are in play, and their location
	 * 
	 * @param playerNumber the number of the player. <b>1 based</b>
	 * @param character the character being played as for this player number
	 * @param humanPlayer if this is a human player
	 */
	public void onCharacterJoinedGame(int playerNumber, Character character, boolean humanPlayer);
	
	
	public void onPlayerTurn(Character player);
	
	public void onCharacterMove(Character player, int roll, Location destination);
	
	public void onSuggestionUndisputed(Character suggester, Suggestion suggestion);
	
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Character disprover);
	
	public void onAccusation(Character accuser, Accusation accusation, boolean correct);

	public void onTurnBegin(String name, Character playersCharacter);

	public void onWeaponMove(Weapon weapon, Location room);

	/** Called when a character is moved to a room because they were suggested as murder in that room
	 * 
	 * @param character
	 * @param room
	 */
	public void onCharacterMove(Character character, Location room);

	public void onDiceRolled(int roll);


	public void onCharacterJoinedGame(String name, Character character, Boolean humanPlayer);


	public void onGameWon(String name, Character playersCharacter);

}
