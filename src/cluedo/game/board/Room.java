package cluedo.game.board;

import java.awt.Point;
import java.util.List;

/**
 * 
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Room  extends Location implements Card{
	private String name;
	private List<Token> tokens;
	
	public Room(String name){
		this.name = name;
	}
	
	// TODO tokens contained
	
	@Override
	public boolean hasVacancy() {
		return true;
	}
	
	public String getName(){
		return name;
	}
	
}
