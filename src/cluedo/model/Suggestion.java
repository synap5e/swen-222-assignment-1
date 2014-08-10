package cluedo.model;

/**
 * 
 * @author Simon Pinfold
 *
 */
public class Suggestion {

	protected Weapon weapon;
	protected Character character;

	public Suggestion(Weapon weapon, Character character) {
		this.weapon = weapon;
		this.character = character;
	}

	public Weapon getWeapon() {
		return weapon;
	}

	public Character getCharacter() {
		return character;
	}


}
