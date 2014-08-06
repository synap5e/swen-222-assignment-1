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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import cluedo.game.board.Board;
import cluedo.game.board.Location;
import cluedo.game.board.Room;
import cluedo.game.board.Tile;
import cluedo.game.board.Location.Direction;

public class Canvas extends JPanel {
	
	private static Color background;
	//private static 
	
	private Board board;
	private int tileWidth = 20;
	private int xOffset;
	private int yOffset;
	
	public Canvas(Board board){
		this.board = board;
		setBackground(new Color(51,201,137));
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				// TODO Auto-generated method stub
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
			g2d.setColor(new Color(255,240,117));
			AffineTransform trans = new AffineTransform();
		    trans.concatenate(saveTransform);
		    trans.translate(xOffset, yOffset);
		    trans.scale(tileWidth, tileWidth);
		    trans.translate(tile.x, tile.y);
		    g2d.setTransform(trans);
		    g2d.fillPolygon(new Polygon(new int[]{0,1,1,0}, new int[]{0,0,1,1}, 4));
		    g2d.setTransform(saveTransform);
		    trans = new AffineTransform();
		    trans.concatenate(saveTransform);
		    trans.translate(xOffset, yOffset);
		    g2d.setTransform(trans);
		    g2d.setColor(Color.DARK_GRAY);
		    g2d.drawPolygon(createBorderPolygon(new Polygon(new int[]{0,1,1,0}, new int[]{0,0,1,1}, 4), tile.x, tile.y));
			//g2d.drawString("" + loc.getNeighbours().size(), (int) (loc.getShape().getBounds2D().getCenterX()+loc.getX())*tileWidth, (int) (loc.getShape().getBounds2D().getCenterY()+loc.getY())*tileWidth+20);
		    g2d.setTransform(saveTransform);
			
		}
		
		/*g2d.setStroke(new BasicStroke(3));
		for(Room room : board.getRooms()){
			g2d.setColor(new Color(201,193,154));
			AffineTransform trans = new AffineTransform();
		    trans.concatenate(saveTransform);
		    trans.translate(xOffset, yOffset);
		    trans.scale(tileWidth, tileWidth);
		    trans.translate(room.getX(), room.getY());
		    g2d.setTransform(trans);
		    g2d.fillPolygon(room.getShape());
		    g2d.setTransform(saveTransform);
		    trans = new AffineTransform();
		    trans.concatenate(saveTransform);
		    trans.translate(xOffset, yOffset);
		    g2d.setTransform(trans);
		    g2d.setColor(Color.BLACK);
		    g2d.drawPolygon(createBorderPolygon(room.getShape(), room.getX(), room.getY()));
		    Rectangle2D nameBounds = g2d.getFontMetrics().getStringBounds(room.getName(), g2d);
		    g2d.drawString(room.getName(), 
		    		(int) ((room.getShape().getBounds2D().getCenterX()+room.getX())*tileWidth-nameBounds.getWidth()/2), 
		    		(int) ((room.getShape().getBounds2D().getCenterY()+room.getY())*tileWidth+nameBounds.getHeight()/2));
		    for (Location neigh : room.getNeighbours()){
		    	g2d.setColor(new Color(255,240,117));
		    	if (!neigh.isRoom()){
		    		Tile tile = (Tile) neigh;
		    		Direction dir = room.neighboursDirection(tile);
		    		int x = tile.getX();
		    		int y = tile.getY();
		    		switch(dir){
		    		case NORTH:
		    			g2d.drawLine((x)*tileWidth+3, (y+1)*tileWidth, (x+1)*tileWidth-3, (y+1)*tileWidth);
		    			break;
		    		case SOUTH:
		    			g2d.drawLine((x)*tileWidth+3, (y)*tileWidth, (x+1)*tileWidth-3, (y)*tileWidth);
		    			break;
		    		case EAST:
		    			g2d.drawLine((x)*tileWidth, (y)*tileWidth+3, (x)*tileWidth, (y+1)*tileWidth-3);
		    			break;
		    		case WEST:
		    			g2d.drawLine((x+1)*tileWidth, (y)*tileWidth+3, (x+1)*tileWidth, (y+1)*tileWidth-3);
		    		}
 		    	}
		    }*/
			//g2d.drawString("" + loc.getNeighbours().size(), (int) (loc.getShape().getBounds2D().getCenterX()+loc.getX())*tileWidth, (int) (loc.getShape().getBounds2D().getCenterY()+loc.getY())*tileWidth+20);
		//    g2d.setTransform(saveTransform);
		//}
	}

	private Polygon createBorderPolygon(Polygon p, int x, int y){
		int[] xs = new int[p.npoints];
		int[] ys = new int[p.npoints];
		for (int i = 0; i < p.npoints; ++i){
			xs[i] = p.xpoints[i]*tileWidth+x*tileWidth;
			ys[i] = p.ypoints[i]*tileWidth+y*tileWidth;
		}
		return new Polygon(xs, ys, p.npoints);
	}
}
