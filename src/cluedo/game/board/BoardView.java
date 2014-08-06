package cluedo.game.board;

import java.util.Set;

public interface BoardView {

	public Location getLocation(Character token);

	public Set<Room> getRooms();

	public Set<Tile> getTiles();

}
