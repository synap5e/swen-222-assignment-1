package cluedo.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import cluedo.game.board.Accusation;
import cluedo.game.board.Board;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Suggestion;
import cluedo.game.board.Tile;
import cluedo.game.board.Token;
import cluedo.game.board.Character;
import cluedo.game.board.Weapon;
import cluedo.util.json.JsonObject;

/**
 * 
 * @author James Greenwood-Thessman
 *
 */
public class Canvas extends JPanel implements MouseListener{
	
	private static final Color BACKGROUND = new Color(145,204,176);
	private static final Color TILE = new Color(238,177,70);
	private static final Color ROOM = new Color(212,196,173);
	private static final Color WALL_COLOR = new Color(130,23,11);//169,32,14);
	private static final Color GRID_COLOR = Color.BLACK;
	
	private static final int WALL_THICKNESS = 3;
	private static final int CARD_WIDTH = 100;
	private static final int CARD_HEIGHT = 150;
	
	private final Board board;
	
	private final Map<String, Image> cardImages;
	private final Map<String, Image> tokenImages;
	private Image cardBack;
	
	private int tileWidth = 20;
	private int xOffset;
	private int yOffset;
	
	private Location selected;
	
	private List<Location> endLocations;
	
	private Map<Room, Point2D> roomCorner;
	private Map<Room, Point2D> roomCenter;
	
	public Canvas(Board brd, JsonObject def){
		board = brd;
		endLocations = Collections.emptyList();
		
		//Find the corner and center of all the rooms
		roomCorner = new HashMap<Room, Point2D>();
		roomCenter = new HashMap<Room, Point2D>();
		for (Room r : board.getRooms()){
			int minX = board.getWidth();
			int minY = board.getHeight();
			int maxX = 0;
			int maxY = 0;
			for (int x = 0; x < board.getWidth(); ++x){
				for (int y = 0; y < board.getHeight(); ++y){
					if (board.getLocation(x, y) == r){
						if (x < minX) minX = x;
						if (x > maxX) maxX = x;
						if (y < minY) minY = y;
						if (y > maxY) maxY = y;
					}
				}
			}
			++maxX;
			++maxY;
			roomCorner.put(r, new Point(minX, minY));
			roomCenter.put(r, new Point2D.Double((minX+maxX)/2, (minY+maxY)/2));
		}
		tokenImages = new HashMap<String, Image>();
		cardImages = new HashMap<String, Image>();
		try {
			cardBack = ImageIO.read(new File("./images/card_back.png"));
			
			//Load weapon pictures
			cardImages.put("Dagger", ImageIO.read(new File("./images/card_dagger.png")));
			cardImages.put("Revolver", ImageIO.read(new File("./images/card_revolver.png")));
			cardImages.put("Rope", ImageIO.read(new File("./images/card_rope.png")));
			cardImages.put("Spanner", ImageIO.read(new File("./images/card_spanner.png")));
			cardImages.put("Lead Piping", ImageIO.read(new File("./images/card_lead_piping.png")));
			cardImages.put("Candlestick", ImageIO.read(new File("./images/card_candlestick.png")));
			
			//Load character pictures
			cardImages.put("Colonel Mustard", ImageIO.read(new File("./images/card_colonel_mustard.png")));
			cardImages.put("Miss Scarlett", ImageIO.read(new File("./images/card_miss_scarlett.png")));
			cardImages.put("Mrs Peacock", ImageIO.read(new File("./images/card_mrs_peacock.png")));
			cardImages.put("Mrs White", ImageIO.read(new File("./images/card_mrs_white.png")));
			cardImages.put("Professor Plum", ImageIO.read(new File("./images/card_professor_plum.png")));
			cardImages.put("Rev. Green", ImageIO.read(new File("./images/card_rev_green.png")));
			
			//Load room pictures
			cardImages.put("Ballroom", ImageIO.read(new File("./images/card_ballroom.png")));
			cardImages.put("Billiard Room", ImageIO.read(new File("./images/card_billiard_room.png")));
			cardImages.put("Conservatory", ImageIO.read(new File("./images/card_conservatory.png")));
			cardImages.put("Dining Room", ImageIO.read(new File("./images/card_dining_room.png")));
			cardImages.put("Hall", ImageIO.read(new File("./images/card_hall.png")));
			cardImages.put("Kitchen", ImageIO.read(new File("./images/card_kitchen.png")));
			cardImages.put("Library", ImageIO.read(new File("./images/card_library.png")));
			cardImages.put("Lounge", ImageIO.read(new File("./images/card_lounge.png")));
			cardImages.put("Study", ImageIO.read(new File("./images/card_study.png")));
		
			//Load token images
			tokenImages.put("Spanner", ImageIO.read(new File("./images/token_spanner.png")));
			tokenImages.put("Lead Piping", ImageIO.read(new File("./images/token_lead_piping.png")));
			tokenImages.put("Knife", ImageIO.read(new File("./images/token_knife.png")));
			tokenImages.put("Candlestick", ImageIO.read(new File("./images/token_candlestick.png")));
			tokenImages.put("Rope", ImageIO.read(new File("./images/token_rope.png")));
			tokenImages.put("Revolver", ImageIO.read(new File("./images/token_revolver.png")));
		} catch (IOException e) {
		}
		
		setBackground(BACKGROUND);
		addMouseListener(this);
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				Canvas can = (Canvas) arg0.getSource();
				int height = can.getHeight() - CARD_HEIGHT-5;
				tileWidth = (can.getWidth()/board.getWidth() < height/board.getHeight()) ? can.getWidth()/board.getWidth() : height/board.getHeight();
				xOffset = (can.getWidth()-tileWidth*24)/2;
				yOffset = (height-tileWidth*25)/2;
				repaint();
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {}
		});
	}
	
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		//Setup the Graphics2D 
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15));
		
		//Remember the current transform and then apply the offset to the graphics
		AffineTransform saveTransform = g2d.getTransform();
		AffineTransform trans = new AffineTransform(saveTransform);
		trans.translate(xOffset, yOffset);
	    g2d.setTransform(trans);
		
		//Draw locations
		for (int x = 0; x < board.getWidth(); ++x){
			for (int y = 0; y < board.getHeight(); ++y){
				Location loc = board.getLocation(x, y);
				if (loc == null) continue;
				
				//Choose the background color for the location
				if (loc instanceof Tile){
					g2d.setColor(TILE);
				} else {
					g2d.setColor(ROOM);
				}
				if (endLocations.contains(loc)) g2d.setColor(Color.BLUE);
				if (loc == selected) g2d.setColor(Color.ORANGE);
				
				g2d.fillRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);
				
				//Draw the grid outline if the location is a tile and any contained token
				if (loc instanceof Tile){
					g2d.setColor(GRID_COLOR);
					g2d.drawRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);
					
					//TODO: set color to match token
					if (loc.getTokens().size() > 0){
						g2d.fillOval(x*tileWidth+3, y*tileWidth+3, tileWidth-5, tileWidth-5);
					}
				}
			}
		}
		
		//Draw room contents
		for (Room room : board.getRooms()){
			//Draw the room name
			g2d.setColor(Color.BLACK);
			Rectangle2D nameBounds = g2d.getFontMetrics().getStringBounds(room.getName(), g2d);
			Point2D center = roomCenter.get(room);
			g2d.drawString(room.getName(), 
			   		(int) (center.getX()*tileWidth-nameBounds.getWidth()/2), 
			 		(int) (center.getY()*tileWidth-nameBounds.getHeight()/2));			
			
			//Draw the tokens inside the room
			int startY = (int) (center.getY()*tileWidth+nameBounds.getHeight()/2);
			int x = (int) center.getX()*tileWidth-(tileWidth*room.getTokens().size())/2 ;
			for (Token token : room.getTokens()){
				if (token instanceof Character){
					//TODO: set color to match token
					g2d.fillOval(x+3, startY+3, tileWidth-5, tileWidth-5);
				} else {
					g2d.drawImage(tokenImages.get(token.getName()), x, startY, tileWidth, tileWidth, null);
				}
				x += tileWidth;
				
			}
		}
		
		//Draw walls
		g2d.setStroke(new BasicStroke(WALL_THICKNESS));
		g2d.setColor(WALL_COLOR);
		for (int x = 0; x < board.getWidth(); ++x){
			for (int y = 0; y < board.getHeight(); ++y){
				Location loc = board.getLocation(x, y);
				if (loc == null) continue;
				
				//Draw the west wall if it exists
				drawWall(g2d, loc, board.getLocation(x-1, y), x, y, 0, 1);
				
				//Draw the east wall if it exists
				drawWall(g2d, loc, board.getLocation(x+1, y), x+1, y, 0, 1);
				
				//Draw the north wall if it exists
				drawWall(g2d, loc, board.getLocation(x, y-1), x, y, 1, 0);
				
				//Draw the south wall if it exists
				drawWall(g2d, loc, board.getLocation(x, y+1), x, y+1, 1, 0);
			}
		}
	
		//TODO: Draw actual hand
		int x = 0;
		for (Image img : cardImages.values()){
			g2d.drawImage(img, x, tileWidth*board.getHeight()+5, CARD_WIDTH, CARD_HEIGHT, null);
			x += CARD_WIDTH+2;
			if (x > (CARD_WIDTH+2)*6){
				break;
			}
		}
		
		//Return the transformation on the graphics back to what it was
		g2d.setTransform(saveTransform);
	}
	
	/**
	 * Draws the wall specified if the two locations should be separated by a wall.
	 * 
	 * @param loc - the location that would have the wall
	 * @param neighbour - the location that could be a neighbour
	 * @param x - the starting x value in the grid
	 * @param y - the starting y value in the grid
	 * @param width - the offset of the second x value in the grid
	 * @param height - the offset of the second y value in the grid
	 */
	private void drawWall(Graphics2D g, Location loc, Location neighbour, int x, int y, int width, int height){
		if (neighbour == null || !(loc.getNeighbours().contains(neighbour) || loc == neighbour)){
			g.drawLine(x*tileWidth, y*tileWidth, (x+width)*tileWidth, (y+height)*tileWidth);
		}
	}
	
	public Location getSelectedLocation(){
		return selected;
	}
	
	public void unselectLocation(){
		selected = null;
	}
	
	public void setPossibleLocations(List<Location> locations){
		if (locations != null){
			endLocations = locations;
		} else {
			endLocations = Collections.emptyList();
		}
		
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		double x = ((double) (arg0.getX()-xOffset))/tileWidth;
		double y = ((double) (arg0.getY()-yOffset))/tileWidth;
		
		Location loc = board.getLocation((int) x,(int) y);
		if (loc != null){
			selected = loc;
			repaint();
			return;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}


	public void onCharacterMove(Character character, Location destination) {
		this.repaint();
	}
	
	public void onWeaponMove(Weapon weapon, Location room) {
		this.repaint();
	}
	
	

	public void onCharacterJoinedGame(String playerName, Character character, boolean humanPlayer) {
		System.out.printf("%s (%s) joind the game as %s\n", playerName, character.getName(), humanPlayer ? "human" : "AI");
	}


	public void onTurnBegin(String name, Character playersCharacter) {
		System.out.printf("Its %s's (%s) turn\n", name, playersCharacter);
	}


	public void onDiceRolled(int roll) {
		System.out.printf("A %d was rolled\n", roll);
	}


	public void onSuggestionUndisputed(Character suggester,	Suggestion suggestion) {
		System.out.printf("%s suggested that %s killed Dr Black with a %s and no-one could disprove that\n", 
				suggester.getName(), suggestion.getCharacter().getName(), suggestion.getWeapon().getName());
	}


	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Character disprover) {
		System.out.printf("%s suggested that %s killed Dr Black with a %s but %s proved that could not be\n", 
				suggester.getName(), suggestion.getCharacter().getName(), suggestion.getWeapon().getName(), disprover.getName());
	}


	public void onAccusation(Character accussuer, Accusation accusation, boolean correct) {
		System.out.printf(	"%s accused %s of killing Dr Black in the %s with a %s\n" +
							(correct ? "This was magically verified as true" : "They were wrong and got killed") + "\n",
							accussuer.getName(), accusation.getCharacter().getName(), accusation.getRoom().getName(), accusation.getWeapon().getName());
	}

	public void onGameWon(String name, Character playersCharacter) {
		System.out.printf("%s (%s) won the game!\n", name, playersCharacter.getName());
	}


	
}
