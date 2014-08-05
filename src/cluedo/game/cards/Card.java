package cluedo.game.cards;
/**
 * 
 * @author Simon Pinfold
 *
 */
public abstract class Card {
	
	protected String name;

	public Card(String name){
		this.name = name;
	}

	
	
	
	
	/* ----- Don't you love machine written code..... I don't */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
