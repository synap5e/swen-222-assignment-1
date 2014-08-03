package cluedo.game.cards;

import java.util.ArrayList;
import java.util.List;

public class Hand {
	
	private List<Card> cards;
	
	public Hand() {
		this.cards = new ArrayList<Card>();
	}
	
	public void addCard(Card c){
		this.cards.add(c);
	}
	
	

}
