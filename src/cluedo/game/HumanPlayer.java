package cluedo.game;

import java.util.List;

import cluedo.game.board.Accusation;
import cluedo.game.board.Card;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.game.board.Suggestion;
import cluedo.game.board.Weapon;
import cluedo.gui.GUIHandle;

public class HumanPlayer extends Player {

	private GUIHandle guiHandle;

	public HumanPlayer(String name, Hand h, GUIHandle guiHandle) {
		super(name, h);
		this.guiHandle = guiHandle;
	}

	@Override
	public void waitForDiceRollOK() {
		guiHandle.startTurn(hand);
	}

	
	@Override
	public Location getDestination(List<Location> possibleLocations) {
		return guiHandle.getDestination(possibleLocations);
	}

	@Override
	public boolean hasSuggestion() {
		return guiHandle.hasSuggestion();
	}

	@Override
	public Suggestion getSuggestion() {
		return new Suggestion(guiHandle.pickWeapon(), guiHandle.pickCharacter());
	}

	@Override
	public boolean hasAccusation() {
		return guiHandle.hasAccusation();
	}

	@Override
	public Accusation getAccusation() {
		return new Accusation(guiHandle.pickWeapon(), guiHandle.pickCharacter(), guiHandle.pickRoom());
	}

	@Override
	protected Card selectDisprovingCardToShow(Character character, List<Card> possibleShow) {
		return guiHandle.selectDisprovingCardToShow(character, possibleShow);
	}

	@Override
	public void suggestionDisproved(Suggestion suggestion, Character characterDisproved, Card disprovingCard) {
		guiHandle.suggestionDisproved(characterDisproved, disprovingCard);
	}

	

}
