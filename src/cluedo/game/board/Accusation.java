package cluedo.game.board;

/**
 * 
 * @author Simon Pinfold
 *
 */
public class Accusation extends Suggestion {

	private Room room;

	public Accusation(Weapon weapon, Character character, Room room) {
		super(weapon, character);
		this.room = room;
	}

	public Room getRoom() {
		return room;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(this.getClass())) return false;
		Accusation other = (Accusation)obj;
		return this.weapon == other.weapon && this.character == other.character && this.room == other.room;
	}

}
