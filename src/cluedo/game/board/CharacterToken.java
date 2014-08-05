package cluedo.game.board;

import cluedo.game.cards.CharacterCard;

public class CharacterToken extends Token{

	private CharacterCard card;

	public CharacterToken(String name, CharacterCard card){
		super(name);
		this.card = card;
	}

	public CharacterCard getCard() {
		return card;
	}

}
