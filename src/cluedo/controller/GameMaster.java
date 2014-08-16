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
import cluedo.controller.network.ServerGameChannel;
import cluedo.controller.network.NetworkPlayerHandler;
import cluedo.controller.player.BasicAIPlayer;
import cluedo.controller.player.GameStateFacade;
import cluedo.controller.player.HumanPlayer;
import cluedo.controller.player.Player;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Hand;
import cluedo.model.cardcollection.Suggestion;
import cluedo.view.GUIGameInput;

/** This class is the controller for a game. It creates a game using the 
 * provided GameInput for how the game is to be played, hosts that  
 * game, asking each player for their moves. 
 * 
 * It is also capable of using a NetworkPlayerHandler to communicate with 
 * one or more GameSlaves allowing clients to connect to the game being run.
 *
 * @author Simon Pinfold
 *
 */
public class GameMaster {

	protected List<Player> players;
	protected Map<Player, Character> playingAs;

	protected Accusation correctAccusation;
	protected List<GameListener> listeners;
	protected Random random = new Random();
	protected int turn;
	protected Board board;
	protected GameInput input;
	protected NetworkPlayerHandler networkPlayerHandler;

	/** Create a new GameMaster
	 * 
	 * @param board the board for this game
	 * @param input the input from the UI/user
	 */
	public GameMaster(Board board, GameInput input) {
		this.board = board;
		this.input = input;
		
		listeners = new ArrayList<GameListener>();
	}
	
	/** Create a new GameMaster with capabilities to accept networked players
	 * joiining the game, depending on the settings specified in input
	 * 
	 * @param board the board for the game to run
	 * @param netHandler the provider of channels to client game
	 * @param input the input from the hosting user
	 */
	public GameMaster(Board board, NetworkPlayerHandler netHandler, GameInput input) {
		this(board, input);
		this.networkPlayerHandler = netHandler;
	}

	/** Add a listener to the events going on in the game
	 * 
	 * @param listener the listenr
	 */
	public void addGameListener(GameListener listener) {
		listeners.add(listener);
	}

	/** Create a new game, using the options provided by the GameInput.
	 * This creates the players objects, and will block until all network
	 * players have connected and all players have selected names and characters.
	 * 
	 * Network players will be sent the starting state of the game, handled by the NetworkPlayerHandler
	 * 
	 * GameListers will be informed of whether the GameMaster is waiting for network
	 * players and who the players are once all have joined the game. 
	 * 
	 * @throws IOException on network error
	 */
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

		List<ServerGameChannel> networkChannels = waitForNetworkPlayers(networkPlayers);

		for (int i=0;i<humanNames.size();i++) playerTypes.add(PlayerType.LocalHuman);
		for (int i=0;i<networkPlayers;i++) playerTypes.add(PlayerType.RemoteHuman);
		while (playerTypes.size() < numberOfPlayers) playerTypes.add(PlayerType.LocalAI);


		// shuffle so that the starting order is random
		Collections.shuffle(humanNames);
		Collections.shuffle(playerTypes);

		// don't favor the first connected network players
		Collections.shuffle(networkChannels);

		// create the players
		for (int playerNumber=0;playerNumber<numberOfPlayers;playerNumber++){
			PlayerType playerType = playerTypes.get(playerNumber);
			Character character;
			Player player;
			if (playerType == PlayerType.LocalHuman){
				String name = humanNames.remove(0);
				character = input.chooseCharacter(name, board.getCharacters(), pickableCharacters);
				player = new HumanPlayer(name, hands.remove(0), character, input);
			} else if (playerType == PlayerType.RemoteHuman){
				ServerGameChannel remoteChannel = networkChannels.remove(0);

				String name = remoteChannel.getSingleName();
				character = remoteChannel.chooseCharacter(name, board.getCharacters(), pickableCharacters);

				Hand hand = hands.remove(0);
				player = new HumanPlayer(name, hand, character, remoteChannel);
				remoteChannel.sendHand(hand);
			} else {
				character = pickableCharacters.get(random.nextInt(pickableCharacters.size()));
				BasicAIPlayer rp = new BasicAIPlayer(hands.remove(0), character, new GameStateFacade(board, character, this));
				listeners.add(rp);
				player = rp;
			}
			players.add(player);
			playingAs.put(player, character);
			pickableCharacters.remove(character);

		}

		// announce to all listeners who is playing the game. This is done after the players are
		// created so all the AI players also get informed.
		for (int playerNumber=0;playerNumber<numberOfPlayers;playerNumber++){
			for (GameListener listener : listeners){
				listener.onCharacterJoinedGame(players.get(playerNumber).getName(), playingAs.get(players.get(playerNumber)), playerTypes.get(playerNumber));
			}
		}

	}

	private List<ServerGameChannel> waitForNetworkPlayers(int networkPlayers) throws IOException {
		List<ServerGameChannel> networkChannels = new ArrayList<ServerGameChannel>();
		if (networkPlayers > 0){
			for (int i=0;i<networkPlayers;i++){
				for (GameListener listener : listeners){
					listener.waitingForNetworkPlayers(networkPlayers-i);
				}

				ServerGameChannel chanel = networkPlayerHandler.getRemoteInput();
				networkChannels.add(chanel);
				listeners.add(chanel);
				
			}
		}
		return networkChannels;
	}


	/** Start the game loop, going through all players and asking for their
	 * moves until a player has won, or there is only one player left active.
	 * 
	 * This will fire events to all GameListeners as events occur. 
	 * 
	 */
	public void startGame(){
		ArrayList<Player> activePlayers = new ArrayList<Player>(players);
		while (true){
			Player player = players.get(turn++ % players.size());
			
			if (!activePlayers.contains(player)) continue; // skip players who have lost
			
			Character playersCharacter = playingAs.get(player);

			for (GameListener listener : listeners){
				listener.onTurnBegin(player.getName(), playersCharacter);
			}

			int dice1 = random.nextInt(6)+1;
			int dice2 = random.nextInt(6)+1;

			player.waitForDiceRollOK();

			for (GameListener listener : listeners){
				listener.onDiceRolled(dice1, dice2);
			}

			handlePlayerMove(player, playersCharacter, dice1+dice2);

			if(board.getLocationOf(playersCharacter) instanceof Room && player.hasSuggestion()){
				handleSuggestion(player, playersCharacter);
			}

			if (player.hasAccusation()){
				handleAccusation(player, playersCharacter, activePlayers);
			}
		}
	}

	private void handleAccusation(Player player, Character playersCharacter, List<Player> activePlayers) {
		Accusation accusation = player.getAccusation();
		boolean correct = accusation.equals(correctAccusation);
		for (GameListener listener : listeners){
			listener.onAccusation(playersCharacter, accusation, correct);
		}

		if (correct){
			for (GameListener listener : listeners){
				listener.onGameWon(player.getName(), playersCharacter);
			}
			activePlayers.clear();
		} else {
			activePlayers.remove(player);
			for (GameListener listener : listeners){
				listener.onLostGame(player.getName(), playersCharacter);
			}
			
			// if player is blocking the entrance to a room, move them in
			parentloop: for (Room r : board.getRooms()){
				for (Location l : r.getNeighbours()){
					if (l instanceof Tile && l.getTokens().contains(player)){
						board.moveCharacter(playersCharacter, r);
						for (GameListener listener : listeners){
							listener.onCharacterMove(playersCharacter, r);
						}
						break parentloop;
					}
				}
			}
			
			if (activePlayers.size() == 1){
				for (GameListener listener : listeners){
					listener.onGameWon(activePlayers.get(0).getName(), playingAs.get(activePlayers.get(0)));
				}
				activePlayers.clear();
			}
		}
	}

	private void handleSuggestion(Player player, Character playersCharacter) {
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
						listener.onSuggestionDisproved(playersCharacter, suggestion, room, playingAs.get(p));
					}
				}
				player.suggestionDisproved(suggestion, playingAs.get(p), disprovingCard);
				disproved = true;

				break;
			}
		}

		if (!disproved){
			for (GameListener listener : listeners){
				listener.onSuggestionUndisputed(playersCharacter, suggestion, room);
			}
		}
	}

	private void handlePlayerMove(Player player, Character playersCharacter, int rollTotal) {
		List<Location> possibleLocations = board.getPossibleDestinations(board.getLocationOf(playersCharacter), rollTotal, false);

		if (possibleLocations.size() == 0){
			// player trapped in the corridor
			possibleLocations = board.getPossibleDestinations(board.getLocationOf(playersCharacter), rollTotal, true);
		}
		
		Location destination = player.getDestination(possibleLocations);

		assert possibleLocations.contains(destination) : "assert "+ possibleLocations + ".contains(" + destination + ")";

		board.moveCharacter(playersCharacter, destination);

		for (GameListener listener : listeners){
			listener.onCharacterMove(playersCharacter, destination);
		}
	}

	private List<Player> getPlayersClockwiseOf(Player p) {
		List<Player> r = new ArrayList<Player>(players);
		Collections.rotate(r, players.indexOf(p));
		r.remove(p);
		return r;
	}

	/** Return a list of characters clockwise of the current character
	 * 
	 * @param character the character of the player
	 * @return the list of characters of the players clockwise of the player playing as <i>character</i>
	 */
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
