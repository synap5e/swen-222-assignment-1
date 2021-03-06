package cluedo.controller.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import util.json.JsonBoolean;
import util.json.JsonObject;
import util.json.JsonStreamReader;
import util.json.JsonString;
import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/** This class serves as the channel of communication between a GameMaster and GameSlave.
 * ServerGameChannel implmented both GameInput and GameListener and as such is used
 * for both querying input from a remote player and sending events to remote players
 *
 * @author Simon Pinfold
 *
 */
public class ServerGameChannel implements GameInput, GameListener {

	private JsonStreamReader inObs;
	private OutputStream os;
	private JsonToModel jsonToModel;

	public ServerGameChannel(OutputStream outputStream, InputStream inputStream, Board board) {
		this.os = outputStream;
		this.inObs = new JsonStreamReader(inputStream);
		this.jsonToModel = new JsonToModel(board);
	}

	private synchronized void write(JsonObject ob) {
		try {
			os.write(ob.toString().getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	// documentation imported from interfaces

	@Override
	public synchronized int getNumberOfPlayers(int min, int max) {
		// not required for network players
		throw new UnsupportedOperationException();
	}


	@Override
	public synchronized List<String> getHumanNames() {
		// not required for network players
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized String getSingleName() {
		write(new MessageBuilder().
					type("pull").
					name("getSingleName").
				build());
		return ((JsonString)inObs.next().get("return")).value();
	}

	@Override
	public synchronized Character chooseCharacter(String playerName, List<Character> allCharacters, List<Character> availableCharacters) {
		write(new MessageBuilder().
					type("pull").
					name("chooseCharacter").
						parameter("playerName", playerName).
						parameter("allCharacters", ModelToJson.<Character>cardsToJson(allCharacters)).
						parameter("availableCharacters", ModelToJson.<Character>cardsToJson(availableCharacters)).
				build());

		return jsonToModel.jsonToCard(inObs.next().get("return"));
	}



	@Override
	public synchronized void startTurn(Hand hand) {
		write(new MessageBuilder().
					type("pull").
					name("startTurn").
					parameter("hand", ModelToJson.handToJson(hand)).
				build());
		inObs.next(); // wait for return here
	}

	@Override
	public synchronized Location getDestination(List<Location> possibleLocations) {
		write(new MessageBuilder().
				type("pull").
				name("getDestination").
					parameter("possibleLocations", ModelToJson.locationsToJson(possibleLocations)).
			build());

		return jsonToModel.jsonToLocation(inObs.next().get("return"));
	}

	@Override
	public synchronized boolean hasSuggestion() {
		write(new MessageBuilder().
				type("pull").
				name("hasSuggestion").
			build());

		return ((JsonBoolean)inObs.next().get("return")).value();
	}

	@Override
	public synchronized Weapon pickWeapon() {
		write(new MessageBuilder().
				type("pull").
				name("pickWeapon").
			build());

		return jsonToModel.jsonToCard(inObs.next().get("return"));
	}

	@Override
	public synchronized Character pickCharacter() {
		write(new MessageBuilder().
				type("pull").
				name("pickCharacter").
			build());

		return jsonToModel.jsonToCard(inObs.next().get("return"));
	}

	@Override
	public synchronized boolean hasAccusation() {
		write(new MessageBuilder().
				type("pull").
				name("hasAccusation").
			build());

		return ((JsonBoolean)inObs.next().get("return")).value();
	}

	@Override
	public synchronized Room pickRoom() {
		write(new MessageBuilder().
				type("pull").
				name("pickRoom").
			build());


		return jsonToModel.jsonToCard(inObs.next().get("return"));
	}

	public synchronized Card selectDisprovingCardToShow(Character character, Character suggester, List<Card> possibleShow) {
		write(new MessageBuilder().
				type("pull").
				name("selectDisprovingCardToShow").
					parameter("character", ModelToJson.cardToJson(character)).
					parameter("suggester", ModelToJson.cardToJson(suggester)).
					parameter("possibleShow", ModelToJson.cardsToJson(possibleShow)).
			build());

		return jsonToModel.jsonToCard(inObs.next().get("return"));
	}

	@Override
	public synchronized void suggestionDisproved(Character characterDisproved, Card disprovingCard) {
		write(new MessageBuilder().
				type("pull").
				name("suggestionDisproved").
					parameter("characterDisproved", ModelToJson.cardToJson(characterDisproved)).
					parameter("disprovingCard", ModelToJson.cardToJson(disprovingCard)).
			build());
		
		inObs.next();
	}

	@Override
	public synchronized int getNetworkPlayerCount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized void onCharacterJoinedGame(String playerName, Character character, PlayerType playerType) {
		write(new MessageBuilder().
					type("push").
					name("onCharacterJoinedGame").
						parameter("playerName", playerName).
						parameter("character", ModelToJson.cardToJson(character)).
						parameter("playerType", playerType.name()).
				build());
	}

	@Override
	public synchronized void onTurnBegin(String playerName, Character playersCharacter) {
		write(new MessageBuilder().
					type("push").
					name("onTurnBegin").
						parameter("playerName", playerName).
						parameter("playersCharacter", ModelToJson.cardToJson(playersCharacter)).
				build());
	}

	@Override
	public synchronized void onSuggestionUndisputed(Character suggester, Suggestion suggestion, Room room) {
		write(new MessageBuilder().
				type("push").
				name("onSuggestionUndisputed").
					parameter("suggester", ModelToJson.cardToJson(suggester)).
					parameter("suggestion", ModelToJson.suggestionToJson(suggestion)).
					parameter("room", ModelToJson.cardToJson(room)).
			build());
	}

	@Override
	public synchronized void onSuggestionDisproved(Character suggester,	Suggestion suggestion, Room room, Character disprover) {
		write(new MessageBuilder().
				type("push").
				name("onSuggestionDisproved").
					parameter("suggester", ModelToJson.cardToJson(suggester)).
					parameter("suggestion", ModelToJson.suggestionToJson(suggestion)).
					parameter("room", ModelToJson.cardToJson(room)).
					parameter("disprover", ModelToJson.cardToJson(disprover)).
			build());
	}

	@Override
	public synchronized void onAccusation(Character accuser, Accusation accusation,	boolean correct) {
		write(new MessageBuilder().
				type("push").
				name("onAccusation").
					parameter("accuser", ModelToJson.cardToJson(accuser)).
					parameter("accusation", ModelToJson.accusationToJson(accusation)).
					parameter("correct", correct).
			build());
	}

	@Override
	public synchronized void onWeaponMove(Weapon weapon, Location newLocation) {
		write(new MessageBuilder().
				type("push").
				name("onWeaponMove").
					parameter("weapon", ModelToJson.cardToJson(weapon)).
					parameter("newLocation", ModelToJson.locationToJson(newLocation)).
			build());
	}

	@Override
	public synchronized void onCharacterMove(Character character, Location newLocation) {

		write(new MessageBuilder().
				type("push").
				name("onCharacterMove").
					parameter("character", ModelToJson.cardToJson(character)).
					parameter("newLocation", ModelToJson.locationToJson(newLocation)).
			build());
	}

	@Override
	public synchronized void onDiceRolled(int dice1, int dice2) {
		write(new MessageBuilder().
				type("push").
				name("onDiceRolled").
					parameter("dice1", dice1).
					parameter("dice2", dice2).
			build());
	}

	@Override
	public synchronized void onGameWon(String playerName, Character playersCharacter) {
		write(new MessageBuilder().
				type("push").
				name("onGameWon").
					parameter("playerName", playerName).
					parameter("playersCharacter", ModelToJson.cardToJson(playersCharacter)).
			build());
	}

	public synchronized void sendHand(Hand hand) {
		write(new MessageBuilder().
				type("hand").
					parameter("hand", ModelToJson.handToJson(hand)).
			build());
	}

	@Override
	public synchronized void waitingForNetworkPlayers(int remaining) {
		write(new MessageBuilder().
				type("push").
				name("waitingForNetworkPlayers").
					parameter("remaining", remaining).
			build());
	}

	@Override
	public void onLostGame(String playerName, Character playersCharacter) {
		write(new MessageBuilder().
				type("push").
				name("onLostGame").
					parameter("playerName", playerName).
					parameter("playersCharacter", ModelToJson.cardToJson(playersCharacter)).
			build());
	}

	@Override
	public void onSuggestion(String suggesterPlayerName, Character suggester, Suggestion suggestion, Room room) {
		write(new MessageBuilder().
				type("push").
				name("onSuggestion").
					parameter("suggesterPlayerName", suggesterPlayerName).
					parameter("suggester", ModelToJson.cardToJson(suggester)).
					parameter("suggestion", ModelToJson.suggestionToJson(suggestion)).
					parameter("room", ModelToJson.cardToJson(room)).
			build());
	}

}
