package cluedo.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

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
 *
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Board {
	private List<Room> rooms;
	private List<Tile> tiles;

	private Location[][] board;

	private List<Character> characters;
	private List<Weapon> weapons;

	private Map<Token, Location> tokenLocations;

	private Random random = new Random();

	private Map<String, Card> cardsByName;

	public Board(JsonObject defs, JsonObject weaponLocationDefs) {
		JsonList rows = (JsonList) defs.get("board");

		rooms = new ArrayList<Room>();
		characters = new ArrayList<Character>();
		weapons = new ArrayList<Weapon>();
		tokenLocations = new HashMap<Token, Location>();
		cardsByName = new HashMap<String, Card>();

		tiles = new ArrayList<Tile>();
		board = new Location[((JsonList)rows.get(0)).size()][rows.size()];

		JsonObject roomsDef = ((JsonObject) defs.get("rooms"));
		JsonObject weaponsDef = (JsonObject) defs.get("weapons");
		JsonObject characterDefs = (JsonObject) defs.get("characters");
		JsonList doorwayDefs = (JsonList) defs.get("doorways");

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

		Map<Double, Token> startLocations = new HashMap<Double, Token>();
		for (String name : characterDefs.keys()){
			JsonObject characterDef = ((JsonObject) characterDefs.get(name));

			Character c = new Character(name);
			characters.add(c);
			startLocations.put(((JsonNumber)characterDef.get("start")).value(), c);
			cardsByName.put(name, c);
		}

		List<Double> roomKeyList = new ArrayList<Double>(kestToRooms.keySet());
		for (String name : weaponsDef.keys()){
			JsonObject weaponDef = ((JsonObject) weaponsDef.get(name));

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
		
		

		Map<Double, Room> doorways = new HashMap<Double, Room>();
		for (JsonEntity doorway : doorwayDefs){
			JsonList doorwayDef = (JsonList) doorway;

			Room room = roomNames.get(((JsonString)doorwayDef.get(1)).value());
			doorways.put(((JsonNumber)doorwayDef.get(0)).value(), room);
		}

		createBoard(rows, roomsDef, kestToRooms, startLocations, doorways);
	}
	
	public Board(JsonObject defs){
		this(defs, null);
	}

	private void createBoard(	JsonList rows, JsonObject roomsDef, Map<Double, Room> roomKeys,
								Map<Double, Token> startLocations, Map<Double, Room> doorways) {
		int y = 0;
		for (JsonEntity row : rows){
			int x = 0;
			for (JsonEntity tile : (JsonList) row){
				double key = ((JsonNumber)tile).value();
				if (key == 0){
					// wall
				} else if (roomKeys.containsKey(key)){
					board[x][y] = roomKeys.get(key);
				} else {
					Tile t = new Tile(x, y);
					board[x][y] = t;
					tiles.add(t);
				}

				if (startLocations.containsKey(key)){
					Token tok = startLocations.remove(key);
					board[x][y].addToken(tok);
					tokenLocations.put(tok, board[x][y]);
				}
				++x;
			}
			++y;
		}

		y = 0;
		for (JsonEntity row : rows){
			int x = 0;
			for (JsonEntity tile : (JsonList) row){
				double key = ((JsonNumber)tile).value();

				if (doorways.containsKey(key)){
					board[x][y].addNeighbour(doorways.get(key));
					doorways.get(key).addNeighbour(board[x][y]);
				}
				++x;
			}
			++y;
		}

		for (int x = 0; x < getWidth()-1; ++x){
			for (y = 0; y < getHeight(); ++y){
				if (board[x][y] instanceof Tile && board[x+1][y] instanceof Tile){
					board[x][y].addNeighbour(board[x+1][y]);
					board[x+1][y].addNeighbour(board[x][y]);
				}
			}
		}
		for (y = 0; y < getHeight()-1; ++y){
			for (int x = 0; x < getWidth(); ++x){
				if (board[x][y] instanceof Tile && board[x][y+1] instanceof Tile){
					board[x][y].addNeighbour(board[x][y+1]);
					board[x][y+1].addNeighbour(board[x][y]);
				}
			}
		}
	}

	public ArrayList<Room> getRooms(){
		return new ArrayList<Room>(rooms);
	}

	public ArrayList<Tile> getTiles(){
		return new ArrayList<Tile>(tiles);
	}

	public ArrayList<Character> getCharacters() {
		return new ArrayList<Character>(characters);
	}

	public ArrayList<Weapon> getWeapons() {
		return new ArrayList<Weapon>(weapons);
	}


	public int getWidth() {
		return board.length;
	}

	public int getHeight() {
		return board[0].length;
	}

	public Location getLocation(int x, int y){
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;
		return board[x][y];
	}


	public List<Location> getPossibleDestinations(Location start, int allowedMoves) {
		List<Location> allowed = new ArrayList<Location>();

		Queue<Location> q = new LinkedList<Location>();
		Map<Location, Integer> depths = new HashMap<Location, Integer>();

		q.offer(start);
		depths.put(start, 0);
		while (!q.isEmpty()){
			Location l = q.poll();
			int depth = depths.get(l);
			if ((l instanceof Room && l != start) || depth == allowedMoves){
				allowed.add(l);
			} else {
				for (Location n : l.getNeighbours()){
					if (!depths.containsKey(n)){
						q.offer(n);
						depths.put(n, depth+1);
					}
				}
			}
		}

		return allowed;
	}


	public Location getLocationOf(Token t) {
		return tokenLocations.get(t);
	}

	public void moveCharacter(Character playersCharacter, Location dest) {
		Location start = tokenLocations.get(playersCharacter);
		start.removeToken(playersCharacter);

		dest.addToken(playersCharacter);
		tokenLocations.put(playersCharacter, dest);
	}

	public void moveWeapon(Weapon weapon, Location location) {
		// TODO Auto-generated method stub

	}

	public Card getCard(String name) {
		return cardsByName.get(name);
	}

}
