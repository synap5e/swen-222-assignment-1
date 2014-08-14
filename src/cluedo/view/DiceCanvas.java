package cluedo.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import cluedo.model.card.Card;

public class DiceCanvas extends JPanel{
	
	private static final Color BACKGROUND = new Color(212,196,173);
	
	private static final int DIE_WIDTH = 40;
	
	private int leftDie;
	private int rightDie;
	private boolean show = false;
	
	public DiceCanvas(){
		setBackground(BACKGROUND);
	}
	
	public void showDice(int left, int right){
		leftDie = left;
		rightDie = right;
		show = true;
	}
	
	public void hideDice(){
		show = false;
	}
	
	@Override
	public void paint(Graphics gg){
		Graphics2D g = (Graphics2D) gg;
		super.paint(g);
		
		if (show){
			drawDie(g, leftDie, getWidth()/2-5-DIE_WIDTH, getHeight()-DIE_WIDTH-5);
			drawDie(g, rightDie, getWidth()/2+5, getHeight()-DIE_WIDTH-5);
		}
	}
	
	private void drawDie(Graphics2D g, int num, int x, int y){
		g.setColor(Color.WHITE);
		g.fillRect(x, y, DIE_WIDTH, DIE_WIDTH);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, DIE_WIDTH, DIE_WIDTH);
		g.setColor(Color.BLACK);
		if (num % 3 == 1){
			g.fillOval(x+DIE_WIDTH/2-2, y+DIE_WIDTH/2-2, 4, 4);
		}
		if (num >= 2){
			g.fillOval(x+4, y+4, 4, 4);
			g.fillOval(x+DIE_WIDTH-8, y+DIE_WIDTH-8, 4, 4);
		}
		if (num >= 4){
			g.fillOval(x+4, y+DIE_WIDTH-8, 4, 4);
			g.fillOval(x+DIE_WIDTH-8, y+4, 4, 4);
		}
		if (num == 6){
			g.fillOval(x+4, y+DIE_WIDTH/2-2, 4, 4);
			g.fillOval(x+DIE_WIDTH-8, y+DIE_WIDTH/2-2, 4, 4);
		}
	}
}
