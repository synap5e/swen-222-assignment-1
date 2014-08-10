package cluedo.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import cluedo.game.GameListener;
import cluedo.game.board.Accusation;
import cluedo.game.board.Board;
import cluedo.game.board.Character;
import cluedo.game.board.Location;
import cluedo.game.board.Suggestion;
import cluedo.game.board.Weapon;
import cluedo.util.json.JsonObject;

/**
 * 
 * @author James Greenwood-Thessman, Simon Pinfold
 *
 */
public class CluedoFrame extends JFrame implements GameListener {
	
	private JMenuBar menu;
	private Canvas canvas;
	
	public CluedoFrame(Board board, JsonObject def){
		setTitle("Cluedo");
		setMinimumSize(new Dimension(600, 700));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menu = new JMenuBar();
		menu.add(new JMenuItem("File"));
		menu.add(new JMenuItem("Game"));
		
		setJMenuBar(menu);
		
		this.canvas = new Canvas(board, def);
		getContentPane().add(canvas);
		pack();
		setVisible(true);
	}
	
	public Canvas getCanvas(){
		return canvas;
	}

	@Override
	public void onCharacterJoinedGame(String playerName, Character character, boolean humanPlayer) {
		canvas.onCharacterJoinedGame(playerName, character, humanPlayer);
	}

	public void onTurnBegin(String name, Character playersCharacter) {
		canvas.onTurnBegin(name, playersCharacter);
	}
	
	@Override
	public void onDiceRolled(int roll) {
		canvas.onDiceRolled(roll);
	}

	@Override
	public void onCharacterMove(Character character, Location destination) {
		canvas.onCharacterMove(character, destination);
	}

	@Override
	public void onSuggestionUndisputed(Character suggester,	Suggestion suggestion) {
		canvas.onSuggestionUndisputed(suggester, suggestion);
	}

	@Override
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Character disprover) {
		canvas.onSuggestionDisproved(suggester, suggestion, disprover);
	}

	@Override
	public void onAccusation(Character accuser, Accusation accusation, boolean correct) {
		canvas.onAccusation(accuser, accusation, correct);
	}

	@Override
	public void onWeaponMove(Weapon weapon, Location room) {
		canvas.onWeaponMove(weapon, room);
	}

	@Override
	public void onGameWon(String name, Character playersCharacter) {
		canvas.onGameWon(name, playersCharacter);
	}


}
