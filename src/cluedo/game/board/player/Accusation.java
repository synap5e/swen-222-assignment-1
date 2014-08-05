package cluedo.game.board.player;

import cluedo.game.board.CharacterToken;
import cluedo.game.board.Room;
import cluedo.game.board.WeaponToken;

public class Accusation extends Suggestion {

	private Room room;

	public Accusation(WeaponToken weapon, CharacterToken character, Room room) {
		super(weapon, character);
		this.room = room;
	}

	public Room getRoom() {
		return room;
	}

}
