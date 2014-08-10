package cluedo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import util.json.JsonObject;
import util.json.JsonParseException;
import util.json.MinimalJson;
import cluedo.controller.GameMaster;
import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.Player;
import cluedo.model.Board;
import cluedo.view.CluedoFrame;
import cluedo.view.GUIGameInput;

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
		GameMaster gm = new GameMaster(board, new GUIGameInput(frame));
		gm.addGameListener(frame);
		
		gm.createGame();
		gm.startGame();
	}

}
