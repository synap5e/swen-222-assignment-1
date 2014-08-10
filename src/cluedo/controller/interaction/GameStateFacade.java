package cluedo.controller.interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cluedo.controller.GameMaster;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
/** NOTE: instance-specific to each player using an instance (generally will be AI players)
 * 
 * @author Simon Pinfold
 *
 */
public class GameStateFacade {

	private Character playersToken;
	private Board board;
	private GameMaster gameMaster;

	public GameStateFacade(Board board, Character playersToken, GameMaster gameMaster){
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
