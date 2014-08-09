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
import cluedo.util.json.JsonObject;

public class CluedoFrame extends JFrame implements GameListener {
	
	private JMenuBar menu;
	
	public CluedoFrame(Board board, JsonObject def){
		setTitle("Cluedo");
		setMinimumSize(new Dimension(600, 700));
		
		menu = new JMenuBar();
		menu.add(new JMenuItem("File"));
		menu.add(new JMenuItem("Game"));
		
		setJMenuBar(menu);
		
		getContentPane().add(new Canvas(board, def));
		pack();
		setVisible(true);
	}

	@Override
	public void onPlayerMove(Character player, int roll, Location destination) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuggestionUndisputed(Character suggester,	Suggestion suggestion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuggestionDisproved(Character suggester, Suggestion suggestion, Character disprover) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccusation(Character accuser, Accusation accusation, boolean correct) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerTurn(Character player) {
		// TODO Auto-generated method stub
		
	}
}
