package cluedo.game.board;

import java.awt.Point;

public class Tile extends Location{

	private Character occupient;
	
	public Tile(int x, int y) {
		super(x, y, false, new Point(0,0), new Point(1,0), new Point(1,1), new Point(0,1));
	}

	@Override
	public boolean hasVacancy() {
		return occupient == null;
	}
}
