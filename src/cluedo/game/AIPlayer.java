package cluedo.game;

import java.util.List;

import cluedo.game.board.Accusation;
import cluedo.game.board.Card;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.game.board.Suggestion;
import cluedo.game.board.Weapon;

public class AIPlayer extends Player implements GameListener{

	private GameView gameView;

	public AIPlayer(Hand h, GameView gameView) {
		super("HAL9000", h);
		this.gameView = gameView;
	}

	@Override
	public void onCharacterJoinedGame(String playerName, Character character,
			boolean humanPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTurnBegin(String name, Character playersCharacter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuggestionUndisputed(Character suggester,
			Suggestion suggestion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuggestionDisproved(Character suggester,
			Suggestion suggestion, Character disprover) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccusation(Character accuser, Accusation accusation,
			boolean correct) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWeaponMove(Weapon weapon, Location room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCharacterMove(Character character, Location room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDiceRolled(int roll) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameWon(String name, Character playersCharacter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Location getDestination(List<Location> possibleLocations) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSuggestion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Suggestion getSuggestion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAccusation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Accusation getAccusation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Card selectDisprovingCardToShow(Character character,
			List<Card> possibleShow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void suggestionDisproved(Suggestion suggestion, Character character,
			Card disprovingCard) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void waitForDiceRollOK() {
		// TODO Auto-generated method stub
		
	}

	
}
