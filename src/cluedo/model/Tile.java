package cluedo.model;

import java.util.ArrayList;
import java.util.List;

import cluedo.model.card.Character;
import cluedo.model.card.Token;


/**
 * Represents a tile in the corridor.
 * 
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Tile extends Location{

	/**
	 * The character currently occupying the tile
	 */
	private Character occupant;
	
	/**
	 * The x location in the grid
	 */
	private final int x;
	
	/**
	 * The y location in the grid
	 */
	private  final int y;
	
	/**
	 * Create a tile at the given position in the grid
	 * @param x the x location in the grid
	 * @param y the y location in the grid
	 */
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean hasVacancy() {
		return occupant == null;
	}
	
	/**
	 * Get the x location in the grid
	 * 
	 * @return the x location
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get the y location in the grid
	 * 
	 * @return the y location
	 */
	public int getY() {
		return y;
	}
	
	@Override
	public void addToken(Token token) {
		if (occupant != null){
			throw new IllegalStateException("Cannot add a token to a tile that is already full");
		}
		if (!(token instanceof Character)){
			throw new IllegalArgumentException("Cannot add a non-character token to a tile");
		}
		occupant = (Character) token;
	}

	@Override
	public void removeToken(Token token) {
		if (occupant == token){
			occupant = null;
		}
	}

	@Override
	public List<Token> getTokens() {
		List<Token> token = new ArrayList<Token>();
		if (occupant != null) token.add(occupant);
		return token;
	}
	
	@Override
	public String toString() {
		return "Tile(" + x + ", " + y + ")";
	}
	
}
