package cluedo.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import util.json.JsonNumber;
import util.json.JsonObject;
import util.json.JsonString;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Token;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Suggestion;

/**
 *
 * @author James Greenwood-Thessman
 *
 */
public class BoardCanvas extends JPanel implements MouseListener, MouseMotionListener{

	private static final Color BACKGROUND = new Color(145,204,176);
	private static final Color TILE = new Color(238,177,70);
	private static final Color ROOM = new Color(212,196,173);
	private static final Color WALL_COLOR = new Color(130,23,11);//169,32,14);
	private static final Color GRID_COLOR = Color.BLACK;
	private static final Color SELECTION_COLOR = new Color(68, 158, 255);

	private static final int WALL_THICKNESS = 3;
	private static final int CARD_WIDTH = 100;
	private static final int CARD_HEIGHT = 150;

	private static final int TITLE_HEIGHT = 40;
	private static final int TITLE_WIDTH = 400;

	private final Board board;

	private final Map<String, Image> cardImages;
	private final Map<String, Image> tokenImages;
	private final Map<String, Color> characterColors;

	private String action = "Game Setup";
	private CluedoFrame frame;

	private Card hover;

	private int tileWidth = 20;
	private int xOffset;
	private int yOffset;

	private Location selected;
	private List<Location> endLocations;

	private Map<Room, Point2D> roomCorner;
	private Map<Room, Point2D> roomCenter;

	private Character currentPlayer;

	private boolean focusCharacters = false;
	private boolean focusWeapons = false;
	private boolean focusRooms = false;

	
	
	public BoardCanvas(CluedoFrame fram, Board brd, JsonObject def, Map<String, Image> cardImages, Map<String, Image> tokenImages){
		frame = fram;
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
		this.tokenImages = tokenImages;
		this.cardImages = cardImages;
		
		//Load Character colours
		characterColors = new HashMap<String, Color>();
		JsonObject character = (JsonObject) def.get("characters");
		for (String key : character.keys()){
			JsonObject colorDefinition = (JsonObject) ((JsonObject) character.get(key)).get("colour");
			Color color = new Color((int) ((JsonNumber) colorDefinition.get("red")).value(),
									(int) ((JsonNumber) colorDefinition.get("green")).value(),
									(int) ((JsonNumber) colorDefinition.get("blue")).value());
			characterColors.put(key, color);
		}

		setBackground(BACKGROUND);
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent arg0) {}

			@Override
			public void componentResized(ComponentEvent arg0) {
				BoardCanvas can = (BoardCanvas) arg0.getSource();
				int height = can.getHeight() -TITLE_HEIGHT-10;
				tileWidth = (can.getWidth()/board.getWidth() < height/board.getHeight()) ? can.getWidth()/board.getWidth() : height/board.getHeight();
				xOffset = (can.getWidth()-tileWidth*24)/2;
				yOffset = (height-tileWidth*25)/2;
				frame.repaint();
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
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, TITLE_HEIGHT-5));

		//Remember the current transform and then apply the offset to the graphics
		AffineTransform saveTransform = g2d.getTransform();
		AffineTransform trans = new AffineTransform(saveTransform);
		trans.translate(xOffset, yOffset+TITLE_HEIGHT+10);
	    g2d.setTransform(trans);

	    g2d.setColor(ROOM);
	    g2d.fillRect((board.getWidth()*tileWidth-TITLE_WIDTH)/2, -5-TITLE_HEIGHT, TITLE_WIDTH, TITLE_HEIGHT);

	    g2d.setColor(Color.BLACK);
		Rectangle2D actionBounds = g2d.getFontMetrics().getStringBounds(action, g2d);
		g2d.drawString(action,
		   		(int) ((board.getWidth()*tileWidth-actionBounds.getWidth())/2),
		 		(int) (5-TITLE_HEIGHT+actionBounds.getHeight()/2));
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15));

		//Draw locations
		for (int x = 0; x < board.getWidth(); ++x){
			for (int y = 0; y < board.getHeight(); ++y){
				Location loc = board.getLocation(x, y);
				if (loc == null) continue;

				//Choose the background color for the location
				if (loc instanceof Tile){
					g2d.setColor(TILE);
				} else {
					g2d.setColor((focusRooms) ? SELECTION_COLOR: ROOM);
				}
				if (endLocations.contains(loc)) g2d.setColor(SELECTION_COLOR);
				if (loc == selected) g2d.setColor(Color.ORANGE);

				g2d.fillRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);

				//Draw the grid outline if the location is a tile and any contained token
				if (loc instanceof Tile){
					g2d.setColor(GRID_COLOR);
					g2d.drawRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);

					if (loc.getTokens().size() > 0){
						g2d.setColor(characterColors.get(loc.getTokens().get(0).getName()));
						g2d.fillOval(x*tileWidth+3, y*tileWidth+3, tileWidth-5, tileWidth-5);
						if (loc.getTokens().get(0) == currentPlayer){
							g2d.setColor(Color.BLACK);
							g2d.drawOval(x*tileWidth+3, y*tileWidth+3, tileWidth-5, tileWidth-5);
						}
						if (focusCharacters){
							g2d.setColor(SELECTION_COLOR);
							g2d.drawOval(x*tileWidth+3, y*tileWidth+3, tileWidth-5, tileWidth-5);
						}
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
			int startY = (int) (center.getY()*tileWidth+10);
			int x = (int) center.getX()*tileWidth-(tileWidth*room.getTokens().size())/2 ;
			for (Token token : room.getTokens()){
				if (token instanceof Character){
					g2d.setColor(characterColors.get(token.getName()));
					g2d.fillOval(x+3, startY+3, tileWidth-5, tileWidth-5);
					if (focusCharacters){
						g2d.setColor(SELECTION_COLOR);
						g2d.drawOval(x+3, startY+3, tileWidth-5, tileWidth-5);
					}
				} else {
					g2d.drawImage(tokenImages.get(token.getName()), x, startY, tileWidth, tileWidth, null);
					if (focusWeapons){
						g2d.setColor(SELECTION_COLOR);
						g2d.drawRect(x, startY, tileWidth, tileWidth);
					}
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
				
				//Draw wall when cornered by a room
				Location above = board.getLocation(x, y-1);
				Location right = board.getLocation(x+1, y);
				if (above == right && above != null && loc.getNeighbours().contains(above)){
					g.drawLine((x+1)*tileWidth, y*tileWidth, (x+1)*tileWidth, (y+1)*tileWidth);
				}
			}
		}

		//Return the transformation on the graphics back to what it was
		g2d.setTransform(saveTransform);

		if (hover != null){
			g2d.drawImage(cardImages.get(hover.getName()), 0, 0, CARD_WIDTH, CARD_HEIGHT, null);
		}
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

	public void focusRooms(boolean focus){
		focusRooms = focus;
	}

	public void focusCharacters(boolean focus){
		focusCharacters = focus;
	}

	public void focusWeapons(boolean focus){
		focusWeapons = focus;
	}

	public void setCurrentAction(String act){
		action = act;
		frame.repaint();
	}
	
	public void onTurnBegin(String name, Character playersCharacter) {
		currentPlayer = playersCharacter;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		double x = ((double) (arg0.getX()-xOffset))/tileWidth;
		double y = ((double) (arg0.getY()-yOffset-TITLE_HEIGHT-10))/tileWidth;

		Location loc = board.getLocation((int) x,(int) y);
		if (loc != null){
			if (loc instanceof Tile){
				if (loc.getTokens().size() > 0){
					frame.onTokenSelect(loc.getTokens().get(0));
				} else {
					selected = loc;
					frame.onLocationSelect(loc);
				}
			} else { //must be a room
				//find token
				Token t = null;
				if (loc.getTokens().size() > 0){
					double xc = roomCenter.get(loc).getX();
					double yc = roomCenter.get(loc).getY() - 10d/tileWidth+1;
					xc = xc-(loc.getTokens().size())/2d ;
					for (Token token : loc.getTokens()){
						if (x > xc && x < xc+1 && y > yc && y < yc+1){
							t = token;
						}
						++xc;
					}
				}
				if (t == null){
					selected = loc;
					frame.onLocationSelect(loc);
				} else {
					frame.onTokenSelect(t);
				}
			}
			frame.repaint();

			return;
		}
	}

	@Override

	public void mouseMoved(MouseEvent arg0) {
		double x = ((double) (arg0.getX()-xOffset))/tileWidth;
		double y = ((double) (arg0.getY()-yOffset-TITLE_HEIGHT-10))/tileWidth;

		Location loc = board.getLocation((int) x,(int) y);
		if (loc != null){
			if (loc instanceof Tile){
				if (loc.getTokens().size() > 0){
					frame.setDisplayedCard(loc.getTokens().get(0));
				}
			} else { //must be a room
				Card hover = (Room) loc;
				if (loc.getTokens().size() > 0){
					double xc = roomCenter.get(loc).getX();
					double yc = roomCenter.get(loc).getY() - 10d/tileWidth+1;
					xc = xc-(loc.getTokens().size())/2d ;
					for (Token token : loc.getTokens()){
						if (x > xc && x < xc+1 && y > yc && y < yc+1){
							hover = token;
						}
						++xc;
					}
				}
				frame.setDisplayedCard(hover);
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

	@Override
	public void mouseDragged(MouseEvent arg0) {}
}
