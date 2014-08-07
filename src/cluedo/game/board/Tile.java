package cluedo.game.board;


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

	@Override
	public void addToken(Token token) {
		// TODO Auto-generated method stub
		
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
}
