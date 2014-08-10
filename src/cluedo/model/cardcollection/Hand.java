package cluedo.model.cardcollection;

import java.util.ArrayList;
import java.util.List;

import cluedo.model.card.Card;
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
		return cards.contains(c);
	}



}
