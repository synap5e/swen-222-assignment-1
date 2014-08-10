package cluedo.model;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Tile extends Location{

	private Character occupant;
	private final int x;
	private  final int y;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean hasVacancy() {
		return occupant == null;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public void addToken(Token token) {
		// TODO: if full error
		if (token instanceof Character){
			occupant = (Character) token;
		}
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
	
}
