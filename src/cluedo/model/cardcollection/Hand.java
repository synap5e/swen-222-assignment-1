package cluedo.model.cardcollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import cluedo.model.card.Card;

/**
 * Represents the hand of a player
 * 
 * @author Simon Pinfold
 *
 */
public class Hand implements Iterable<Card> {

	/**
	 * The list of cards in the hand
	 */
	private List<Card> cards;

	/**
	 * Create an empty hand
	 */
	public Hand() {
		this.cards = new ArrayList<Card>();
	}

	/**
	 * Add a card to the hand
	 * 
	 * @param c the card to add
	 */
	public void addCard(Card c){
		this.cards.add(c);
	}

	/**
	 * Check whether the card is contained in the hand
	 * 
	 * @param c the card to check for
	 * @return whether the hand contains the card
	 */
	public boolean hasCard(Card c) {
		return cards.contains(c);
	}

	@Override
	public Iterator<Card> iterator() {
		return cards.iterator();
	}
	
	@Override
	public String toString() {
		return cards.toString();
	}



}
