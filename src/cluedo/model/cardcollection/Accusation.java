package cluedo.model.cardcollection;

import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Weapon;

/**
 * Represents an accusation in Cluedo. An accusation consists of a weapon, character and room.
 * 
 * @author Simon Pinfold
 *
 */
public class Accusation extends Suggestion {

	/**
	 * The room believed to be the murder scene
	 */
	private Room room;

	/**
	 * Create an accusation consisting of the given parameters.
	 * 
	 * @param weapon the believed murder weapon
	 * @param character the believed murderer
	 * @param room the believed murder scene
	 */
	public Accusation(Weapon weapon, Character character, Room room) {
		super(weapon, character);
		this.room = room;
	}

	/**
	 * Get the room
	 * 
	 * @return the room
	 */
	public Room getRoom() {
		return room;
	}
	
	@Override
	public boolean equals(Object obj) {
		//Check the object comparing to is an accusation
		if (obj == null || !obj.getClass().equals(this.getClass())) return false;
		Accusation other = (Accusation)obj;
		
		//Check accusations are equal
		return this.weapon == other.weapon && this.character == other.character && this.room == other.room;
	}
	
	@Override
	public String toString() {
		return "{ " + weapon +", " + character + ", " + room + "}";
	}

}
