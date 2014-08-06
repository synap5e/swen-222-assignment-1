package cluedo.game;

import java.util.List;

import cluedo.game.board.Accusation;
import cluedo.game.board.Card;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.game.board.Suggestion;
/**
 * 
 * @author Simon Pinfold
 *
 */
public abstract class Player implements GameListener{

	private Hand hand;
	private GameView gameView;

	public Player(Hand h, GameView view){
		hand = h;
		this.gameView = view;
	}
	
	public abstract Character pickCharacter(List<Character> possibleCharacters);

	public abstract Location getDestination(List<Location> possibleLocations);

	public abstract boolean hasSuggestion();

	public abstract Suggestion getSuggestion();

	public abstract boolean hasAccusation();

	public abstract Accusation getAccusation();
	
	

}
