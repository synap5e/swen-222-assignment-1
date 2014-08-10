package cluedo.game;

import java.util.ArrayList;
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
	private GameMaster gameMaster;

	public GameView(Board board, Character playersToken, GameMaster gameMaster){
		this.board = board;
		this.playersToken = playersToken;
		this.gameMaster = gameMaster;
	}
	
	public List<Character> getPlayersClockwiseOfMe(){
		return gameMaster.getCharactersClockwiseOf(playersToken);
	}
	
	public Location getMyLocation(){
		return board.getLocationOf(playersToken);
	}
	
	public ArrayList<Room> getRooms(){
		return board.getRooms();
	}
	
	public ArrayList<Tile> getTiles(){
		return board.getTiles();
	}
	
}
