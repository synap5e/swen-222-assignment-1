package cluedo.game.board;

/**
 * 
 * @author James Greenwood-Thessman
 *
 */
public class Token implements Card{

	protected String name;

	public Token(String name){
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
