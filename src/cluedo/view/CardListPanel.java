package cluedo.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import cluedo.model.card.Card;

/**
 * CardListPanel displays and allows selection of a list of cards.
 *
 * @author James Greenwood-Thessman
 *
 */
public class CardListPanel extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	
	/**
	 * The width of the cards
	 */
	private static final int CARD_WIDTH = 100;
	/**
	 * The height of the cards
	 */
	private static final int CARD_HEIGHT = 150;
	
	/**
	 * The colour of the background
	 */
	private static final Color BACKGROUND = new Color(212,196,173);
	
	/**
	 * The images of the cards
	 */
	private final Map<String, Image> cardImages;
	
	/**
	 * The list of cards to display in the panel
	 */
	private List<Card> cards;
	
	/**
	 * The listeners listening for when cards are selected
	 */
	private List<CardListener> listeners;
	
	/**
	 * Whether to hide the cards
	 */
	private boolean hidden = false;
	
	/**
	 * Creates a CardListPanel
	 * 
	 * @param cardImages the images of the cards
	 */
	public CardListPanel(Map<String, Image> cardImages){
		this.cardImages = cardImages;
		listeners = new ArrayList<CardListener>();
		setBackground(BACKGROUND);
		addMouseListener(this);
	}
	
	/**
	 * Set the cards to display
	 * 
	 * @param cards the cards to display
	 */
	public void setCards(List<Card> cards){
		this.cards = cards;
	}
	
	/**
	 * Set whether to hide the cards
	 * 
	 * @param hide whether to hide the cards
	 */
	public void hideCards(boolean hide){
		hidden = hide;
	}
	
	@Override
	public void paint(Graphics gg){
		Graphics2D g = (Graphics2D) gg;
		super.paint(g);
		
		//If there are cards to display
		if (cards != null){
			int step = CARD_WIDTH+5;
			int x = (getWidth()-cards.size()*(step)+5)/2;
			int y = (getHeight()-CARD_HEIGHT)/2;
			
			//If there is not enough room for the cards
			if (x < 5){
				//Overlap the cards
				x = 5;
				step = (this.getWidth()-5)/cards.size();
			}
			//Draw the cards
			for (Card c : cards){
				g.drawImage(cardImages.get((hidden) ? "back" : c.getName()), x, y, CARD_WIDTH, CARD_HEIGHT, null);
				x+=step;
			}
		}
	}
	
	/**
	 * Add a listener for cards being selected
	 * 
	 * @param ls the listener
	 */
	public void addListener(CardListener ls){
		listeners.add(ls);
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		//Get the location clicked
		int x = arg0.getX();
		int y = arg0.getY();
		
		//Find the card that is selected
		if (cards != null){
			int step = CARD_WIDTH+5;
			int xp = (getWidth()-cards.size()*(step)+5)/2;
			int yp = (getHeight()-CARD_HEIGHT)/2;
			
			if (x < 5){
				x = 5;
				step = (this.getWidth()-5)/cards.size();
			}
			
			for (Card c : cards){
				if (xp < x && x < xp+step && yp < y && y < yp+CARD_HEIGHT){
					for (CardListener lis : listeners) lis.onCardSelected(c);
					break;
				}
				xp+=step;
			}
		}
	}
	
	/*
	 * Methods not used but required by interfaces
	 */
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	/**
	 * Specifies a listener that listens for when cards are selected
	 * 
	 * @author James Greenwood-Thessman
	 */
	public interface CardListener {
		
		/**
		 * Called when a card was selected
		 * 
		 * @param c the card selected
		 */
		public void onCardSelected(Card c);
	}
}
