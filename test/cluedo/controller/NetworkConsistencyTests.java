package cluedo.controller;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.JOptionPane;

import org.junit.Test;

import sun.org.mozilla.javascript.tools.debugger.GuiCallback;
import util.json.JsonEntity;
import util.json.JsonObject;
import util.json.JsonParseException;
import util.json.JsonStreamReader;
import util.json.MinimalJson;
import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.network.NetworkPlayerHandler;
import cluedo.controller.network.ServerGameChannel;
import cluedo.controller.player.HumanPlayer;
import cluedo.controller.player.Player;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Token;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;
import cluedo.view.CluedoFrame;

/** This test suite tests that 2 (or more) boards across a networked game
 * end up in an identical state. In doing so this tests a large portion of 
 * the network code
 * 
 * @author Simon Pinfold
 *
 */
public class NetworkConsistencyTests implements GameListener {
	
	private enum Moves { MOVE, SUGGEST, ACCUSE, SHOW, };
	
	private class ScriptedInput implements GameInput{
		
		private Queue<Object[]> moves;
		private Object[] move;

		public ScriptedInput(Queue<Object[]> moves) {
			this.moves = moves;
		}

		@Override
		public Location getDestination(List<Location> possibleLocations) {
			Object[] move = moves.poll();
			assert move[0] == Moves.MOVE;
			return (Location) move[1];
		}

		@Override
		public boolean hasSuggestion() {
			if (moves.peek()[0] == Moves.SUGGEST){
				this.move = moves.poll();
				return true;
			}
			return false;
		}

		@Override
		public boolean hasAccusation() {
			if (moves.peek()[0] == Moves.ACCUSE){
				this.move = moves.poll();
				return true;
			}
			return false;
		}
		
		@Override
		public Weapon pickWeapon() {
			return (Weapon) move[1];
		}

		@Override
		public Character pickCharacter() {
			return (Character) move[2];
		}

		@Override
		public Room pickRoom() {
			return (Room) move[3];
		}
		
		@Override
		public Card selectDisprovingCardToShow(Character character,
				Character suggester, List<Card> possibleShow) {
			assert moves.peek()[0] == Moves.SHOW;
			return (Card) moves.poll()[1];
		}
		
		
		@Override
		public Character chooseCharacter(String playerName,
				List<Character> allCharacters,
				List<Character> availableCharacters) {
			return availableCharacters.get(0);
		}

		@Override
		public void startTurn(Hand hand) {
			try {
				Thread.sleep(showgames ? 200 : 0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		@Override
		public int getNumberOfPlayers(int min, int max) {
			assert false;
			return 0;
		}

		@Override
		public List<String> getHumanNames() {
			assert false;
			return null;
		}

		@Override
		public void suggestionDisproved(Character characterDisproved,
				Card disprovingCard) {
		}

		@Override
		public int getNetworkPlayerCount() {
			assert false;
			return 0;
		}

		@Override
		public String getSingleName() {
			return toString();
		}

	}
	
	
	private GameMaster master;
	
	private boolean showgames = true;

	@Test
	/**
	 * A simple game between 2 players. They move around, make some suggestions
	 * then 1 miraculously makes the correct accusation
	 * 
	 */
	public void testGameScript1() throws IOException, JsonParseException{
		JsonObject defs = MinimalJson.parseJson(new File("./rules/cards.json"));
		JsonObject starts = MinimalJson.parseJson("{ \"Rope\" : \"Library\",\"Dagger\" : \"Ballroom\",\"Spanner\" : \"Conservatory\",\"Revolver\" : \"Kitchen\",\"Candlestick\" : \"Lounge\",\"Lead Piping\" : \"Billiard Room\"}");
				
		Board masterBoard = new Board(defs, starts);
		
		Queue<Object[]> player0Moves = new LinkedList<Object[]>(); // Mrs White
		Queue<Object[]> player1Moves = new LinkedList<Object[]>(); // Rev. Green
		
		player0Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(7,4)});
		
		player1Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(16,4)});
		
		player0Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(8,5)}); // ballroom
		player0Moves.offer(new Object[]{
				Moves.SUGGEST, 
				masterBoard.getCard("Rope"),
				masterBoard.getCard("Rev. Green")
		});  // suggest Rev. Green with the rope (in the ballroom)
		// can't disprove
		
		player1Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(19,5)}); // move to conservatory
		player1Moves.offer(new Object[]{
				Moves.SUGGEST, 
				masterBoard.getCard("Revolver"),
				masterBoard.getCard("Mrs Peacock")
		});  // suggest Mers Peacock with the Revolver (in the conservatory)
		player0Moves.offer(new Object[]{Moves.SHOW, masterBoard.getCard("Conservatory")}); // but player 1 shows Conservatory to player 2

		player0Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(9,13)}); // near the middle
		player0Moves.offer(new Object[]{
				Moves.ACCUSE, 
				masterBoard.getCard("Rope"),
				masterBoard.getCard("Mrs Peacock"),
				masterBoard.getCard("Study"),
		});  // win the game
		
		player1Moves.offer(new Object[]{null}); // end of game
		
		runGame(masterBoard, player0Moves, player1Moves);
	}
	
	@Test
	/** Among other things this tests ServerGameChannel.waitingForNetworkPlayers
	 * 
	 */
	public void testMultipleRemotePlayers() throws JsonParseException, IOException{
		JsonObject defs = MinimalJson.parseJson(new File("./rules/cards.json"));
		JsonObject starts = MinimalJson.parseJson("{ \"Rope\" : \"Library\",\"Dagger\" : \"Ballroom\",\"Spanner\" : \"Conservatory\",\"Revolver\" : \"Kitchen\",\"Candlestick\" : \"Lounge\",\"Lead Piping\" : \"Billiard Room\"}");
				
		Board masterBoard = new Board(defs, starts);
		
		Queue<Object[]> player0Moves = new LinkedList<Object[]>(); // Mrs White
		Queue<Object[]> player1Moves = new LinkedList<Object[]>(); // Rev. Green
		Queue<Object[]> player2Moves = new LinkedList<Object[]>(); // Mrs Peacock
		
		player0Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(7,4)});
		
		player1Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(16,4)});
		
		player2Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(18,5)});
		player2Moves.offer(new Object[]{Moves.ACCUSE,
				masterBoard.getCard("Dagger"),
				masterBoard.getCard("Rev. Green"),
				masterBoard.getCard("Lounge"),
		}); // loose the game
		
		player0Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(8,9)});
		player0Moves.offer(new Object[]{
				Moves.ACCUSE, 
				masterBoard.getCard("Rope"),
				masterBoard.getCard("Mrs Peacock"),
				masterBoard.getCard("Study"),
		});  // win the game
		
		player1Moves.offer(new Object[]{null}); // end of game
		
		runGame(masterBoard, player0Moves, player1Moves, player2Moves);
	}

	@Test
	public void testRemotePlayerShowHand() throws IOException, JsonParseException{
		JsonObject defs = MinimalJson.parseJson(new File("./rules/cards.json"));
		JsonObject starts = MinimalJson.parseJson("{ \"Rope\" : \"Library\",\"Dagger\" : \"Ballroom\",\"Spanner\" : \"Conservatory\",\"Revolver\" : \"Kitchen\",\"Candlestick\" : \"Lounge\",\"Lead Piping\" : \"Billiard Room\"}");
				
		Board masterBoard = new Board(defs, starts);
		
		Queue<Object[]> player0Moves = new LinkedList<Object[]>(); // Mrs White
		Queue<Object[]> player1Moves = new LinkedList<Object[]>(); // Rev. Green
		Queue<Object[]> player2Moves = new LinkedList<Object[]>(); // Mrs Peacock
		
		player0Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(6,3)});
		
		player1Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(17,3)});
		
		player2Moves.offer(new Object[]{Moves.MOVE, masterBoard.getLocation(18,7)});
		
		player0Moves.offer(new Object[]{Moves.MOVE, masterBoard.getCard("Ballroom")});
		
		player1Moves.offer(new Object[]{Moves.MOVE, masterBoard.getCard("Ballroom")});
		
		player2Moves.offer(new Object[]{Moves.MOVE, masterBoard.getCard("Ballroom")});
		
		player0Moves.offer(new Object[]{Moves.MOVE, masterBoard.getCard("Billiard Room")});
		player0Moves.offer(new Object[]{Moves.SUGGEST,
				masterBoard.getCard("Lead Piping"),
				masterBoard.getCard("Colonel Mustard"),
		});
		player1Moves.offer(new Object[]{Moves.SHOW, masterBoard.getCard("Billiard Room")}); // remote player disproves
		player0Moves.offer(new Object[]{
				Moves.ACCUSE, 
				masterBoard.getCard("Lead Piping"),
				masterBoard.getCard("Colonel Mustard"),
				masterBoard.getCard("Billiard Room"),
		});  // loose the game
		
		player1Moves.offer(new Object[]{Moves.MOVE, masterBoard.getCard("Billiard Room")});
		player1Moves.offer(new Object[]{
				Moves.ACCUSE, 
				masterBoard.getCard("Lead Piping"),
				masterBoard.getCard("Colonel Mustard"),
				masterBoard.getCard("Billiard Room"),
		});  // loose the game
		
		player2Moves.offer(new Object[]{null}); // game over here
		
		
		runGame(masterBoard, player0Moves, player1Moves, player2Moves);
	}
	
	/** Run a using playerScripts as the scripts for all players.
	 * The first playerScript defines the "local" player while the subsequent playerScripts 
	 * are run in new threads as network players. Note that because the model is serialized 
	 * using the card names all scripts can use cards from masterBoard, even though the 
	 * actual cards used by the network players will not be equal to these.
	 * 
	 * Once the game is over all boards will be compared to ensure they are in the same state
	 * 
	 * @param masterBoard The board used by the GameMaster (and therefore the local player)
	 * @param playerScripts the "scripts" for the players to follow.
	 */
	private void runGame(final Board masterBoard, final Queue<Object[]> ... playerScripts) throws IOException, JsonParseException {
		JsonObject defs = MinimalJson.parseJson(new File("./rules/cards.json"));
		
		NetworkPlayerHandler net;
		this.master = new GameMaster(
				masterBoard,
				net = new NetworkPlayerHandler("127.0.0.1", 1337, defs, masterBoard),
				null
		){
			@Override
			public void createGame() throws IOException {
				this.random = new Random(){
					@Override
					public int nextInt(int n) {
						return 2; // always roll a 3 on the dice
					}
				};
				
				this.players = new ArrayList<Player>();
				playingAs = new HashMap<Player, Character>();
				
				List<Card> deck = new ArrayList<Card>();
				deck.addAll(masterBoard.getCharacters());
				deck.addAll(masterBoard.getRooms());
				deck.addAll(masterBoard.getWeapons());
				
				this.correctAccusation = new Accusation(board.getWeapons().get(0), board.getCharacters().get(0), board.getRooms().get(0));
				deck.remove(correctAccusation.getWeapon());
				deck.remove(correctAccusation.getCharacter());
				deck.remove(correctAccusation.getRoom());
				
				
				Character[] cs = new Character[3];
				for (Character c : board.getCharacters()){
					if (c.getName().equals("Mrs White")){
						cs[0] = c;
					}
					if (c.getName().equals("Rev. Green")){
						cs[1] = c;
					}
					if (c.getName().equals("Mrs Peacock")){
						cs[2] = c;
					}
				}
				List<Character> pickableCharacters = Arrays.asList(cs);
				List<Character> availCharacters = new ArrayList<Character>(pickableCharacters);
				
				
				int handSize = deck.size() / playerScripts.length;
				
				Hand hand = new Hand();
				for (int x=0;x<handSize;x++){
					hand.addCard(deck.remove(0));
				}
				players.add(new HumanPlayer("test_player_" + 0, hand, pickableCharacters.get(0), new ScriptedInput(playerScripts[0])));
				playingAs.put(players.get(0), pickableCharacters.get(0));
				System.out.println(pickableCharacters.get(0).getName());
				
				for (int i=1;i<playerScripts.length;i++){
					for (GameListener listener : listeners){
						listener.waitingForNetworkPlayers(playerScripts.length-i);
					}
					
					hand = new Hand();
					for (int x=0;x<handSize;x++){
						hand.addCard(deck.remove(0));
					}
					ServerGameChannel n = this.networkPlayerHandler.getRemoteInput();
					this.addGameListener(n);
					
					String name = n.getSingleName();
					Character c = n.chooseCharacter(name, pickableCharacters, availCharacters);
					availCharacters.remove(c);
					players.add(new HumanPlayer("test_player_" + i + " - " + name, hand, pickableCharacters.get(i), n));
					playingAs.put(players.get(i), pickableCharacters.get(i));
					n.sendHand(hand);
				}
				
				for (int playerNumber=0;playerNumber<playerScripts.length;playerNumber++){
					for (GameListener listener : listeners){
						listener.onCharacterJoinedGame(players.get(playerNumber).getName(), playingAs.get(players.get(playerNumber)), PlayerType.RemoteHuman);
					}
				}
			}
			
			
		};
		
		master.addGameListener(this);
		
		CluedoFrame cf = null;
		if (showgames){
			cf = new CluedoFrame(masterBoard, defs);
			master.addGameListener(cf);
		}
		
		final List<Board> allBoards = new ArrayList<Board>();
		allBoards.add(masterBoard);
		
		final NetworkConsistencyTests pthis = this;
		
		for (int i=1;i<playerScripts.length;i++){
			final int ing = i;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						// wait for server to start
						Thread.sleep(1000);
						
						Socket connection = new Socket(InetAddress.getByName("127.0.0.1"), 1337);
						
						JsonStreamReader reader = new JsonStreamReader(connection.getInputStream());
					
						JsonObject defs = reader.next();
						JsonObject weaponLocations = reader.next();
						Board board = new Board(defs, weaponLocations);
						
						synchronized (allBoards) {
							allBoards.add(board);
						}
						
						GameSlave gc = new GameSlave(board, new ScriptedInput(playerScripts[ing]));
						
						gc.addGameListener(pthis);
						
						gc.startGame(reader, connection.getOutputStream());
						
						connection.close();
						
					} catch (IOException | InterruptedException e){
						e.printStackTrace();
					}
				}
			}).start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		master.createGame();
		master.startGame();
		
		
		// allow messages to propagate to network players
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Board lastBoard = null;
		for (Board b : allBoards){
			if (lastBoard != null){
				
				// assert that all tiles in b hold the same tokens as the coresponding tile in lastBoard
				for (Tile t : b.getTiles()){
					assertTrue("Tiles can only have 1 token", t.getTokens().size() < 2);
					Tile other = (Tile) lastBoard.getLocation(t.getX(), t.getY());
					
					if (t.getTokens().size() != other.getTokens().size()){
						System.out.println();
					}
					
					assertEquals("Tile at " + t.getX() + ", " + t.getY() + " should hold the same number of tokens for all boards", 
							t.getTokens().size(), other.getTokens().size());
					if (t.getTokens().size() > 0){
						assertEquals(t.getTokens().get(0).getName(), other.getTokens().get(0).getName());
					}
				}
				
				for (Room r : b.getRooms()){
					Room other = (Room) lastBoard.getCard(r.getName());
					assertEquals("Room " + r.getName() + " should have the same number of tokens for all boards", 
							r.getTokens().size(), other.getTokens().size());
					
					
					ArrayList<String> tokenNames = new ArrayList<String>();
					for (Token t : r.getTokens()){
						tokenNames.add(t.getName());
					}
					
					for (Token t : other.getTokens()){
						assertTrue(tokenNames.contains(t.getName()));
						tokenNames.remove(t.getName());
					}
					
					assertEquals(0, tokenNames.size());
				}
				
				
			}
			lastBoard = b;
		}
		
		if (cf != null){
			cf.dispose();
		}
		net.shutdown();
	}

	@Override
	public void onCharacterJoinedGame(String playerName, Character character, PlayerType playerType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTurnBegin(String name, Character playersCharacter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccusation(Character accuser, Accusation accusation,
			boolean correct) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWeaponMove(Weapon weapon, Location newLocation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCharacterMove(Character character, Location newLocation) {
		if (newLocation instanceof Tile) System.out.printf("%s to (%d,%d)\n", character.getName(), ((Tile) newLocation).getX(), ((Tile) newLocation).getY());
		
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
	public void waitingForNetworkPlayers(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLostGame(String name, Character playersCharacter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuggestionUndisputed(Character suggester,
			Suggestion suggestion, Room room) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuggestionDisproved(Character suggester,
			Suggestion suggestion, Room room, Character disprover) {
		// TODO Auto-generated method stub
		
	}

}
