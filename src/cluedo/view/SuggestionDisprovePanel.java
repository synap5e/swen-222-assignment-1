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

public class SuggestionDisprovePanel extends JPanel implements MouseListener{
	private static final int CARD_WIDTH = 100;
	private static final int CARD_HEIGHT = 150;
	
	private static final Color ROOM = new Color(212,196,173);
	private final Map<String, Image> cardImages;
	
	private List<Card> cards;
	
	private List<CardListener> listeners;
	
	public SuggestionDisprovePanel(Map<String, Image> cardImages){
		this.cardImages = cardImages;
		listeners = new ArrayList<CardListener>();
	}
	
	public void setCards(List<Card> cards){
		setBackground(ROOM);
		this.cards = cards;
		addMouseListener(this);
	}
	
	@Override
	public void paint(Graphics gg){
		Graphics2D g = (Graphics2D) gg;
		super.paint(g);
		if (cards != null){
			int x = (getWidth()-cards.size()*(CARD_WIDTH+5)+5)/2;
			int y = (getHeight()-CARD_HEIGHT)/2;
			
			for (Card c : cards){
				g.drawImage(cardImages.get(c.getName()), x, y, CARD_WIDTH, CARD_HEIGHT, null);
				x+=CARD_WIDTH+5;
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
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {
		int x = arg0.getX();
		int y = arg0.getY();
		
		if (cards != null){
			int xp = (getWidth()-cards.size()*(CARD_WIDTH+5)+5)/2;
			int yp = (getHeight()-cards.size()*CARD_HEIGHT)/2;
			
			for (Card c : cards){
				if (xp < x && x < xp+CARD_WIDTH && yp < y && y < yp+CARD_HEIGHT){
					for (CardListener lis : listeners) lis.onCardSelected(c);
					break;
				}
				xp+=CARD_WIDTH+5;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
