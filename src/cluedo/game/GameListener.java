package cluedo.game;

import cluedo.game.board.Accusation;
import cluedo.game.board.Character;
import cluedo.game.board.Location;
import cluedo.game.board.Suggestion;

public interface GameListener {
	
	public void onPlayerMove(Character player, int roll, Location destination);
	
	public void onSuggestionUndisputed(Character suggester, Suggestion suggestion);
	
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Character disprover);
	
	public void onAccusation(Character accuser, Accusation accusation, boolean correct);

}
