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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import util.json.JsonNumber;
import util.json.JsonObject;
import cluedo.model.Board;
import cluedo.model.Location;
import cluedo.model.Tile;
import cluedo.model.card.Card;
import cluedo.model.card.Character;
import cluedo.model.card.Room;
import cluedo.model.card.Token;

/**
 * BoardCanvas displays and enables the user to interact with board as well as provide a display for the current action. 
 * The interaction with the board gives the user the ability to select rooms and tokens.
 *
 * @author James Greenwood-Thessman
 *
 */
public class BoardCanvas extends JPanel implements MouseListener, MouseMotionListener, ComponentListener{

	private static final long serialVersionUID = 1L;
	
	/*
	 * Color constants
	 */
	/**
	 * The colour of the background
	 */
	private static final Color BACKGROUND = new Color(145,204,176);
	/**
	 * The colour of the tiles
	 */
	private static final Color TILE = new Color(238,177,70);
	/**
	 * The colour of the rooms
	 */
	private static final Color ROOM = new Color(212,196,173);
	/**
	 * The colour of the walls
	 */
	private static final Color WALL_COLOR = new Color(130,23,11);
	/**
	 * The colour of the lines that form the grid
	 */
	private static final Color GRID_COLOR = Color.BLACK;
	/**
	 * The colour of items that are able to be selected
	 */
	private static final Color SELECTABLE_COLOR = new Color(68, 158, 255);

	/*
	 * Board sizes
	 */
	/**
	 * The thickness to draw each wall
	 */
	private static final int WALL_THICKNESS = 3;
	/**
	 * The height of the tiles
	 */
	private static final int TITLE_HEIGHT = 40;
	/**
	 * The margin around the arrows showing where the secret passage leads
	 */
	private static final int ARROW_MARGIN = 5;
	
	/**
	 * The width of the title that displays the current action
	 */
	private static final int TITLE_WIDTH = 400;

	/**
	 * The board being displayed
	 */
	private final Board board;

	/**
	 * The map of token images using the name of the token as the key
	 */
	private final Map<String, Image> tokenImages;
	
	/**
	 * The map of colours for the characters where the character name is the key 
	 */
	private final Map<String, Color> characterColors;

	/**
	 * The current action to be displayed
	 */
	private String action = "Game Setup";
	
	/**
	 * The parent frame
	 */
	private CluedoFrame frame;

	/**
	 * The current width of the tiles
	 */
	private int tileWidth = 20;
	
	/**
	 * The x offset required to center the board
	 */
	
	private int xOffset;
	/**
	 * The y offset required to center the board
	 */
	private int yOffset;

	/**
	 * The currently selected location
	 */
	private Location selected;
	
	/**
	 * The list of locations that can be moved to in the current turn
	 */
	private List<Location> possibleDestinations;

	/**
	 * The top left corner of each room
	 */
	private Map<Room, Point2D> roomCorner;
	
	/**
	 * The center of each room
	 */
	private Map<Room, Point2D> roomCenter;
	
	/**
	 * The map of relative locations of passage ways inside rooms (only contains rooms with secret passages)
	 */
	private Map<String, Point2D> passageWay;

	/**
	 * The character of the player whose turn it currently is
	 */
	private Character currentPlayer;

	/**
	 * Whether the characters should be highlighted to draw foca=us
	 */
	private boolean focusCharacters = false;
	/**
	 * Whether the weapons should be highlighted to draw foca=us
	 */
	private boolean focusWeapons = false;
	/**
	 * Whether the rooms should be highlighted to draw foca=us
	 */
	private boolean focusRooms = false;

	
	/**
	 * Creates a canvas to display the given board
	 * 
	 * @param frame the parent frame
	 * @param board the board to represent
	 * @param def the JSON definition of the board and characters
	 * @param tokenImages the images for each token
	 */
	public BoardCanvas(CluedoFrame frame, Board board, JsonObject def, Map<String, Image> tokenImages){
		this.frame = frame;
		this.board = board;
		this.tokenImages = tokenImages;
		possibleDestinations = Collections.emptyList();
		
		//Set the background colour
		setBackground(BACKGROUND);
		
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
		
		//Load Character colours using the JSON definition
		characterColors = new HashMap<String, Color>();
		JsonObject character = (JsonObject) def.get("characters");
		for (String key : character.keys()){
			//Add the colour for each character
			JsonObject colorDefinition = (JsonObject) ((JsonObject) character.get(key)).get("colour");
			Color color = new Color((int) ((JsonNumber) colorDefinition.get("red")).value(),
									(int) ((JsonNumber) colorDefinition.get("green")).value(),
									(int) ((JsonNumber) colorDefinition.get("blue")).value());
			characterColors.put(key, color);
		}
		
		
		//Load passage way data using the JSON definition
		passageWay = new HashMap<String, Point2D>();
		JsonObject rooms = (JsonObject) def.get("rooms");
		for (String key : rooms.keys()){
			//Add the relative locations of passage ways for the rooms that have them
			if (((JsonObject) rooms.get(key)).containsKey("passage")){
				JsonObject passage = (JsonObject) ((JsonObject) rooms.get(key)).get("passage");
				passageWay.put(key, new Point((int)((JsonNumber) passage.get("x")).value(),
											  (int)((JsonNumber) passage.get("y")).value()));
			}
		}

		//Add the listeners
		addMouseListener(this);
		addMouseMotionListener(this);
		addComponentListener(this);
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		
		//Setup the Graphics2D
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//Remember the current transform and then apply the offset to the graphics
		AffineTransform saveTransform = g2d.getTransform();
		AffineTransform trans = new AffineTransform(saveTransform);
		trans.translate(xOffset, yOffset+TITLE_HEIGHT+10);
	    g2d.setTransform(trans);

	    //Draw the solid rectangle for the showing the current action
	    g2d.setColor(ROOM);
	    g2d.fillRect((board.getWidth()*tileWidth-TITLE_WIDTH)/2, -5-TITLE_HEIGHT, TITLE_WIDTH, TITLE_HEIGHT);

	    //Display the current action
	    g2d.setColor(Color.BLACK);
	    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, TITLE_HEIGHT-5));
		Rectangle2D actionBounds = g2d.getFontMetrics().getStringBounds(action, g2d);
		g2d.drawString(action,
		   		(int) ((board.getWidth()*tileWidth-actionBounds.getWidth())/2),
		 		(int) (5-TITLE_HEIGHT+actionBounds.getHeight()/2));

		//Draw locations iterating over each square
		for (int x = 0; x < board.getWidth(); ++x){
			for (int y = 0; y < board.getHeight(); ++y){
				//Get the current location
				Location loc = board.getLocation(x, y);
				if (loc == null) continue;

				//Choose the background colour for the location
				if (loc == selected) {
					//Draw the selected location as orange
					g2d.setColor(Color.ORANGE);
				} else if (possibleDestinations.contains(loc)){
					//Draw possible destinations as selectable
					g2d.setColor(SELECTABLE_COLOR);
				} else if (loc instanceof Tile){
					g2d.setColor(TILE);
				} else {
					//Display rooms as selectable (if focused) or their normal colour
					g2d.setColor((focusRooms) ? SELECTABLE_COLOR: ROOM);
				}

				//Fill the square at that location
				g2d.fillRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);

				//Draw the grid outline if the location is a tile and any contained token
				if (loc instanceof Tile){
					//Draw the grid
					g2d.setColor(GRID_COLOR);
					g2d.drawRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);

					//Draw the character on the tile
					if (loc.getTokens().size() > 0){
						//Set the colour to match the character
						g2d.setColor(characterColors.get(loc.getTokens().get(0).getName()));
						
						//Draw the character token 
						g2d.fillOval(x*tileWidth+3, y*tileWidth+3, tileWidth-5, tileWidth-5);
						
						//Draw the outline
						if (focusCharacters){
							g2d.setColor(SELECTABLE_COLOR);
							g2d.drawOval(x*tileWidth+3, y*tileWidth+3, tileWidth-5, tileWidth-5);
						} else if (loc.getTokens().get(0) == currentPlayer){
							g2d.setColor(Color.BLACK);
							g2d.drawOval(x*tileWidth+3, y*tileWidth+3, tileWidth-5, tileWidth-5);
						}
					}
				//Draw passage way entrances
				} else if (passageWay.containsKey(((Room) loc).getName())){
					Room room = (Room) loc;
					//If the current location is an entrance
					if (x == (int) (roomCorner.get(loc).getX()+passageWay.get(room.getName()).getX())
							&& y == (int) (roomCorner.get(loc).getY()+passageWay.get(room.getName()).getY())){
						//Draw the background and outline of the entrance
						g2d.setColor(Color.GRAY);
						g2d.fillRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);
						g2d.setColor(Color.BLACK);
						g2d.drawRect(x*tileWidth, y*tileWidth, tileWidth-1, tileWidth-1);
						
						//Find the other end of the passage way
						Room otherEnd = null;
						for (Location neighbour : room.getNeighbours()){
							if (neighbour instanceof Room){
								otherEnd = (Room) neighbour;
								break;
							}
						}
						
						//Get the difference between the entrances of the passage way
						int dx = (int) (roomCenter.get(room).getX()-roomCorner.get(otherEnd).getX());
						int dy = (int) (roomCenter.get(room).getY()-roomCorner.get(otherEnd).getY());
						
						//Draw the arrow towards the other entrance
						g2d.setColor(Color.ORANGE);
						if (dx > 0 && dy > 0){
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN); //top
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN, (x)*tileWidth+ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN); //left
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN); 
						} else if (dx < 0 && dy < 0){
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN); //bottom
							g2d.drawLine((x+1)*tileWidth-ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN); //right
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN);
						} else if (dx < 0 && dy > 0){
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN); //top
							g2d.drawLine((x+1)*tileWidth-ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN); //right
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN);
						} else {
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN); //bottom
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN, (x)*tileWidth+ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN); //left
							g2d.drawLine((x)*tileWidth+ARROW_MARGIN, (y+1)*tileWidth-ARROW_MARGIN, (x+1)*tileWidth-ARROW_MARGIN, (y)*tileWidth+ARROW_MARGIN);
						}
					}
				}
			}
		}

		//Set the font size ready for when the names of the rooms are drawn
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15));
		
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
				//Draw the character tokens
				if (token instanceof Character){
					g2d.setColor(characterColors.get(token.getName()));
					g2d.fillOval(x+3, startY+3, tileWidth-5, tileWidth-5);
					if (focusCharacters){
						g2d.setColor(SELECTABLE_COLOR);
						g2d.drawOval(x+3, startY+3, tileWidth-5, tileWidth-5);
					}
				//Draw the weapon tokens
				} else {
					g2d.drawImage(tokenImages.get(token.getName()), x, startY, tileWidth, tileWidth, null);
					if (focusWeapons){
						g2d.setColor(SELECTABLE_COLOR);
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

	/**
	 * Unselect the current location selected
	 */
	public void unselectLocation(){
		selected = null;
	}

	/**
	 * Set the possible locations the current player can move to. 
	 * Using null will set the possible locations to an empty list.
	 * 
	 * @param locations the location able to be moved to
	 */
	public void setPossibleLocations(List<Location> locations){
		if (locations != null){
			possibleDestinations = locations;
		} else {
			possibleDestinations = Collections.emptyList();
		}
	}

	/**
	 * Set whether to focus rooms
	 * 
	 * @param focus whether to focus rooms
	 */
	public void focusRooms(boolean focus){
		focusRooms = focus;
	}

	/**
	 * Set whether to focus character tokens
	 * 
	 * @param focus whether to focus characters
	 */
	public void focusCharacters(boolean focus){
		focusCharacters = focus;
	}

	/**
	 * Set whether to focus weapon tokens
	 * 
	 * @param focus whether to focus weapons
	 */
	public void focusWeapons(boolean focus){
		focusWeapons = focus;
	}

	/**
	 * Set the current action to be displayed
	 * 
	 * @param act the new action
	 */
	public void setCurrentAction(String action){
		this.action = action;
		repaint();
	}
	
	/**
	 * Set the current player
	 * 
	 * @param playersCharacter the character of the current player
	 */
	public void setCurrentPlayer(Character playersCharacter) {
		currentPlayer = playersCharacter;
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		//Get the location on the board
		double x = ((double) (arg0.getX()-xOffset))/tileWidth;
		double y = ((double) (arg0.getY()-yOffset-TITLE_HEIGHT-10))/tileWidth;
		Location loc = board.getLocation((int) x,(int) y);
		
		//If there is a location there
		if (loc != null){
			//If it's a tile
			if (loc instanceof Tile){
				//If there is a token, select the token instead
				if (loc.getTokens().size() > 0){
					frame.onTokenSelect(loc.getTokens().get(0));
				} else {
					//Otherwise select the locations
					selected = loc;
					frame.onLocationSelect(loc);
				}
			//If the location is the room
			} else { 
				//Try find the token selected in the room
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
				//If no token was selected
				if (t == null){
					//Select the room instead
					selected = loc;
					frame.onLocationSelect(loc);
				} else {
					frame.onTokenSelect(t);
				}
			}
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		//Get the location on the board
		double x = ((double) (arg0.getX()-xOffset))/tileWidth;
		double y = ((double) (arg0.getY()-yOffset-TITLE_HEIGHT-10))/tileWidth;
		Location loc = board.getLocation((int) x,(int) y);
		
		//If there is a location there
		if (loc != null){
			//If it's a tile
			if (loc instanceof Tile){
				//If there is a token, have the token displayed
				if (loc.getTokens().size() > 0){
					frame.setDisplayedCard(loc.getTokens().get(0));
				}
			//If the location is the room
			} else { 
				//Have the room displayed
				Card hover = (Room) loc;
				
				//Unless a token is being hovered over
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
	public void componentResized(ComponentEvent arg0) {
		//Get the height of the board taking into account the current action display
		int height = getHeight()-TITLE_HEIGHT-10;
		
		//Calculate the width of the tiles as well as the offset
		tileWidth = (getWidth()/board.getWidth() < height/board.getHeight()) ? getWidth()/board.getWidth() : height/board.getHeight();
		xOffset = (getWidth()-tileWidth*24)/2;
		yOffset = (height-tileWidth*25)/2;
		
		repaint();
	}
	
	/*
	 * Methods not implemented but needed to satisfy interfaces 
	 */
	
	@Override
	public void componentShown(ComponentEvent arg0) {}

	@Override
	public void componentMoved(ComponentEvent arg0) {}

	@Override
	public void componentHidden(ComponentEvent arg0) {}
	
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
