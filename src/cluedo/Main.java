package cluedo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import util.json.JsonObject;
import util.json.JsonParseException;
import util.json.JsonStreamReader;
import util.json.MinimalJson;
import cluedo.controller.GameMaster;
import cluedo.controller.GameSlave;
import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.Player;
import cluedo.model.Board;
import cluedo.view.CluedoFrame;
import cluedo.view.GUIGameInput;

public class Main {

	public static void main(String[] args) throws IOException {
		if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(null, "Run as client game?", "", JOptionPane.YES_NO_OPTION)){
			JsonObject defs = null;
			try {
				defs = MinimalJson.parseJson(new File("./rules/cards.json"));
			} catch (FileNotFoundException | JsonParseException e) {
				System.err.println("Could not load card definitions");
				e.printStackTrace();
				// TODO: GUI display?
				System.exit(-1);
			}
			System.out.println(defs);
			startServerGame(defs);
		} else {
			startClientGame("127.0.0.1", 5362);
		}
	}

	private static void startClientGame(String address, int port) throws IOException {
		Socket connection = new Socket(InetAddress.getByName(address), port);
		
		JsonStreamReader reader = new JsonStreamReader(connection.getInputStream());
	
		JsonObject defs = reader.next();
		Board board = new Board(defs);
		CluedoFrame frame = new CluedoFrame(board, defs);
		
		GameSlave gs = new GameSlave(board, new GUIGameInput(frame));
		gs.addGameListener(frame);
		
		gs.startGame(reader, connection.getOutputStream());
		
		connection.close();
	}

	private static void startServerGame(JsonObject defs) throws IOException {
		Board board = new Board(defs);
		CluedoFrame frame = new CluedoFrame(board, defs);
		GameMaster gm = new GameMaster(board, defs, new GUIGameInput(frame));
		gm.addGameListener(frame);
		
		gm.createGame();
		gm.startGame();
	}

}
