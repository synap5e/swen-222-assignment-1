package cluedo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
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
import cluedo.controller.network.NetworkPlayerHandler;
import cluedo.controller.player.Player;
import cluedo.model.Board;
import cluedo.view.CluedoFrame;
import cluedo.view.ConfigListener;
import cluedo.view.GUIGameInput;
import cluedo.view.GameConfig;

public class Main {

	public static void main(String[] args) throws IOException {
		final GameConfig gc = new GameConfig();
		
		gc.setConfigListener(new ConfigListener() {

			@Override
			public void onConfigured() {
				// don't start in the GUI thread
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							if (gc.isServerGame()) {
								startServerGame(gc);
							} else {
								startClientGame(gc);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	private static void startClientGame(GameConfig gc) throws IOException {
		Socket connection = new Socket(InetAddress.getByName(gc.getRemoteHost()), gc.getRemotePort());
		
		JsonStreamReader reader = new JsonStreamReader(connection.getInputStream());
	
		JsonObject defs = reader.next();
		JsonObject weaponLocations = reader.next();
		Board board = new Board(defs, weaponLocations);
		CluedoFrame frame = new CluedoFrame(board, defs);
		
		GameSlave gs = new GameSlave(board, new GUIGameInput(frame, gc));
		gs.addGameListener(frame);
		
		gs.startGame(reader, connection.getOutputStream());
		
		connection.close();
	}

	private static void startServerGame(GameConfig gc) throws IOException {
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
		
		GameMaster gm;
		if (gc.getNetworkCount() > 0){
			NetworkPlayerHandler netHandler = new NetworkPlayerHandler(gc.getLocalHost(), gc.getLocalPort(), defs, board);
			gm = new GameMaster(board, netHandler, new GUIGameInput(frame, gc));
		} else {
			gm = new GameMaster(board, new GUIGameInput(frame, gc));
		}
		gm.addGameListener(frame);
		
		gm.createGame();
		gm.startGame();
	}

}
