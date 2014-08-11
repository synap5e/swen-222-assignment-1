package cluedo.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import util.json.JsonBoolean;
import util.json.JsonEntity;
import util.json.JsonList;
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
import cluedo.model.cardcollection.Hand;
import cluedo.view.CluedoFrame;
import cluedo.view.GUIGameInput;

/**
 * 
 * @author Simon Pinfold
 *
 */
public class GameSlave {
	
	private Board board;
	private GameInput input;
	private GameListener listener = null;
	private OutputStream os;
	private String playerName;
	private Hand hand;
	private JsonToModel jsonToModel;


	public GameSlave(Board board, GameInput input) {
		this.board = board;
		this.input = input;
		this.jsonToModel = new JsonToModel(board);
	}

	public void addGameListener(GameListener listener) {
		assert this.listener == null : "GameSlave only supports one GameListener";
		this.listener = listener;
	}
	
	private void write(JsonObject ob) {
		try {
			os.write(ob.toString().getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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
					(int) ((JsonNumber)parameters.get("roll")).value()
			);
			return;
			
		default:
			System.err.println("Game Slave could not handle push for \"" + methodName + "\"");
			//throw new RuntimeException("Game Slave could not handle push for \"" + methodName + "\"");
			
		}
	}

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
			
					
		default:
			System.err.println("Game Slave could not answer pull for \"" + methodName + "\"");
			return new JsonObject();
			//throw new RuntimeException("Game Slave could not answer pull for \"" + methodName + "\"");
		}
	}

	
}
