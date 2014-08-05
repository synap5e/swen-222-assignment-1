package cluedo.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import cluedo.game.board.Board;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Tile;
import cluedo.game.board.Location.Direction;

public class Canvas extends JPanel implements MouseListener{
	
	private static final Color BACKGROUND = new Color(51,201,137);
	private static final Color TILE = new Color(255,240,117);
	private static final Color ROOM = new Color(201,193,154);
	private static final Color WALL_COLOR = Color.BLACK;
	private static final Color GRID_COLOR = Color.DARK_GRAY;
	
	private static final int WALL_THICKNESS = 3;
	
	
	private Board board;
	private int tileWidth = 20;
	private int xOffset;
	private int yOffset;
	
	public Canvas(Board board){
		this.board = board;
		setBackground(BACKGROUND);
		
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
		
		for(Tile tile : board.getTiles()){
			g2d.setColor(TILE);
			
			//Apply offset, scaling and translation for the tile
			applyTransform(tile.getX(), tile.getY(), tileWidth, g2d);
		    
		    //Draw tile
		    g2d.fillPolygon(tile.getShape());
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
			
			//Apply offset, scaling and translation for the room
			applyTransform(room.getX(), room.getY(), tileWidth, g2d);
			
		    //Draw room
		    g2d.fillPolygon(room.getShape());
		    
		    //Apply only offset
		    g2d.setTransform(saveTransform);
		    applyTransform(0,0, 1, g2d);
		    
		    //Draw the walls
		    g2d.setColor(WALL_COLOR);
		    g2d.drawPolygon(createBorderPolygon(room));
		    
		    //Draw the name
		    Rectangle2D nameBounds = g2d.getFontMetrics().getStringBounds(room.getName(), g2d);
		    g2d.drawString(room.getName(), 
		    		(int) ((room.getShape().getBounds2D().getCenterX()+room.getX())*tileWidth-nameBounds.getWidth()/2), 
		    		(int) ((room.getShape().getBounds2D().getCenterY()+room.getY())*tileWidth+nameBounds.getHeight()/2));
			
		    drawDoors(g2d, room);
		    
		    //Reset transform
		    g2d.setTransform(saveTransform);
		}
	}
	
	private void applyTransform(double tranX, double tranY, double scale, Graphics2D g){
		AffineTransform trans = new AffineTransform(g.getTransform());
		trans.translate(xOffset, yOffset);
	    trans.scale(scale, scale);
	    trans.translate(tranX, tranY);
	    g.setTransform(trans);
	}
	
	private void drawDoors(Graphics2D g, Room r){
		 for (Location neigh : r.getNeighbours()){
		    	g.setColor(TILE);
		    	if (!neigh.isRoom()){
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
		    }
	}

	/**
	 * Creates a scaled border of the given location. The border is scaled by the current tile width. 
	 * 
	 * @param loc - the location to create the border for
	 * @return a border of the given location.
	 */
	private Polygon createBorderPolygon(Location loc){
		Polygon p = loc.getShape();
		int[] xs = new int[p.npoints];
		int[] ys = new int[p.npoints];
		for (int i = 0; i < p.npoints; ++i){
			xs[i] = (p.xpoints[i]+loc.getX())*tileWidth;
			ys[i] = (p.ypoints[i]+loc.getY())*tileWidth;
		}
		return new Polygon(xs, ys, p.npoints);
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
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
