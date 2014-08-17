package cluedo.controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.BasicAIPlayer;
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
import util.json.JsonObject;
import util.json.JsonParseException;
import util.json.MinimalJson;

public class AITest {
	
	private int turns = 0;
	
	@Test
	public void aiNeverIncorrect() throws JsonParseException, IOException{
		int numberOfTrials = 10000;
		BasicAIPlayer.disableThinkWait();
		JsonObject defs = MinimalJson.parseJson(new File("./rules.json"));
		
		int c = 0;
		for (int playerCount=2; playerCount <= 6; playerCount++){
			for (int trial=0;trial<numberOfTrials/5;trial++){
				turns = 0;
				
				Board b = new Board(defs);
				GameMaster gm = new GameMaster(b, mockInput(playerCount));
				gm.addGameListener(assertNoPlayerLooses());
			
				gm.createGame();
				gm.startGame();
				
				System.err.println("Run game " + c++ + "\t with " + playerCount + " players. Took " + turns + "  \tturns");
				
			}
		
		}
	}

	private GameListener assertNoPlayerLooses() {
		return new GameListener() {
			
			@Override
			public void waitingForNetworkPlayers(int count) {
			}
			
			@Override
			public void onWeaponMove(Weapon weapon, Location newLocation) {
			}
			
			@Override
			public void onTurnBegin(String name, Character playersCharacter) {
				turns++;
			}
			
			@Override
			public void onSuggestionUndisputed(Character suggester,
					Suggestion suggestion, Room room) {
			}
			
			@Override
			public void onSuggestionDisproved(Character suggester,
					Suggestion suggestion, Room room, Character disprover) {
			}
			
			@Override
			public void onLostGame(String name, Character playersCharacter) {
				assertTrue("Bots should not loose the game", false);
			}
			
			@Override
			public void onGameWon(String name, Character playersCharacter) {
			}
			
			@Override
			public void onDiceRolled(int dice1, int dice2) {
			}
			
			@Override
			public void onCharacterMove(Character character, Location newLocation) {
			}
			
			@Override
			public void onCharacterJoinedGame(String playerName, Character character,
					PlayerType playerType) {
			}
			
			@Override
			public void onAccusation(Character accuser, Accusation accusation, boolean correct) {
				assertTrue("Bots should not make incorrect accusations", correct);
			}

			@Override
			public void onSuggestion(String suggesterPlayerName,
					Character suggester, Suggestion suggestion, Room room) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	private GameInput mockInput(final int playerCount) {
		return new GameInput() {
			
			@Override
			public void suggestionDisproved(Character characterDisproved, Card disprovingCard) {
			}
			
			@Override
			public void startTurn(Hand hand) {
			}
			
			@Override
			public Card selectDisprovingCardToShow(Character character,	Character suggester, List<Card> possibleShow) {
				return null;
			}
			
			@Override
			public Weapon pickWeapon() {
				return null;
			}
			
			@Override
			public Room pickRoom() {
				return null;
			}
			
			@Override
			public Character pickCharacter() {
				return null;
			}
			
			@Override
			public boolean hasSuggestion() {
				return false;
			}
			
			@Override
			public boolean hasAccusation() {
				return false;
			}
			
			@Override
			public String getSingleName() {
				return null;
			}
			
			@Override
			public int getNumberOfPlayers(int min, int max) {
				return playerCount;
			}
			
			@Override
			public int getNetworkPlayerCount() {
				return 0;
			}
			
			@Override
			public List<String> getHumanNames() {
				return new ArrayList<String>();
			}
			
			@Override
			public Location getDestination(List<Location> possibleLocations) {
				return null;
			}
			
			@Override
			public Character chooseCharacter(String playerName,	List<Character> allCharacters, List<Character> availableCharacters) {
				return null;
			}
		};
	}

}
