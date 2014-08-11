package cluedo.controller.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import util.json.JsonObject;
import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

public class NetworkGameChannel implements GameInput, GameListener {

	private InputStream is;
	private OutputStream os;

	public NetworkGameChannel(OutputStream outputStream, InputStream inputStream) {
		this.os = outputStream;
		this.is = inputStream;
	}
	
	@Override
	public synchronized int getNumberOfPlayers(int min, int max) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized List<String> getHumanNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized Character chooseCharacter(String playerName,
			List<Character> characters, List<Character> availableCharacters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void startTurn(Hand h) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized Location getDestination(List<Location> possibleLocations) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized boolean hasSuggestion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized Weapon pickWeapon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized Character pickCharacter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized boolean hasAccusation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized Room pickRoom() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized Card selectDisprovingCardToShow(Character character,
			List<Card> possibleShow) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void suggestionDisproved(Character characterDisproved,
			Card disprovingCard) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized int getNetworkPlayerCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public synchronized String getSingleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void onCharacterJoinedGame(String playerName, Character character,
			PlayerType playerType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onTurnBegin(String name, Character playersCharacter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onSuggestionUndisputed(Character suggester,
			Suggestion suggestion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onSuggestionDisproved(Character suggester,
			Suggestion suggestion, Character disprover) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onAccusation(Character accuser, Accusation accusation,
			boolean correct) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onWeaponMove(Weapon weapon, Location room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onCharacterMove(Character character, Location room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onDiceRolled(int roll) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void onGameWon(String name, Character playersCharacter) {
		// TODO Auto-generated method stub
		
	}

	public synchronized void sendHand(Hand hand) {
		// TODO Auto-generated method stub
		
	}

}
