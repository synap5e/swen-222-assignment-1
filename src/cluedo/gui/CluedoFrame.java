package cluedo.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import cluedo.game.board.Board;

public class CluedoFrame extends JFrame {
	
	private JMenuBar menu;
	
	public CluedoFrame(Board board){
		setTitle("Cluedo");
		setMinimumSize(new Dimension(480, 550));
		
		menu = new JMenuBar();
		menu.add(new JMenuItem("File"));
		menu.add(new JMenuItem("Game"));
		
		setJMenuBar(menu);
		
		getContentPane().add(new Canvas(board));
		pack();
		setVisible(true);
	}
}
