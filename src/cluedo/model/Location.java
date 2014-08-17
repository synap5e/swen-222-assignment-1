package cluedo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cluedo.model.card.Token;

/**
 * Represents a location on the board.
 * A location has neighbours which a character can move to.
 * 
 * @author James Greenwood-Thessman
 *
 */
public abstract class Location {

	/**
	 * The list of neighbouring locations.
	 */
	private List<Location> neighbours;

	/**
	 * Create a location.
	 */
	public Location(){
		neighbours = new ArrayList<Location>();
	}

	/**
	 * Add a neighbouring location.
	 * 
	 * @param neighbour the location to make a neighbour
	 */
	public void addNeighbour(Location neighbour){
		neighbours.add(neighbour);
	}

	/**
	 * Get the list of neighbouring locations.
	 * 
	 * @return an unmodifiable list of the neighbours
	 */
	public List<Location> getNeighbours(){
		return Collections.<Location>unmodifiableList(neighbours);
	}

	/**
	 * Whether the location is able to be moved to.
	 * 
	 * @return whether the location has a vacancy
	 */
	public abstract boolean hasVacancy();

	/**
	 * Add the given token to the location.
	 * 
	 * @param token the token to add
	 */
	public abstract void addToken(Token token);

	/**
	 * Remove the given token from the location.
	 * 
	 * @param token the token to remove
	 */
	public abstract void removeToken(Token token);

	/**
	 * Get the list of tokens at this location.
	 * 
	 * @return the tokens at this location
	 */
	public abstract List<Token> getTokens();
}
