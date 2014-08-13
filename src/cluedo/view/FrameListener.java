package cluedo.view;

import java.util.List;

import cluedo.model.Location;
import cluedo.model.card.Token;

/**
 * 
 * @author James Greenwood-Thessman
 *
 */
public interface FrameListener {
	/*
	 * Events related to the canvas
	 */
	public void onLocationSelect(Location loc);
	
	public void onTokenSelect(Token token);
	
	/*
	 * Events related to game setup (as part of the frame)
	 */
	public void onNumberOfPlayers(int num);
	
	public void onPlayerSelection(List<String> names, int numberAI, int numberNetwork);
	
	public void onSinglePlayerName(String name);
	
	/*
	 * Events related to users 'initiating' sequences
	 */
	public void onRollDice();
	
	public void onSuggest();
	
	public void onAccuse();
	
	public void onEndTurn();
}
