package cluedo.game.cards;

public class Envelope {
	
	private WeaponCard murderWeapon;
	private RoomCard sceneOfTheCrime;
	private CharacterCard murderer;
	
	public Envelope(WeaponCard weapon, RoomCard room, CharacterCard character) {
		this.murderWeapon = weapon;
		this.sceneOfTheCrime = room;
		this.murderer = character;
	}
	
	public WeaponCard getMurderWeapon() {
		return murderWeapon;
	}

	public RoomCard getSceneOfTheCrime() {
		return sceneOfTheCrime;
	}

	public CharacterCard getMurderer() {
		return murderer;
	}

}
