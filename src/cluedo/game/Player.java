package cluedo.game;

import cluedo.game.board.CharacterToken;
import cluedo.game.board.player.BoardPlayer;
import cluedo.game.cards.Card;
import cluedo.game.cards.CardPlayer;
import cluedo.game.cards.CharacterCard;
import cluedo.game.cards.Hand;

public abstract class Player implements CardPlayer, BoardPlayer{

	private Hand hand;
	private CharacterToken character;

	public Player(Hand h, CharacterToken character){
		hand = h;
		this.character = character;
	}

	@Override
	public CharacterToken getToken() {
		return character;
	}

	public boolean hasCard(Card c){
		return hand.hasCard(c);
	}

}
