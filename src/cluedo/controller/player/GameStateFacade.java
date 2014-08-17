package cluedo.controller.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import cluedo.controller.GameMaster;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;
/** This class defines a player-specific view into the game state, combining 
 * reading from the board and the GameMaster and using the specific character 
 * of the player.
 * 
 * This allows an AI player to read game state without keeping a reference to 
 * the board and game master, and serves to simplify (and restrict) access to 
 * the state.
 * 
 * @author Simon Pinfold
 *
 */
public class GameStateFacade {

	private Character playersToken;
	private Board board;
	private GameMaster gameMaster;

	// method names document themselves
	
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
	
	public List<Room> getRooms(){
		return board.getRooms();
	}
	
	public List<Tile> getTiles(){
		return board.getTiles();
	}

	public List<Weapon> getWeapons() {
		return board.getWeapons();
	}

	public List<Character> getCharacters() {
		return board.getCharacters();
	}

	public Location getClosestLocation(List<Location> possibleLocations, final Room room) {
		ArrayList<Location> possibleLocationsClone = new ArrayList<Location>(possibleLocations);
		Collections.sort(possibleLocationsClone, new Comparator<Location>(){

			@Override
			public int compare(Location o1, Location o2) {
				return board.distanceBetween(o1, room) - board.distanceBetween(o2, room);
			}
			
		});
		return possibleLocationsClone.get(0);
	}

	public int distanceBetween(Location o1, Location o2) {
		return board.distanceBetween(o1, o2);
	}
	
}
