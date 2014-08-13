package cluedo.controller.player;

import java.util.List;

import cluedo.controller.interaction.GameListener;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/**
 *
 * @author Simon Pinfold
 *
 */
public class AIPlayer extends Player implements GameListener{

	private GameStateFacade gameView;

	public AIPlayer(Hand h, Character c, GameStateFacade gameView) {
		super("HAL9000", h, c);
		this.gameView = gameView;
	}

	@Override
	public void onCharacterJoinedGame(String playerName, Character character,
			PlayerType type) {
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
	public void onDiceRolled(int dice1, int dice2) {
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

	@Override
	public void waitingForNetworkPlayers(int i) {
		// TODO Auto-generated method stub

	}


}
