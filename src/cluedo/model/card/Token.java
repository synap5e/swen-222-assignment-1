package cluedo.model.card;

/**
 * Represents a token in Cluedo and the card that represents that token. 
 * All subclasses are able to be placed on the board.
 * 
 * @author James Greenwood-Thessman
 *
 */
public abstract class Token implements Card{

	/**
	 * The name of the token
	 */
	protected String name;

	/**
	 * Create the token with the given name
	 * 
	 * @param name the name of the token
	 */
	public Token(String name){
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
