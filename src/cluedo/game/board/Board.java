package cluedo.game.board;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
	private Set<Tile> tiles;
	
	private Location[][] board;
	
	public Board(JsonObject defs){
		JsonObject roomsOb = ((JsonObject) defs.get("rooms"));
		JsonList rows = (JsonList) defs.get("board");
		
		rooms = new LinkedHashSet<Room>();
		tiles = new LinkedHashSet<Tile>();
		board = new Location[((JsonList)rows.get(0)).size()][rows.size()];

		Map<Double, Room> keys = new HashMap<Double, Room>();
		
		for (String name : roomsOb.keys()){
			JsonObject roomOb = (JsonObject) roomsOb.get(name);
			Double key = ((JsonNumber)roomOb.get("key")).value();
			Room room = new Room(name);
			rooms.add(room);
			keys.put(key, room);
		}
		
		
		int y = 0;
		for (JsonEntity row : rows){
			int x = 0;
			for (JsonEntity tile : (JsonList) row){
				double key = ((JsonNumber)tile).value();
				if (key == 0){
					// empty
				} else if (keys.containsKey(key)){
					board[x][y] = keys.get(key);
				} else {
					Tile t = new Tile(x, y);
					board[x][y] = t;
					tiles.add(t);
				}
				++x;
			}
			++y;
		}
		for (int x = 0; x < 23; ++x){
			for (y = 0; y < 25; ++y){
				if (board[x][y] instanceof Tile && board[x+1][y] instanceof Tile){
					board[x][y].addNeighbour(board[x+1][y], Direction.EAST);
					board[x+1][y].addNeighbour(board[x][y], Direction.WEST);
				}
			}
		}
		for (y = 0; y < 24; ++y){
			for (int x = 0; x < 24; ++x){
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
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Weapon> getWeapons() {
		// TODO Auto-generated method stub
		return null;
	}

	public Location getLocation(Character character) {
		// TODO Auto-generated method stub
		return null;
	}
}
