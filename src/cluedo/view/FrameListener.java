package cluedo.view;

import cluedo.model.Location;
import cluedo.model.card.Token;

/**
 * FrameListener listens for events from the CluedoFrame
 * 
 * @author James Greenwood-Thessman
 */
public interface FrameListener {
	/*
	 * Events related to the canvas
	 */
	/**
	 * A location was selected
	 * 
	 * @param loc the location selected
	 */
	public void onLocationSelect(Location loc);
	
	/**
	 * A token was selected 
	 * 
	 * @param token the token selected
	 */
	public void onTokenSelect(Token token);
	
	/*
	 * Events related to users 'initiating' sequences
	 */
	/**
	 * The user decided to roll the dice
	 */
	public void onRollDice();
	
	/**
	 * The user decided to make a suggestion
	 */
	public void onSuggest();
	
	/**
	 * The user decided to make an accusation
	 */
	public void onAccuse();
	
	/**
	 * The user decided to end their turn
	 */
	public void onEndTurn();
}
