package cluedo.game.board;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import cluedo.game.board.Location.Direction;
import cluedo.util.json.JsonObject;

public class Board {
	private Set<Room> rooms;
	private Set<Tile> tiles;
	
	private Room kitchen = new Room("Kitchen", 0, 1, new Point(0,0), new Point(6,0), new Point(6,6), new Point(1,6), new Point(1,5), new Point(0,5));
	private Room ballroom = new Room("Ball Room", 8, 1, new Point(2,0), new Point(6,0), new Point(6,1), new Point(8,1),
														new Point(8,7), new Point(0,7), new Point(0,1), new Point(2,1));
	private Room conservatory = new Room("Conservatory", 18, 1, new Point(0,0), new Point(6,0), new Point(6,4), new Point(5,4),
																new Point(5,5), new Point(1,5), new Point(1,4), new Point(0,4));
	private Room billiard = new Room("Billiard Room", 18, 8, new Point(0,0), new Point(6,0), new Point(6,5), new Point(0,5));
	private Room library = new Room("Library", 17, 14, new Point(1,0), new Point(6,0), new Point(6,1), new Point(7,1), new Point(7,4), new Point(6,4), 
														new Point(6,5), new Point(1,5), new Point(1,4), new Point(0,4), new Point(0,1), new Point(1,1));
	private Room study = new Room("Study", 17, 21, new Point(0,0), new Point(7,0), new Point(7,4), new Point(1,4), new Point(1,3), new Point(0,3));
	private Room hall = new Room("Hall", 9, 18, new Point(0,0), new Point(6,0), new Point(6,7), new Point(0,7));
	private Room lounge = new Room("Lounge", 0, 19, new Point(0,0), new Point(7,0), new Point(7,5), new Point(6,5), new Point(6,6), new Point(0,6));
	private Room dining = new Room("Dining Room", 0, 9, new Point(0,0), new Point(5,0), new Point(5,1), new Point(8,1), new Point(8,7), new Point(0,7));
	
	private int[][] boardLayout = new int[][]{{0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,0,0,0,0,0,0,0},
											  {0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0},
											  {0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1},
											  {1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0},
											  {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,0},
											  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,1,1,0,0,0,0,0,0,0},
											  {0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,1,0,0,0,0,0,0,0},
											  {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0},
											  {0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1},
											  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0},
											  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0},
											  {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0}};
	private Location[][] board;
	
	public Board(JsonObject defs){
		// TODO: read from defs
		
		rooms = new LinkedHashSet<Room>();
		tiles = new LinkedHashSet<Tile>();
		board = new Location[24][25];
		
		rooms.add(kitchen);
		board[0][0] = kitchen; //to neighbour the study
		board[4][6] = kitchen;
		
		rooms.add(ballroom);
		board[8][5] = ballroom;
		board[9][7] = ballroom;
		board[14][7] = ballroom;
		board[15][5] = ballroom;
		
		rooms.add(conservatory);
		board[0][2] = conservatory; //to neighbour the lounge
		board[1][4] = conservatory; 

		rooms.add(billiard);
		board[18][9] = billiard;
		board[22][12] = billiard;
		
		rooms.add(library);
		board[20][14] = library;
		board[17][16] = library;
		
		rooms.add(study);
		board[23][24] = study;
		board[1][0] = study; //to neighbour the kitchen
		
		rooms.add(hall);
		board[14][20] = hall;
		board[13][22] = hall;
		board[10][22] = hall;
		
		rooms.add(lounge);
		board[1][2] = lounge; //to neighbour the conservatory
		board[0][24] = lounge;
		
		rooms.add(dining);
		board[7][12] = dining;
		board[6][15] = dining;
		
		//Create corridors
		for (int x = 0; x < 24; ++x){
			for (int y = 0; y < 25; ++y){
				if (boardLayout[y][x] == 1){
					board[x][y] = new Tile(x, y);
					tiles.add((Tile) board[x][y]);
				}
			}
		}
		
		board[0][4] = board[18][5]; //tile that neighbours conservatory
		board[23][23] = board[17][20]; // tile that neighbours study
		board[13][21] = board[12][17]; //tile hall
		board[10][21] = board[11][17]; //tile hall
		board[0][23] = board[6][18]; //tile lounge 
		
		for (int x = 0; x < 23; ++x){
			for (int y = 0; y < 25; ++y){
				if (board[x][y] != null && board[x+1][y] != null){
					board[x][y].addNeighbour(board[x+1][y], Direction.EAST);
					board[x+1][y].addNeighbour(board[x][y], Direction.WEST);
				}
			}
		}
		for (int y = 0; y < 24; ++y){
			for (int x = 0; x < 24; ++x){
				if (board[x][y] != null && board[x][y+1] != null){
					board[x][y].addNeighbour(board[x][y+1], Direction.SOUTH);
					board[x][y+1].addNeighbour(board[x][y], Direction.NORTH);
				}
			}
		}
	}
	
	public Set<Room> getRooms(){
		return Collections.<Room>unmodifiableSet(rooms);
	}
	
	public Set<Tile> getTiles(){
		return Collections.<Tile>unmodifiableSet(tiles);
	}
}
