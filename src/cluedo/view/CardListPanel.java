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
*
* @author James Greenwood-Thessman
*
*/
public class CardListPanel extends JPanel implements MouseListener{
	private static final int CARD_WIDTH = 100;
	private static final int CARD_HEIGHT = 150;
	
	private static final Color ROOM = new Color(212,196,173);
	private final Map<String, Image> cardImages;
	
	private List<Card> cards;
	
	private List<CardListener> listeners;
	
	private boolean hidden = false;
	
	public CardListPanel(Map<String, Image> cardImages){
		this.cardImages = cardImages;
		listeners = new ArrayList<CardListener>();
		setBackground(ROOM);
		addMouseListener(this);
	}
	
	public void setCards(List<Card> cards){
		this.cards = cards;
	}
	
	public void hideCards(boolean hide){
		hidden = hide;
	}
	
	@Override
	public void paint(Graphics gg){
		Graphics2D g = (Graphics2D) gg;
		super.paint(g);
		
		if (cards != null){
			int step = CARD_WIDTH+5;
			int x = (getWidth()-cards.size()*(step)+5)/2;
			int y = (getHeight()-CARD_HEIGHT)/2;
			
			if (x < 5){
				x = 5;
				step = (this.getWidth()-5)/cards.size();
			}
			for (Card c : cards){
				g.drawImage(cardImages.get((hidden) ? "back" : c.getName()), x, y, CARD_WIDTH, CARD_HEIGHT, null);
				x+=step;
			}
		}
	}
	
	public void addListener(CardListener ls){
		listeners.add(ls);
	}
	
	public interface CardListener {
		
		public void onCardSelected(Card c);
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		int x = arg0.getX();
		int y = arg0.getY();
		
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
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
