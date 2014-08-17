package cluedo.controller;

import java.io.IOException;
import java.io.OutputStream;
import util.json.JsonBoolean;
import util.json.JsonEntity;
import util.json.JsonNumber;
import util.json.JsonObject;
import util.json.JsonStreamReader;
import util.json.JsonString;
import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.network.JsonToModel;
import cluedo.controller.network.ModelToJson;
import cluedo.controller.network.MessageBuilder;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Hand;

/** This class is the controller for a client game. Because the client/server
 * architecture used makes all client dumb, this class simply listens for events
 * from the Master and responds to queries from the master.
 * 
 * GameSlave also updates the model with any events fired from the master
 *
 * @author Simon Pinfold
 *
 */
public class GameSlave {

	private Board board;
	private GameInput input;
	private GameListener listener = null;
	private OutputStream os;
	private Hand hand;
	private JsonToModel jsonToModel;

	/** Create a new GameSlavem using the board as the starting state, and 
	 * the provided GameInput to answer pulls from the GameMaster that 
	 * this GameSlave will be connected to.
	 * 
	 * @param board the board to use
	 * @param input the user input
	 */
	public GameSlave(Board board, GameInput input) {
		this.board = board;
		this.input = input;
		this.jsonToModel = new JsonToModel(board);
	}

	/** Set the GameListener for the events from the remote game. 
	 * 
	 * @param listener the GameListener
	 */
	public void setGameListener(GameListener listener) {
		this.listener = listener;
	}

	/** Convenience method to send a json object back to the GameMaster
	 * 
	 * @param ob
	 */
	private void write(JsonObject ob) {
		try {
			os.write(ob.toString().getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/** Connect to a remote and start receiving messages.
	 * 
	 * @param reader the provider of json messages
	 * @param outputStream the stream to send responses to pulls on
	 */
	public void startGame(JsonStreamReader reader, OutputStream outputStream) {
		this.os = outputStream;
		while (true){
			JsonObject message = reader.next();
			String type = ((JsonString) message.get("type")).value();
			if (type.equals("pull")){
				JsonEntity ret = handlePull(message);

				write(new MessageBuilder().
							type("response").
							returnValue(ret).
						build());


			} else if (type.equals("push")){

				handlePush(message);

			} else if (type.equals("hand")){
				JsonObject parameters = (JsonObject) message.get("parameters");
				this.hand = jsonToModel.jsonToHand(parameters.get("hand"));
			} else {
				throw new RuntimeException("Game Slave could not handle message type \"" + type + "\"");
			}
		}
	}

	/** Handle a push message from the remote GameMaster.
	 * Push messages are events sent to the GameSlave's GameListeners and
	 * used to update the mode.
	 * 
	 * @param message the message
	 */
	private void handlePush(JsonObject message) {
		String methodName = ((JsonString) message.get("name")).value();
		JsonObject parameters = (JsonObject)message.get("parameters");

		switch(methodName){
		case "onCharacterJoinedGame":
			listener.onCharacterJoinedGame(
					((JsonString)parameters.get("playerName")).value(),
					jsonToModel.<Character>jsonToCard(parameters.get("character")),
					PlayerType.valueOf(((JsonString)parameters.get("playerType")).value())
			);
			return;

		case "onCharacterMove":
			Character character = jsonToModel.jsonToCard(parameters.get("character"));
			Location newLocation = jsonToModel.jsonToLocation(parameters.get("newLocation"));
			board.moveCharacter(character, newLocation);
			listener.onCharacterMove(character, newLocation);
			return;

		case "onTurnBegin":
			listener.onTurnBegin(
					((JsonString)parameters.get("playerName")).value(),
					jsonToModel.<Character>jsonToCard(parameters.get("playersCharacter"))
			);
			return;

		case "onDiceRolled":
			listener.onDiceRolled(
					(int) ((JsonNumber)parameters.get("dice1")).value(),
					(int) ((JsonNumber)parameters.get("dice2")).value()
			);
			return;
		
		case "onSuggestionUndisputed":
			listener.onSuggestionUndisputed(
					jsonToModel.<Character>jsonToCard(parameters.get("suggester")),
					jsonToModel.jsonToSuggestion(parameters.get("suggestion")),
					jsonToModel.<Room>jsonToCard(parameters.get("room"))
			);
			return;
			
		case "onSuggestionDisproved":
			listener.onSuggestionDisproved(
					jsonToModel.<Character>jsonToCard(parameters.get("suggester")),
					jsonToModel.jsonToSuggestion(parameters.get("suggestion")),
					jsonToModel.<Room>jsonToCard(parameters.get("room")),
					jsonToModel.<Character>jsonToCard(parameters.get("disprover"))
			);
			return;
			
		case "onAccusation":
			listener.onAccusation(
					jsonToModel.<Character>jsonToCard(parameters.get("accuser")),
					jsonToModel.jsonToAccusation(parameters.get("accusation")),
					((JsonBoolean)parameters.get("correct")).value()
			);
			return;
			
		case "onWeaponMove":
			Weapon weapon = jsonToModel.jsonToCard(parameters.get("weapon"));
			newLocation = jsonToModel.jsonToLocation(parameters.get("newLocation"));
			board.moveWeapon(weapon, newLocation);
			listener.onWeaponMove(weapon, newLocation);
			return;
			
		case "onGameWon":
			listener.onGameWon(
					((JsonString)parameters.get("playerName")).value(),
					jsonToModel.<Character>jsonToCard(parameters.get("playersCharacter"))
			);
			return;
			
		case "waitingForNetworkPlayers":
			listener.waitingForNetworkPlayers(
					(int) ((JsonNumber)parameters.get("remaining")).value()
			);
			return;
		
		case "onLostGame":
			listener.onLostGame(
					((JsonString)parameters.get("playerName")).value(),
					jsonToModel.<Character>jsonToCard(parameters.get("playersCharacter"))
			);
			return;
					
		case "onSuggestion":
			listener.onSuggestion(
					((JsonString)parameters.get("suggesterPlayerName")).value(),
					jsonToModel.<Character>jsonToCard(parameters.get("suggester")),
					jsonToModel.jsonToSuggestion(parameters.get("suggestion")),
					jsonToModel.<Room>jsonToCard(parameters.get("room"))
			);
			return;
					
		default:
			//System.err.println("Game Slave could not handle push for \"" + methodName + "\"");
			throw new RuntimeException("Game Slave could not handle push for \"" + methodName + "\"");

		}
	}

	/** Handle a pull message from the remote GameMaster.
	 * Pull messages are a request for user input, and require the GameSlave
	 * to respond with an appropriate response message after querying the desired 
	 * information from a user.
	 * 
	 * @param message the pull message
	 * @return the response to send back to the GameMaster
	 */
	private JsonEntity handlePull(JsonObject message) {
		String methodName = ((JsonString) message.get("name")).value();
		JsonObject parameters = (JsonObject)message.get("parameters");

		switch(methodName){
		case "getSingleName":
			return new JsonString(input.getSingleName());

		case "chooseCharacter":
			return ModelToJson.cardToJson(
						input.chooseCharacter(
								((JsonString)parameters.get("playerName")).value(),
								jsonToModel.<Character>jsonToCards(parameters.get("allCharacters")),
								jsonToModel.<Character>jsonToCards(parameters.get("availableCharacters"))
						)
					);

		case "startTurn":
			input.startTurn(this.hand);
			return new JsonObject();

		case "getDestination":
			return ModelToJson.locationToJson(
						input.getDestination(
							jsonToModel.jsonToLocations(parameters.get("possibleLocations"))
						)
					);

		case "hasSuggestion":
			return new JsonBoolean(input.hasSuggestion());

		case "pickWeapon":
			return ModelToJson.cardToJson(
						input.pickWeapon()
					);

		case "pickCharacter":
			return ModelToJson.cardToJson(
						input.pickCharacter()
					);

		case "hasAccusation":
			return new JsonBoolean(input.hasAccusation());

		case "pickRoom":
			return ModelToJson.cardToJson(
						input.pickRoom()
					);
			
		case "selectDisprovingCardToShow":
			return ModelToJson.cardToJson(
					input.selectDisprovingCardToShow(
							jsonToModel.<Character>jsonToCard(parameters.get("character")),
							jsonToModel.<Character>jsonToCard(parameters.get("suggester")),
							jsonToModel.<Card>jsonToCards(parameters.get("possibleShow"))
					)
				);
			
		case "suggestionDisproved":
				input.suggestionDisproved(
							jsonToModel.<Character>jsonToCard(parameters.get("characterDisproved")),
							jsonToModel.<Character>jsonToCard(parameters.get("disprovingCard"))
				);
				return new JsonObject();
				
		default:
			throw new RuntimeException("Game Slave could not answer pull for \"" + methodName + "\"");
		}
	}


}
