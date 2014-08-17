package cluedo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Token;
import cluedo.model.card.Weapon;
import util.json.JsonEntity;
import util.json.JsonList;
import util.json.JsonNumber;
import util.json.JsonObject;
import util.json.JsonString;

/**
 * Represents the board in Cluedo. 
 * The board is composed of Rooms and Tiles. 
 * The board also keeps track of where tokens are on the board.
 *
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Board {
	
	/**
	 * The list of rooms
	 */
	private List<Room> rooms;
	
	/**
	 * The list of tiles
	 */
	private List<Tile> tiles;

	/**
	 * The grid version of the board
	 */
	private Location[][] board;

	/**
	 * The list of characters on the board
	 */
	private List<Character> characters;
	
	/**
	 * The list of weapons on the board
	 */
	private List<Weapon> weapons;

	/**
	 * The map from tokens and their location on the board
	 */
	private Map<Token, Location> tokenLocations;

	/**
	 * The cards mapped by name
	 */
	private Map<String, Card> cardsByName;

	/**
	 * Create a board from the given definitions
	 * 
	 * @param defs the definition of the board
	 * @param weaponLocationDefs the definition of weapon locations
	 */
	public Board(JsonObject defs, JsonObject weaponLocationDefs) {
		//Get the rows of the board
		JsonList rows = (JsonList) defs.get("board");

		//Create the maps and lists
		rooms = new ArrayList<Room>();
		characters = new ArrayList<Character>();
		weapons = new ArrayList<Weapon>();
		tokenLocations = new HashMap<Token, Location>();
		cardsByName = new HashMap<String, Card>();

		tiles = new ArrayList<Tile>();
		
		//Create the board array
		board = new Location[((JsonList)rows.get(0)).size()][rows.size()];

		//Get the different definitions
		JsonObject roomsDef = ((JsonObject) defs.get("rooms"));
		JsonObject weaponsDef = (JsonObject) defs.get("weapons");
		JsonObject characterDefs = (JsonObject) defs.get("characters");
		JsonList doorwayDefs = (JsonList) defs.get("doorways");

		//Create the rooms
		Map<Double, Room> kestToRooms = new HashMap<Double, Room>();
		Map<Room, Double> roomsToKeys = new HashMap<Room, Double>();
		Map<String, Room> roomNames = new HashMap<String, Room>();
		for (String name : roomsDef.keys()){
			JsonObject roomDef = (JsonObject) roomsDef.get(name);
			Double key = ((JsonNumber)roomDef.get("key")).value();
			Room room = new Room(name);
			rooms.add(room);
			kestToRooms.put(key, room);
			roomsToKeys.put(room, key);
			roomNames.put(name, room);
			cardsByName.put(name, room);
		}

		//Create the characters and mark where they start
		Map<Double, Token> startLocations = new HashMap<Double, Token>();
		for (String name : characterDefs.keys()){
			JsonObject characterDef = ((JsonObject) characterDefs.get(name));

			Character c = new Character(name);
			characters.add(c);
			startLocations.put(((JsonNumber)characterDef.get("start")).value(), c);
			cardsByName.put(name, c);
		}

		//Create the weapons and put in their starting rooms (or random rooms if no starting rooms are specified)
		Random random = new Random();
		List<Double> roomKeyList = new ArrayList<Double>(kestToRooms.keySet());
		for (String name : weaponsDef.keys()){
			//Create the weapon
			Weapon w = new Weapon(name);
			weapons.add(w);
			cardsByName.put(name, w);

			if (weaponLocationDefs == null){
				// random location
				startLocations.put(roomKeyList.remove(random.nextInt(roomKeyList.size())), w);
			} else {
				// defined location by weaponLocationDefs = { ... "<weapon name>" : "<room name>", ... }
				String roomName = ((JsonString) weaponLocationDefs.get(name)).value();
				Room room = roomNames.get(roomName);
				double roomKey = roomsToKeys.get(room);
				startLocations.put(roomKey, w);
			}
		}

		//Create the doorways
		Map<Double, Room> doorways = new HashMap<Double, Room>();
		for (JsonEntity doorway : doorwayDefs){
			JsonList doorwayDef = (JsonList) doorway;

			Room room = roomNames.get(((JsonString)doorwayDef.get(1)).value());
			doorways.put(((JsonNumber)doorwayDef.get(0)).value(), room);
		}

		//Create the board
		createBoard(rows, roomsDef, kestToRooms, startLocations, doorways);
		
		//Create the secret passages between rooms
		for (JsonEntity je : (JsonList)defs.get("passages")){
			JsonList tuple = (JsonList) je;
			JsonString r1 = (JsonString) tuple.get(0);
			JsonString r2 = (JsonString) tuple.get(1);
			
			String n1 = r1.value();
			String n2 = r2.value();
			
			roomNames.get(n1).addNeighbour(roomNames.get(n2));
			roomNames.get(n2).addNeighbour(roomNames.get(n1));
		}
	}

	/**
	 * Create a board from the given definition
	 * 
	 * @param defs the definition of the board
	 */
	public Board(JsonObject defs){
		this(defs, null);
	}

	/**
	 * Create the board.
	 * 
	 * @param rows the rows of the board
	 * @param roomsDef the room definitions
	 * @param roomKeys the map from keys to rooms
	 * @param startLocations the start locations of the characters
	 * @param doorways the doorways between the corridor and the rooms
	 */
	private void createBoard(JsonList rows, JsonObject roomsDef, Map<Double, Room> roomKeys,
							Map<Double, Token> startLocations, Map<Double, Room> doorways) {
		//Add the locations to the table
		int y = 0;
		for (JsonEntity row : rows){
			int x = 0;
			for (JsonEntity tile : (JsonList) row){
				double key = ((JsonNumber)tile).value();
				//If its a wall do nothing
				if (key == 0){
				//If its a room
				} else if (roomKeys.containsKey(key)){
					//Add the room to that location
					board[x][y] = roomKeys.get(key);
				} else {
					//Otherwise create a tile at that locations
					Tile t = new Tile(x, y);
					board[x][y] = t;
					tiles.add(t);
				}
				//If the tile is a start location
				if (startLocations.containsKey(key)){
					//put the token in that start location
					Token tok = startLocations.remove(key);
					board[x][y].addToken(tok);
					tokenLocations.put(tok, board[x][y]);
				}
				++x;
			}
			++y;
		}

		//Add the doorways
		y = 0;
		for (JsonEntity row : rows){
			int x = 0;
			for (JsonEntity tile : (JsonList) row){
				//Find the key of the tile
				double key = ((JsonNumber)tile).value();

				//If the doorway add make them neighbours
				if (doorways.containsKey(key)){
					board[x][y].addNeighbour(doorways.get(key));
					doorways.get(key).addNeighbour(board[x][y]);
				}
				++x;
			}
			++y;
		}

		//Make every every tile that are horizontally next to each other neighbours
		for (int x = 0; x < getWidth()-1; ++x){
			for (y = 0; y < getHeight(); ++y){
				if (board[x][y] instanceof Tile && board[x+1][y] instanceof Tile){
					board[x][y].addNeighbour(board[x+1][y]);
					board[x+1][y].addNeighbour(board[x][y]);
				}
			}
		}
		//Make every every tile that are vertically next to each other neighbours
		for (y = 0; y < getHeight()-1; ++y){
			for (int x = 0; x < getWidth(); ++x){
				if (board[x][y] instanceof Tile && board[x][y+1] instanceof Tile){
					board[x][y].addNeighbour(board[x][y+1]);
					board[x][y+1].addNeighbour(board[x][y]);
				}
			}
		}
	}

	/**
	 * Get the rooms of the board
	 * 
	 * @return the rooms
	 */
	public ArrayList<Room> getRooms(){
		return new ArrayList<Room>(rooms);
	}

	/**
	 * Get the tiles of the board
	 * 
	 * @return the tiles
	 */
	public ArrayList<Tile> getTiles(){
		return new ArrayList<Tile>(tiles);
	}

	/**
	 * Get the characters on the board
	 * 
	 * @return the characters
	 */
	public ArrayList<Character> getCharacters() {
		return new ArrayList<Character>(characters);
	}

	/**
	 * Get the weapons on the board
	 * 
	 * @return the weapons
	 */
	public ArrayList<Weapon> getWeapons() {
		return new ArrayList<Weapon>(weapons);
	}

	/**
	 * Get the width of the board
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return board.length;
	}

	/**
	 * Get the height of the board
	 * 
	 * @return the height
	 */
	public int getHeight() {
		return board[0].length;
	}

	/**
	 * Get the location at that point on the board
	 * 
	 * @param x the x coordinate in the grid
	 * @param y the y coordinate in the grid
	 * 
	 * @return the location at that coordinate (null if there is no such location)
	 */
	public Location getLocation(int x, int y){
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;
		return board[x][y];
	}

	/**
	 * Get the locations that is able to be moved to
	 * 
	 * @param start the start location
	 * @param allowedMoves how far the move can be
	 * @param allowMoveThroughPlayers whether characters can be moved through
	 * @return
	 */
	public List<Location> getPossibleDestinations(Location start, int allowedMoves, boolean allowMoveThroughPlayers) {
		List<Location> allowed = new ArrayList<Location>();
		
		//If the start location is a room, then the player can stay there
		if (start instanceof Room) allowed.add(start);

		Queue<Location> q = new LinkedList<Location>();
		Map<Location, Integer> depths = new HashMap<Location, Integer>();
		
		//Start breadth first search
		q.offer(start);
		depths.put(start, 0);
		while (!q.isEmpty()){
			Location l = q.poll();
			int depth = depths.get(l);
			if ((l instanceof Room && l != start) || depth == allowedMoves){
				allowed.add(l);
			} else {
				for (Location n : l.getNeighbours()){
					if (!depths.containsKey(n) && (n instanceof Room || allowMoveThroughPlayers || ((Tile)n).getTokens().size() == 0)){
						q.offer(n);
						depths.put(n, depth+1);
					}
				}
			}
		}

		return allowed;
	}
	
	/**
	 * Get the distance between two locations
	 * @param l1 the first location
	 * @param l2 the second location
	 * @return the distance between the two locations
	 */
	public int distanceBetween(Location l1, Location l2) {
		Queue<Location> q = new LinkedList<Location>();
		Map<Location, Integer> depths = new HashMap<Location, Integer>();
		
		q.offer(l1);
		depths.put(l1, 0);
		while (!q.isEmpty()){
			Location l = q.poll();
			int depth = depths.get(l);
			if (l == l2) return depth;
			
			for (Location n : l.getNeighbours()){
				if (!depths.containsKey(n)){
					q.offer(n);
					depths.put(n, depth+1);
				}
			}
		}
		return -1;
	}

	/**
	 * Get the location where the token is
	 * 
	 * @param t the token
	 * @return the location containing the token
	 */
	public Location getLocationOf(Token t) {
		return tokenLocations.get(t);
	}

	/**
	 * Moves a character to another location
	 * @param playersCharacter the character to move
	 * @param dest the destination to move to
	 */
	public void moveCharacter(Character playersCharacter, Location dest) {
		Location start = tokenLocations.get(playersCharacter);
		start.removeToken(playersCharacter);

		dest.addToken(playersCharacter);
		tokenLocations.put(playersCharacter, dest);
	}

	/**
	 * Moves a weapon to another location
	 * @param weapon the weapon to move
	 * @param dest the destination to move to
	 */
	public void moveWeapon(Weapon weapon, Location dest) {
		Location start = tokenLocations.get(weapon);
		start.removeToken(weapon);

		dest.addToken(weapon);
		tokenLocations.put(weapon, dest);
	}

	/**
	 * Get the card with the given name
	 * 
	 * @param name the name of the card
	 * @return the card specified
	 */
	public Card getCard(String name) {
		return cardsByName.get(name);
	}

}
