package cluedo.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cluedo.game.board.Accusation;
import cluedo.game.board.Board;
import cluedo.game.board.Card;
import cluedo.game.board.Character;
import cluedo.game.board.Hand;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Suggestion;
import cluedo.gui.GUIHandle;

public class GameMaster {

	private List<Player> players;
	private Map<Player, Character> playingAs;
	
	private Accusation correctAccusation;
	private List<GameListener> listeners;
	private Random random = new Random();
	private int turn;
	private Board board;
	private GUIHandle guiHandle;

	public GameMaster(Board board, GUIHandle guiHandle) {
		this.board = board;
		this.guiHandle = guiHandle;
		listeners = new ArrayList<GameListener>();
	}
	
	public void addGameListener(GameListener listener) {
		listeners.add(listener);
	}
	
	public void createGame(){
		turn = 0;
		players = new ArrayList<Player>();
		playingAs = new HashMap<Player, Character>();
		
		Dealer dealer = new Dealer(board);
		this.correctAccusation = dealer.createAccusation();
		
		List<Character> pickableCharacters = new ArrayList<Character>(board.getCharacters());
		
		int numberOfPlayers = guiHandle.getNumberOfPlayers(2, pickableCharacters.size());
		List<Hand> hands = dealer.dealHands(numberOfPlayers);
		
		// This array is used to shuffle the order of players
		// This can be extended if we get more that just human and computer players
		ArrayList<Boolean> humans = new ArrayList<Boolean>();
		
		List<String> humanNames = guiHandle.getHumanNames();
		assert humanNames.size() < numberOfPlayers;
		
		for (int i=0;i<numberOfPlayers-humanNames.size();i++) humans.add(false);
		for (int i=0;i<humanNames.size();i++) humans.add(true);
		Collections.shuffle(humanNames);
		Collections.shuffle(humans);
		
		// create the players
		for (int playerNumber=1;playerNumber<=numberOfPlayers;playerNumber++){
			boolean playerIsHuman = humans.get(playerNumber-1);
			Character character;
			Player player;
			if (playerIsHuman){
				String name = humanNames.remove(0);
				character = guiHandle.chooseCharacter(name, pickableCharacters);
				player = new HumanPlayer(name, hands.remove(0), guiHandle);
			} else {
				character = pickableCharacters.get(random.nextInt(pickableCharacters.size()));
				AIPlayer rp = new AIPlayer(hands.remove(0), new GameView(board, character, this));
				listeners.add(rp);
				player = rp;
			}
			players.add(player);
			playingAs.put(player, character);
			pickableCharacters.remove(character);
		}
		
		// announce to all listeners who is playing the game. This is done after the players are 
		// created so all the AI players also get informed.
		for (int playerNumber=1;playerNumber<=numberOfPlayers;playerNumber++){
			for (GameListener listener : listeners){
				listener.onCharacterJoinedGame(players.get(playerNumber-1).getName(), playingAs.get(players.get(playerNumber-1)), humans.get(playerNumber-1));
			}
		}
	}

	public void startGame(){
		while (true){
			Player player = players.get(turn++ % players.size());
			Character playersCharacter = playingAs.get(player);
			
			for (GameListener listener : listeners){
				listener.onTurnBegin(player.getName(), playersCharacter);
			}
			
			
			int roll = random.nextInt(6)+1;
			
			player.waitForDiceRollOK();
			
			for (GameListener listener : listeners){
				listener.onDiceRolled(roll);
			}
			
			List<Location> possibleLocations = board.getPossibleDestinations(board.getLocationOf(playersCharacter), roll);
			
			Location destination = player.getDestination(possibleLocations);
			
			// TODO, what if not in passed in list - should we complain here always, or only in assert mode
			assert possibleLocations.contains(destination);
			
			board.moveCharacter(playersCharacter, destination);
			
			for (GameListener listener : listeners){
				listener.onCharacterMove(playersCharacter, destination);
			}
			
			if(board.getLocationOf(playersCharacter) instanceof Room && player.hasSuggestion()){
				Suggestion suggestion = player.getSuggestion();
				Room room = (Room) board.getLocationOf(playersCharacter);
				
				board.moveWeapon(suggestion.getWeapon(), room);
				for (GameListener listener : listeners){
					listener.onWeaponMove(suggestion.getWeapon(), room);
				}
				
				board.moveCharacter(suggestion.getCharacter(), room);
				for (GameListener listener : listeners){
					listener.onCharacterMove(suggestion.getCharacter(), room);
				}
				
				boolean disproved = false;
				for (Player p : getPlayersClockwiseOf(player)){
					if (p.canDisprove(suggestion.getCharacter(), suggestion.getWeapon(), room)){
						Card disprovingCard = p.selectDisprovingCard(suggestion.getCharacter(), suggestion.getWeapon(), room);
						
						// TODO should we complain here always, or only in assert mode?
						assert 	disprovingCard == suggestion.getCharacter() || 
								disprovingCard == suggestion.getWeapon() ||
								disprovingCard == room;
						
						for (GameListener listener : listeners){
							if (listener != player){
								listener.onSuggestionDisproved(playersCharacter, suggestion, playingAs.get(p));
							}
						}
						player.suggestionDisproved(suggestion, playingAs.get(p), disprovingCard);
						disproved = true;
						
						break;
					}
				}
				
				if (!disproved){
					for (GameListener listener : listeners){
						listener.onSuggestionUndisputed(playersCharacter, suggestion);
					}
				}
			}
			
			if (player.hasAccusation()){
				Accusation accusation = player.getAccusation();
				boolean correct = accusation.equals(correctAccusation);
				for (GameListener listener : listeners){
					listener.onAccusation(playersCharacter, accusation, correct);
				}
				
				if (correct){
					for (GameListener listener : listeners){
						listener.onGameWon(player.getName(), playersCharacter);
					}
				}
				
			}
		}
	}
	
	private List<Player> getPlayersClockwiseOf(Player p) {
		List<Player> r = new ArrayList<Player>(players);
		Collections.rotate(r, players.indexOf(p));
		r.remove(0);
		return r;
	}

	public List<Character> getCharactersClockwiseOf(Character character) {
		// where are my list comprehensions :'(
		// r = [ playingAs[p] for p in players ]
		List<Character> r = new ArrayList<Character>();
		for (Player p : players){
			r.add(playingAs.get(p));
		}
		Collections.rotate(r, players.indexOf(character));
		r.remove(0);
		return r;
	}

}
