package cluedo.game.board.player;

import java.util.List;

import cluedo.game.board.CharacterToken;
import cluedo.game.board.Location;

// TODO is this board player strategy?
public interface BoardPlayer {

	public Location getDestination(List<Location> possibleLocations);

	public CharacterToken getToken();

	public boolean hasSuggestion();

	public Suggestion getSuggestion();

	public boolean hasAccusation();

	public Accusation getAccusation();
}
