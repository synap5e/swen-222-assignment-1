package cluedo.model.card;


/**
 * 
 * @author James Greenwood-Thessman
 *
 */
public abstract class Token implements Card{

	protected String name;

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
