package cluedo.game.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class Location {
	
	public enum Direction {NORTH, SOUTH, EAST, WEST}

	private HashMap<Location, Direction> neighbours;
	
	public Location(){
		neighbours = new HashMap<Location, Direction>();
	}
	
	public void addNeighbour(Location neighbour, Direction dir){
		neighbours.put(neighbour, dir);
	}
	
	public Set<Location> getNeighbours(){
		return Collections.<Location>unmodifiableSet(neighbours.keySet());
	}
	
	public Direction neighboursDirection(Location loc){
		return neighbours.get(loc);
	}
	
	public abstract boolean hasVacancy();

	public abstract void addToken(Token token);
	
	public abstract void removeToken(Token token);
	
	public abstract List<Token> getTokens();
}
