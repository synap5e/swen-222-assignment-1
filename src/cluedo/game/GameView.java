package cluedo.game;

import java.util.List;
import java.util.Set;

import cluedo.game.board.Board;
import cluedo.game.board.Character;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Tile;
/**
 * 
 * @author Simon Pinfold
 *
 */
public class GameView {

	private Character playersToken;
	private Board board;

	public GameView(Board board, Character playersToken){
		this.board = board;
		this.playersToken = playersToken;
	}
	
	/** Gets a list of CharacterCards in the order that they will play, starting to the left of
	 * this player and going clockwise up to the player to our right
	 * 
	 * @return a list of CharacterCards for each of the players
	 */
	public List<Character> getPlayerOrder(){
		// TODO getPlayerOrder
		return null;
	}
	
	public Location getMyLocation(){
		return board.getLocationOf(playersToken);
	}
	
	public Set<Room> getRooms(){
		return board.getRooms();
	}
	
	public Set<Tile> getTiles(){
		return board.getTiles();
	}
	
}
