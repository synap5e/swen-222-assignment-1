package cluedo.model.cardcollection;

import cluedo.model.card.Character;
import cluedo.model.card.Weapon;

/**
 * Represents a suggestion in Cluedo. A suggestion consists of a weapon and a character.
 * 
 * @author Simon Pinfold
 *
 */
public class Suggestion {

	/**
	 * The suggested murder weapon
	 */
	protected Weapon weapon;
	
	/**
	 * The suggested murderer
	 */
	protected Character character;

	/**
	 * Create a suggestion suggesting the given weapon and character
	 * 
	 * @param weapon the suggested weapon
	 * @param character the suggested
	 */
	public Suggestion(Weapon weapon, Character character) {
		this.weapon = weapon;
		this.character = character;
	}

	/**
	 * Get the suggested weapon
	 * 
	 * @return the weapon
	 */
	public Weapon getWeapon() {
		return weapon;
	}

	/**
	 * Get the suggested murderer
	 * 
	 * @return the character suggested
	 */
	public Character getCharacter() {
		return character;
	}


}
