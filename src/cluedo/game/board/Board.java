package cluedo.game.board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cluedo.game.board.Location.Direction;
import cluedo.util.json.JsonEntity;
import cluedo.util.json.JsonList;
import cluedo.util.json.JsonNumber;
import cluedo.util.json.JsonObject;
import cluedo.util.json.JsonString;

/**
 * 
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Board {
	private Set<Room> rooms;
	
	private Set<Character> characters;
	private Set<Weapon> weapons;
	
	private Set<Tile> tiles;
	
	private Location[][] board;
	
	private Random random = new Random();
	
	public Board(JsonObject defs){
		JsonList rows = (JsonList) defs.get("board");
		
		rooms = new LinkedHashSet<Room>();
		characters = new LinkedHashSet<Character>();
		weapons = new LinkedHashSet<Weapon>();
		
		tiles = new LinkedHashSet<Tile>();
		board = new Location[((JsonList)rows.get(0)).size()][rows.size()];

		JsonObject roomsDef = ((JsonObject) defs.get("rooms"));
		JsonObject weaponsDef = (JsonObject) defs.get("weapons");
		JsonObject characterDefs = (JsonObject) defs.get("characters");
		
		Map<Double, Room> roomKeys = new HashMap<Double, Room>();
		for (String name : roomsDef.keys()){
			JsonObject roomDef = (JsonObject) roomsDef.get(name);
			Double key = ((JsonNumber)roomDef.get("key")).value();
			Room room = new Room(name);
			rooms.add(room);
			roomKeys.put(key, room);
		}
		
		Map<Double, Token> startLocations = new HashMap<Double, Token>();
		for (String name : characterDefs.keys()){
			JsonObject characterDef = ((JsonObject) characterDefs.get(name));
			
			Character c = new Character(name);
			characters.add(c);
			startLocations.put(((JsonNumber)characterDef.get("start")).value(), c);
		}
		
		List<Double> roomKeyList = new ArrayList<Double>(roomKeys.keySet());
		for (String name : weaponsDef.keys()){
			JsonObject weaponDef = ((JsonObject) weaponsDef.get(name));
			
			Weapon w = new Weapon(name);
			weapons.add(w);
			startLocations.put(roomKeyList.get(random.nextInt(roomKeyList.size())), w);
		}

		createBoard(rows, roomsDef, roomKeys, startLocations);
		
	
	}

	private void createBoard(JsonList rows, JsonObject roomsDef, Map<Double, Room> roomKeys, Map<Double, Token> startLocations) {
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
					board[x][y].addToken(startLocations.remove(key));
				}
				++x;
			}
			++y;
		}
		for (int x = 0; x < getWidth()-1; ++x){
			for (y = 0; y < getHeight(); ++y){
				if (board[x][y] instanceof Tile && board[x+1][y] instanceof Tile){
					board[x][y].addNeighbour(board[x+1][y], Direction.EAST);
					board[x+1][y].addNeighbour(board[x][y], Direction.WEST);
				}
			}
		}
		for (y = 0; y < getHeight()-1; ++y){
			for (int x = 0; x < getWidth(); ++x){
				if (board[x][y] instanceof Tile && board[x][y+1] instanceof Tile){
					board[x][y].addNeighbour(board[x][y+1], Direction.SOUTH);
					board[x][y+1].addNeighbour(board[x][y], Direction.NORTH);
				}
			}
		}
	}

	public Set<Room> getRooms(){
		return Collections.<Room>unmodifiableSet(rooms);
	}
	
	public Set<Tile> getTiles(){
		return Collections.<Tile>unmodifiableSet(tiles);
	}

	public Set<Character> getCharacters() {
		return Collections.<Character>unmodifiableSet(characters);
	}

	public Set<Weapon> getWeapons() {
		return Collections.<Weapon>unmodifiableSet(weapons);
	}
	
	
	public Location getLocationOf(Character character) {
		// TODO
		return null;
	}
	
	
	private int getWidth() {
		return board.length;
	}
	
	private int getHeight() {
		return board[0].length;
	}

	public Location getLocation(int x, int y){
		if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;
		return board[x][y];
	}
}
