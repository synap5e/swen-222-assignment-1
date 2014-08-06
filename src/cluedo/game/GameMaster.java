package cluedo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

import cluedo.game.board.Accusation;
import cluedo.game.board.Board;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.util.json.JsonObject;
import cluedo.util.json.JsonParseException;
import cluedo.util.json.MinimalJson;

public class GameMaster {

	public GameMaster(Board board) {
		// TODO Auto-generated constructor stub
	}
	
	/*private Accusation correctAccusation;
	private Board board;
	private List<Player> players;
	private int turn = 0;
	private Random dice;
	
	public void createGame(){
		// TODO: get from GUI
		int numberOfPlayers = 5;
		
		this.dice = new Random();
		
		JsonObject defs = null;
		try {
			defs = MinimalJson.parseJson(new File("./rules/cards.json"));
		} catch (FileNotFoundException | JsonParseException e) {
			System.err.println("Could not load card definitions");
			e.printStackTrace();
			// TODO: GUI display?
			System.exit(-1);
		}
		Dealer d = new Dealer(defs);
		
		this.correctAccusation = d.createAccusation();
		List<Hand> hands = d.dealHands(numberOfPlayers);
		
		// TODO create players
		//CharacterToken playersToken = null;
		//players.add(new Player(hands.get(0), new GameView(board, playersToken)));
	}
	
	public void startGame(){
		while (true  TODO: game not finished ){
			Player player = players.get(turn++);
			
			int roll = dice.nextInt(6)+1;
			
			// TODO
			//List<Location> possibleLocations = board.getPossibleDestinations(roll);
			//Location desired = player.getDestination(possibleLocations);
		}
	}
	*/

}
