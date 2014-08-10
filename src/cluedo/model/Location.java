package cluedo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public abstract class Location {

	private List<Location> neighbours;

	public Location(){
		neighbours = new ArrayList<Location>();
	}

	public void addNeighbour(Location neighbour){
		neighbours.add(neighbour);
	}

	public List<Location> getNeighbours(){
		return Collections.<Location>unmodifiableList(neighbours);
	}

	public abstract boolean hasVacancy();

	public abstract void addToken(Token token);

	public abstract void removeToken(Token token);

	public abstract List<Token> getTokens();
}
