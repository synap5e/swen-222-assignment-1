package cluedo.game.board;

import java.awt.Point;
import java.util.List;

public class Room  extends Location implements Card{
	private String name;
	private List<Token> tokens;
	
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
