package cluedo.controller.player;

import java.util.List;

import cluedo.controller.interaction.GameInput;
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
public class HumanPlayer extends Player {

	private GameInput input;

	public HumanPlayer(String name, Hand h, GameInput input) {
		super(name, h);
		this.input = input;
	}

	@Override
	public void waitForDiceRollOK() {
		input.startTurn(hand);
	}

	
	@Override
	public Location getDestination(List<Location> possibleLocations) {
		return input.getDestination(possibleLocations);
	}

	@Override
	public boolean hasSuggestion() {
		return input.hasSuggestion();
	}

	@Override
	public Suggestion getSuggestion() {
		return new Suggestion(input.pickWeapon(), input.pickCharacter());
	}

	@Override
	public boolean hasAccusation() {
		return input.hasAccusation();
	}

	@Override
	public Accusation getAccusation() {
		return new Accusation(input.pickWeapon(), input.pickCharacter(), input.pickRoom());
	}

	@Override
	protected Card selectDisprovingCardToShow(Character character, List<Card> possibleShow) {
		return input.selectDisprovingCardToShow(character, possibleShow);
	}

	@Override
	public void suggestionDisproved(Suggestion suggestion, Character characterDisproved, Card disprovingCard) {
		input.suggestionDisproved(characterDisproved, disprovingCard);
	}

	

}
