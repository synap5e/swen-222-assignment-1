package cluedo.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cluedo.game.board.Accusation;
import cluedo.game.board.Board;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.gui.CluedoFrame;
import cluedo.gui.GUIHandle;
import cluedo.util.json.JsonObject;
import cluedo.util.json.JsonParseException;
import cluedo.util.json.MinimalJson;

public class GameMaster {

	private ArrayList<Player> players;
	private Accusation correctAccusation;
	private Random random = new Random();
	private int turn;

	public GameMaster(Board board, GUIHandle guiHandle) {
		turn = 0;
		players = new ArrayList<Player>();
		
		Dealer dealer = new Dealer(board);
		correctAccusation = dealer.createAccusation();
		
		List<Hand> hands = dealer.dealHands(guiHandle.getNumberOfPlayers());
		List<Character> characters = new ArrayList<Character>(board.getCharacters());
		
		assert characters.size() <= hands.size();

		while(!hands.isEmpty()){
			/*players.add(new HumanPlayer(hands.remove(0), 
							new GameView(board, guiHandle.chooseCharacter(characters)))
						);
			
			players.add(new AIPlayer(hands.remove(0), 
					new GameView(board, characters.remove(random.nextInt(characters.size()))));
				);*/
		}
	}

	public void addGameListener(CluedoFrame frame) {
		// TODO Auto-generated method stub
	}
	
	
	public void startGame(){
		while (true /* TODO: game not finished*/ ){
			Player player = players.get(turn++);
			
			int roll = random.nextInt(6)+1;
			
			// TODO
			//List<Location> possibleLocations = board.getPossibleDestinations(roll);
			//Location desired = player.getDestination(possibleLocations);
		}
	}
	

}
