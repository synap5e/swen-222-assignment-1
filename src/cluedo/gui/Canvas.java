package cluedo.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

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
	private final int boardWidth;
	private final int boardHeight;
	
	private int tileWidth = 20;
	private int xOffset;
	private int yOffset;
	
	private Location selected;
	
	public Canvas(Board board, JsonObject def){
		this.board = board;
		JsonList rows = (JsonList) def.get("board");
		boardWidth = ((JsonList)rows.get(0)).size();
		boardHeight = rows.size();
		
		setBackground(BACKGROUND);
		addMouseListener(this);
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				Canvas can = (Canvas) arg0.getSource();
				tileWidth = (can.getWidth()/24 < can.getHeight()/25) ? can.getWidth()/24:can.getHeight()/25;
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
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 15));
		AffineTransform saveTransform = g2d.getTransform();
		applyOffset(g2d);
		
		//Draw locations
		for (int x = 0; x < boardWidth; ++x){
			for (int y = 0; y < boardHeight; ++y){
				Location loc = board.getLocation(x, y);
				if (loc == null) continue;
				if (loc instanceof Tile){
					g2d.setColor(TILE);
				} else {
					g2d.setColor(ROOM);
				}
				
				if (loc == selected) g2d.setColor(Color.ORANGE);
				
				g2d.fillRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);
				
				if (loc instanceof Tile){
					g2d.setColor(GRID_COLOR);
					g2d.drawRect(x*tileWidth, y*tileWidth, tileWidth, tileWidth);
				}
				
				//TODO: Draw tokens in rooms
			}
		}
		
		//Draw walls
		g2d.setStroke(new BasicStroke(WALL_THICKNESS));
		g2d.setColor(WALL_COLOR);
		for (int x = 0; x < boardWidth; ++x){
			for (int y = 0; y < boardHeight; ++y){
				Location loc = board.getLocation(x, y);
				if (loc == null) continue;
				
				//if has west wall
				Location west = board.getLocation(x-1, y);
				if (west == null || !(loc.getNeighbours().contains(west) || loc == west)){
					g2d.drawLine(x*tileWidth, y*tileWidth, x*tileWidth, (y+1)*tileWidth);
				}
				//if has east wall
				Location east = board.getLocation(x+1, y);
				if (east == null || !(loc.getNeighbours().contains(east) || loc == east)){
					g2d.drawLine((x+1)*tileWidth, y*tileWidth, (x+1)*tileWidth, (y+1)*tileWidth);
				}
				//if has north wall
				Location north = board.getLocation(x, y-1);
				if (north == null || !(loc.getNeighbours().contains(north) || loc == north)){
					g2d.drawLine(x*tileWidth, y*tileWidth, (x+1)*tileWidth, y*tileWidth);
				}
				//if has south wall
				Location south = board.getLocation(x, y+1);
				if (south == null || !(loc.getNeighbours().contains(south) || loc == south)){
					g2d.drawLine(x*tileWidth, (y+1)*tileWidth, (x+1)*tileWidth, (y+1)*tileWidth);
				}
			}
		}
		g2d.setTransform(saveTransform);
		
		/*for(Tile tile : board.getTiles()){
			g2d.setColor(TILE);
			if (tile == selected) g2d.setColor(Color.ORANGE);
			
			//Apply offset, scaling and translation for the tile
			applyTransform(tile.getX(), tile.getY(), tileWidth, g2d);
		    
		    //Draw tile
		    //g2d.fillPolygon(tile.getShape());
		    g2d.setTransform(saveTransform);
		    
		    //Apply only offset
		    applyTransform(0, 0, 1, g2d);
		   
		    //Draw the outline
		    g2d.setColor(GRID_COLOR);
		    g2d.drawPolygon(createBorderPolygon(tile));
			
		    //Reset transform
		    g2d.setTransform(saveTransform);
		}
		
		g2d.setStroke(new BasicStroke(WALL_THICKNESS));
		for(Room room : board.getRooms()){
			g2d.setColor(ROOM);
			if (room == selected) g2d.setColor(Color.ORANGE);
			
			//Apply offset, scaling and translation for the room
			//applyTransform(room.getX(), room.getY(), tileWidth, g2d);
			
		    //Draw room
		    //g2d.fillPolygon(room.getShape());
		    
		    //Apply only offset
		    g2d.setTransform(saveTransform);
		    applyTransform(0,0, 1, g2d);
		    
		    //Draw the walls
		    g2d.setColor(WALL_COLOR);
		    g2d.drawPolygon(createBorderPolygon(room));
		    
		    //Draw the name
		    Rectangle2D nameBounds = g2d.getFontMetrics().getStringBounds(room.getName(), g2d);
		    /*g2d.drawString(room.getName(), 
		    		(int) ((room.getShape().getBounds2D().getCenterX()+room.getX())*tileWidth-nameBounds.getWidth()/2), 
		    		(int) ((room.getShape().getBounds2D().getCenterY()+room.getY())*tileWidth+nameBounds.getHeight()/2));
			*
		    drawDoors(g2d, room);
		    
		    //Reset transform
		    g2d.setTransform(saveTransform);
		}*/
	}
	
	private void applyOffset(Graphics2D g){
		AffineTransform trans = new AffineTransform(g.getTransform());
		trans.translate(xOffset, yOffset);
	    g.setTransform(trans);
	}
	
	private void drawDoors(Graphics2D g, Room r){
		 for (Location neigh : r.getNeighbours()){
		    	g.setColor(TILE);
		    	if (neigh instanceof Tile){
		    		Tile tile = (Tile) neigh;
		    		Direction dir = r.neighboursDirection(tile);
		    		int x = tile.getX();
		    		int y = tile.getY();
		    		switch(dir){
		    		case NORTH:
		    			g.drawLine((x)*tileWidth+WALL_THICKNESS, (y+1)*tileWidth, (x+1)*tileWidth-WALL_THICKNESS, (y+1)*tileWidth);
		    			break;
		    		case SOUTH:
		    			g.drawLine((x)*tileWidth+WALL_THICKNESS, (y)*tileWidth, (x+1)*tileWidth-WALL_THICKNESS, (y)*tileWidth);
		    			break;
		    		case EAST:
		    			g.drawLine((x)*tileWidth, (y)*tileWidth+WALL_THICKNESS, (x)*tileWidth, (y+1)*tileWidth-WALL_THICKNESS);
		    			break;
		    		case WEST:
		    			g.drawLine((x+1)*tileWidth, (y)*tileWidth+WALL_THICKNESS, (x+1)*tileWidth, (y+1)*tileWidth-WALL_THICKNESS);
		    		}
		    	}
			//g2d.drawString("" + loc.getNeighbours().size(), (int) (loc.getShape().getBounds2D().getCenterX()+loc.getX())*tileWidth, (int) (loc.getShape().getBounds2D().getCenterY()+loc.getY())*tileWidth+20);
		    //g2d.setTransform(saveTransform);
		}
	}

	/**
	 * Creates a scaled border of the given location. The border is scaled by the current tile width. 
	 * 
	 * @param loc - the location to create the border for
	 * @return a border of the given location.
	 */
	private Polygon createBorderPolygon(Location loc){
		/*Polygon p = loc.getShape();
		int[] xs = new int[p.npoints];
		int[] ys = new int[p.npoints];
		for (int i = 0; i < p.npoints; ++i){
			xs[i] = (p.xpoints[i]+loc.getX())*tileWidth;
			ys[i] = (p.ypoints[i]+loc.getY())*tileWidth;
		}*/
		return new Polygon();//xs, ys, p.npoints);
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
		for (Room room : board.getRooms()){
			/*double relX = x - room.getX();
			double relY = y - room.getY();
			if (room.getShape().contains(relX, relY)){
				selected = room;
				repaint();
				return;
			}*/
		}
		
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
