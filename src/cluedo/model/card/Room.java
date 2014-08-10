package cluedo.model.card;

import java.util.ArrayList;
import java.util.List;

import cluedo.model.Location;

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
		tokens = new ArrayList<Token>();
	}
	
	@Override
	public boolean hasVacancy() {
		return true;
	}
	
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
	
	
	
}
