package cluedo;

import cluedo.game.board.Board;
import cluedo.gui.CluedoFrame;

public class Main {

	public static void main(String[] args) {
		Board board = new Board();
		CluedoFrame frame = new CluedoFrame(board);
	}

}
