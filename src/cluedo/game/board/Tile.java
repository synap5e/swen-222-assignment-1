package cluedo.game.board;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Tile extends Location{

	private Character occupient;
	private final int x;
	private  final int y;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean hasVacancy() {
		return occupient == null;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public void addToken(Token token) {
		if (occupient != null && token instanceof Character){
			occupient = (Character) token;
		}
	}

	@Override
	public void removeToken(Token token) {
		if (occupient == token){
			occupient = null;
		}
	}

	@Override
	public List<Token> getTokens() {
		List<Token> token = new ArrayList<Token>();
		if (occupient != null) token.add(occupient);
		return token;
	}
	
}
