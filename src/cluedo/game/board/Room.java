package cluedo.game.board;

import java.awt.Point;

public class Room  extends Location{
	private String name;
	
	public Room(String name, int x, int y, Point... pts){
		super(x, y, true, pts);
		this.name = name;
	}
	
	@Override
	public boolean hasVacancy() {
		return true;
	}
	
	public String getName(){
		return name;
	}
	
}
