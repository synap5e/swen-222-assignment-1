package cluedo.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * Displays a pair of dice
 *
 * @author James Greenwood-Thessman
 */
public class DiceCanvas extends JPanel{
	
	private static final long serialVersionUID = 1L;

	/**
	 * The colour of the background
	 */
	private static final Color BACKGROUND = new Color(212,196,173);
	
	/**
	 * The width of the dice
	 */
	private static final int DIE_WIDTH = 40;
	
	/**
	 * The current value of the left die
	 */
	private int leftDie;
	
	/**
	 * The current value of the right die
	 */
	private int rightDie;
	
	/**
	 * Whether to display the dice
	 */
	private boolean show = false;
	
	/**
	 * Create the canvas for displaying dice
	 */
	public DiceCanvas(){
		setBackground(BACKGROUND);
	}
	
	/**
	 * Show the dice
	 * 
	 * @param left the left die
	 * @param right the right die
	 */
	public void showDice(int left, int right){
		leftDie = left;
		rightDie = right;
		show = true;
		repaint();
	}
	
	/**
	 * Hides the dice
	 */
	public void hideDice(){
		show = false;
		repaint();
	}
	
	@Override
	public void paint(Graphics gg){
		Graphics2D g = (Graphics2D) gg;
		super.paint(g);
		
		//If the dice are to be displayed, draw both dice
		if (show){
			drawDie(g, leftDie, getWidth()/2-5-DIE_WIDTH, getHeight()-DIE_WIDTH-5);
			drawDie(g, rightDie, getWidth()/2+5, getHeight()-DIE_WIDTH-5);
		}
	}
	
	private void drawDie(Graphics2D g, int num, int x, int y){
		//Draw the die without the pips
		g.setColor(Color.WHITE);
		g.fillRect(x, y, DIE_WIDTH, DIE_WIDTH);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, DIE_WIDTH, DIE_WIDTH);
		g.setColor(Color.BLACK);
		
		//If the number is 1, 3, 5
		if (num % 2 == 1){
			g.fillOval(x+DIE_WIDTH/2-2, y+DIE_WIDTH/2-2, 4, 4);
		}
		//If the number is 2, 3, 4, 5, 6
		if (num >= 2){
			g.fillOval(x+4, y+4, 4, 4);
			g.fillOval(x+DIE_WIDTH-8, y+DIE_WIDTH-8, 4, 4);
		}
		//If the number is 4, 5, 6
		if (num >= 4){
			g.fillOval(x+4, y+DIE_WIDTH-8, 4, 4);
			g.fillOval(x+DIE_WIDTH-8, y+4, 4, 4);
		}
		//If the number is 6
		if (num == 6){
			g.fillOval(x+4, y+DIE_WIDTH/2-2, 4, 4);
			g.fillOval(x+DIE_WIDTH-8, y+DIE_WIDTH/2-2, 4, 4);
		}
	}
}
