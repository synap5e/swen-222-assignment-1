package cluedo.game.board;


/**
 * 
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Tile extends Location{

	private Character occupient;
	public final int x;
	public final int y;
	
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
	
	// TODO occupient
}
