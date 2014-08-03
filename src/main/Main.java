package main;

import game.Board;
import gui.CluedoFrame;

public class Main {

	public static void main(String[] args) {
		Board board = new Board();
		CluedoFrame frame = new CluedoFrame(board);
	}

}
