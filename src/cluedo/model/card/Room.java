package cluedo.model.card;

import java.util.ArrayList;
import java.util.List;

import cluedo.model.Location;

/**
 * Room represents the location on the board as well as a card in the game Cluedo.
 * 
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class Room  extends Location implements Card{
	
	/**
	 * The name of the room
	 */
	private String name;
	
	/**
	 * The tokens held in room
	 */
	private List<Token> tokens;
	
	/**
	 * Create the room with the given name
	 * 
	 * @param name the name of the room
	 */
	public Room(String name){
		this.name = name;
		tokens = new ArrayList<Token>();
	}
	
	@Override
	public boolean hasVacancy() {
		//Rooms always have vacancies
		return true;
	}
	
	@Override
	public String getName(){
		return name;
	}

	@Override
	public void addToken(Token token) {
		tokens.add(token);
	}
	
	@Override
	public List<Token> getTokens(){
		return new ArrayList<Token>(tokens);
	}

	@Override
	public void removeToken(Token token) {
		tokens.remove(token);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
