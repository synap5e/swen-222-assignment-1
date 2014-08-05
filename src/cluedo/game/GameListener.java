package cluedo.game;

import cluedo.game.board.Location;
import cluedo.game.cards.Accusation;
import cluedo.game.cards.CharacterCard;
import cluedo.game.cards.Suggestion;

public interface GameListener {
	
	public void onPlayerMove(CharacterCard player, int roll, Location destination);
	
	public void onSuggestionUndisputed(CharacterCard suggester, Suggestion suggestion);
	
	public void onSuggestionDisproved(CharacterCard suggester, Suggestion suggestion, CharacterCard disprover);
	
	public void onAccusation(CharacterCard accuser, Accusation accusation, boolean correct);

}
