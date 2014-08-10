package cluedo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cluedo.game.GameListener;
import cluedo.game.GameMaster;
import cluedo.game.Player;
import cluedo.game.board.Board;
import cluedo.gui.CluedoFrame;
import cluedo.gui.GUIHandle;
import cluedo.gui.GUIHandleImpl;
import cluedo.util.json.JsonObject;
import cluedo.util.json.JsonParseException;
import cluedo.util.json.MinimalJson;

public class Main {

	public static void main(String[] args) {
		JsonObject defs = null;
		try {
			defs = MinimalJson.parseJson(new File("./rules/cards.json"));
		} catch (FileNotFoundException | JsonParseException e) {
			System.err.println("Could not load card definitions");
			e.printStackTrace();
			// TODO: GUI display?
			System.exit(-1);
		}
		Board board = new Board(defs);
		CluedoFrame frame = new CluedoFrame(board, defs);
		GameMaster gm = new GameMaster(board, new GUIHandleImpl());
		gm.addGameListener(frame);
		
		gm.createGame();
		gm.startGame();
	}

}
