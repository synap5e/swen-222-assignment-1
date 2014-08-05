package cluedo.game;

import java.util.List;

import cluedo.game.board.Location;
import cluedo.game.cards.Accusation;
import cluedo.game.cards.Card;
import cluedo.game.cards.CharacterCard;
import cluedo.game.cards.Hand;
import cluedo.game.cards.Suggestion;
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
	
	public abstract CharacterCard pickCharacter(List<CharacterCard> possibleCharacters);

	public abstract Location getDestination(List<Location> possibleLocations);

	public abstract boolean hasSuggestion();

	public abstract Suggestion getSuggestion();

	public abstract boolean hasAccusation();

	public abstract Accusation getAccusation();
	
	

}
