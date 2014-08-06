package cluedo.game.board;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import cluedo.game.board.Location.Direction;
import cluedo.util.json.JsonObject;

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
		// TODO: read from defs
		
		rooms = new LinkedHashSet<Room>();
		tiles = new LinkedHashSet<Tile>();
		board = new Location[24][25];
		

		
		
		
		//Create corridors
		for (int x = 0; x < 24; ++x){
			for (int y = 0; y < 25; ++y){
				if (boardLayout[y][x] == 1){
					board[x][y] = new Tile(x, y);
					tiles.add((Tile) board[x][y]);
				}
			}
		}
		for (int x = 0; x < 23; ++x){
			for (int y = 0; y < 25; ++y){
				if (board[x][y] != null && board[x+1][y] != null){
					board[x][y].addNeighbour(board[x+1][y], Direction.EAST);
					board[x+1][y].addNeighbour(board[x][y], Direction.WEST);
				}
			}
		}
		for (int y = 0; y < 24; ++y){
			for (int x = 0; x < 24; ++x){
				if (board[x][y] != null && board[x][y+1] != null){
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
