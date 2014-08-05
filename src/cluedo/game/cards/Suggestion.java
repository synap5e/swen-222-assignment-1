package cluedo.game.cards;

import cluedo.game.board.CharacterToken;
import cluedo.game.board.WeaponToken;

public class Suggestion {

	protected WeaponToken weapon;
	protected CharacterToken character;

	public Suggestion(WeaponToken weapon, CharacterToken character) {
		this.weapon = weapon;
		this.character = character;
	}

	public WeaponToken getWeapon() {
		return weapon;
	}

	public CharacterToken getCharacter() {
		return character;
	}


}
