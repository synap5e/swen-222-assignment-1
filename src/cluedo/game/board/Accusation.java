package cluedo.game.board;


public class Accusation extends Suggestion {

	private Room room;

	public Accusation(Weapon weapon, Character character, Room room) {
		super(weapon, character);
		this.room = room;
	}

	public Room getRoom() {
		return room;
	}

}