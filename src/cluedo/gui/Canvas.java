package cluedo.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import cluedo.game.board.Character;
import cluedo.game.board.Board;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Tile;
import cluedo.game.board.Location.Direction;
import cluedo.util.json.JsonList;
import cluedo.util.json.JsonObject;

public class Canvas extends JPanel implements MouseListener{
	
	private static final Color BACKGROUND = new Color(51,201,137);
	private static final Color TILE = new Color(255,240,117);
	private static final Color ROOM = new Color(201,193,154);
	private static final Color WALL_COLOR = Color.BLACK;
	private static final Color GRID_COLOR = Color.DARK_GRAY;
	
	private static final int WALL_THICKNESS = 3;
	
	
	private final Board board;
	
	private int tileWidth = 20;
	private int xOffset;
	private int yOffset;
	
	private Location selected;
	
	private Map<Room, Point2D> roomCorner;
	private Map<Room, Point2D> roomCenter;
	
	public Canvas(Board brd, JsonObject def){
		board = brd;
		
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
		
		setBackground(BACKGROUND);
		addMouseListener(this);
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				Canvas can = (Canvas) arg0.getSource();
				tileWidth = (can.getWidth()/board.getWidth() < can.getHeight()/board.getHeight()) ? can.getWidth()/board.getWidth() : can.getHeight()/board.getHeight();
				xOffset = (can.getWidth()-tileWidth*24)/2;
				yOffset = (can.getHeight()-tileWidth*25)/2;
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
		g2d.setColor(Color.BLACK);
		for (Room room : board.getRooms()){
			//Draw the room name
			Rectangle2D nameBounds = g2d.getFontMetrics().getStringBounds(room.getName(), g2d);
			Point2D center = roomCenter.get(room);
			g2d.drawString(room.getName(), 
			   		(int) (center.getX()*tileWidth-nameBounds.getWidth()/2), 
			 		(int) (center.getY()*tileWidth+nameBounds.getHeight()/2));			
			
			//TODO: Draw tokens inside the room
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
}
