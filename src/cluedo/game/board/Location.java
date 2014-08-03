package cluedo.game.board;

import java.awt.Point;
import java.awt.Polygon;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class Location {
	
	public enum Direction {NORTH, SOUTH, EAST, WEST}
	
	private int x;
	private int y;
	private boolean isRoom;
	private Polygon shape;
	private Map<Location, Direction> neighbours;
	
	public Location(int x, int y, boolean isRoom, Point... shape){
		this.x = x;
		this.y = y;
		this.isRoom = isRoom;
		neighbours = new HashMap<Location, Direction>();
		this.shape = new Polygon();
		for (Point pt : shape){
			this.shape.addPoint(pt.x, pt.y);
		}
	}
	
	public void addNeighbour(Location neighbour, Direction dir){
		neighbours.put(neighbour, dir);
	}
	
	public Set<Location> getNeighbours(){
		return Collections.<Location>unmodifiableSet(neighbours.keySet());
	}
	
	public Direction neighboursDirection(Location loc){
		return neighbours.get(loc);
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public Point toPoint(){
		return new Point(x, y);
	}

	public boolean isRoom(){
		return isRoom;
	}
	
	public Polygon getShape(){
		return shape;
	}
	
	public abstract boolean hasVacancy();
}
