package cluedo.game.board;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author Simon Pinfold
 *
 */
public class Hand {

	private List<Card> cards;

	public Hand() {
		this.cards = new ArrayList<Card>();
	}

	public void addCard(Card c){
		this.cards.add(c);
	}

	public boolean hasCard(Card c) {
		// TODO Auto-generated method stub
		return false;
	}



}
