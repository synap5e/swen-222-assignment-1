package cluedo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cluedo.model.Board;
import cluedo.model.Character;
import cluedo.model.Location;
import cluedo.model.Room;
import cluedo.model.Tile;
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
