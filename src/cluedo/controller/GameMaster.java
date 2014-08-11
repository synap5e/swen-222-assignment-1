package cluedo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.json.JsonObject;
import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.network.NetworkGameChannel;
import cluedo.controller.network.NetworkPlayerHandler;
import cluedo.controller.player.AIPlayer;
import cluedo.controller.player.GameStateFacade;
import cluedo.controller.player.HumanPlayer;
import cluedo.controller.player.Player;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;

/**
 * 
 * @author Simon Pinfold
 *
 */
public class GameMaster {

	private List<Player> players;
	private Map<Player, Character> playingAs;
	
	private Accusation correctAccusation;
	private List<GameListener> listeners;
	private Random random = new Random();
	private int turn;
	private Board board;
	private GameInput input;
	private JsonObject defs;

	public GameMaster(Board board, JsonObject defs, GameInput input) {
		this.board = board;
		this.input = input;
		this.defs = defs;
		listeners = new ArrayList<GameListener>();
	}
	
	public void addGameListener(GameListener listener) {
		listeners.add(listener);
	}
	
	public void createGame() throws IOException{
		turn = 0;
		players = new ArrayList<Player>();
		playingAs = new HashMap<Player, Character>();
		
		Dealer dealer = new Dealer(board);
		this.correctAccusation = dealer.createAccusation();
		
		List<Character> pickableCharacters = new ArrayList<Character>(board.getCharacters());
		
		int numberOfPlayers = input.getNumberOfPlayers(2, pickableCharacters.size());
		assert numberOfPlayers <= pickableCharacters.size();
		List<Hand> hands = dealer.dealHands(numberOfPlayers);
		
		// This array is used to shuffle the order of players
		// This can be extended if we get more that just human and computer players
		ArrayList<Player.PlayerType> playerTypes = new ArrayList<Player.PlayerType>();
		
		List<String> humanNames = input.getHumanNames();
		int networkPlayers = input.getNetworkPlayerCount();
		assert humanNames.size() + networkPlayers <= numberOfPlayers;
		
		NetworkPlayerHandler networkPlayerHandler = null;
		if (networkPlayers > 0){
			networkPlayerHandler = new NetworkPlayerHandler("0.0.0.0", 5362, defs);
		}
		
		for (int i=0;i<humanNames.size();i++) playerTypes.add(PlayerType.LocalHuman);
		for (int i=0;i<networkPlayers;i++) playerTypes.add(PlayerType.RemoteHuman);
		while (playerTypes.size() < numberOfPlayers) playerTypes.add(PlayerType.LocalAI);
		
		
		Collections.shuffle(humanNames);
		Collections.shuffle(playerTypes);
		
		// create the players
		for (int playerNumber=1;playerNumber<=numberOfPlayers;playerNumber++){
			PlayerType playerType = playerTypes.get(playerNumber-1);
			Character character;
			Player player;
			if (playerType == PlayerType.LocalHuman){
				String name = humanNames.remove(0);
				character = input.chooseCharacter(name, board.getCharacters(), pickableCharacters);
				player = new HumanPlayer(name, hands.remove(0), input);
			} else if (playerType == PlayerType.RemoteHuman){
				NetworkGameChannel remoteChannel = networkPlayerHandler.getRemoteInput(30);
				// TODO handle timeout
				
				listeners.add(remoteChannel);
				String name = remoteChannel.getSingleName();
				character = remoteChannel.chooseCharacter(name, board.getCharacters(), pickableCharacters);
				
				Hand hand = hands.remove(0);
				player = new HumanPlayer(name, hand, remoteChannel);
				remoteChannel.sendHand(hand);
			} else {
				character = pickableCharacters.get(random.nextInt(pickableCharacters.size()));
				AIPlayer rp = new AIPlayer(hands.remove(0), new GameStateFacade(board, character, this));
				listeners.add(rp);
				player = rp;
			}
			players.add(player);
			playingAs.put(player, character);
			pickableCharacters.remove(character);
			
			for (GameListener listener : listeners){
				listener.onCharacterJoinedGame(players.get(playerNumber-1).getName(), playingAs.get(players.get(playerNumber-1)), playerTypes.get(playerNumber-1));
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
			
			// TODO accusation can go here too
			
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
