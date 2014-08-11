package cluedo.controller.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import sun.org.mozilla.javascript.json.JsonParser;
import util.json.JsonObject;
import cluedo.controller.interaction.GameInput;
import cluedo.controller.interaction.GameListener;
import cluedo.controller.player.Player.PlayerType;
import cluedo.model.Location;
import cluedo.model.card.Character;
import cluedo.model.card.Weapon;
import cluedo.model.cardcollection.Accusation;
import cluedo.model.cardcollection.Suggestion;

public class NetworkPlayerHandler {

	private ServerSocket socket;
	private List<OutputStream> clients;
	private JsonObject defs;

	public NetworkPlayerHandler(String bind, int port, JsonObject defs) throws IOException {
		this.socket = new ServerSocket(port, 50, InetAddress.getByName(bind));
		this.defs = defs;
	}

	public NetworkGameChannel getRemoteInput(int timeout) throws IOException {
		Socket con = socket.accept();
		OutputStream os = con.getOutputStream();
		os.write(defs.toString().getBytes());
		return new NetworkGameChannel(os, con.getInputStream());
	}

}
