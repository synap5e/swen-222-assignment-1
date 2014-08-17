package cluedo.controller.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import util.json.JsonObject;
import cluedo.model.Board;

/** This class provides a way of getting a  bidirectional channel to a client game, so
 * that input can be requested from them and evnets can be sent.
 * 
 * @author Simon Pinfold
 *
 */
public class NetworkPlayerHandler {

	private ServerSocket socket;
	private JsonObject defs;
	private Board board;

	/** Bind to the specified address and port. Connections to GameSlaves can then be 
	 * accepted with getRemoteInput.
	 * 
	 * @param bind the address of the interface to bind on. "0.0.0.0" for all interfaces.
	 * @param port the port to bind on
	 * @param defs the game configuration to send to the client so they play with the same rules
	 * @param board The board that will be used for the game
	 * @throws IOException
	 */
	public NetworkPlayerHandler(String bind, int port, JsonObject defs, Board board) throws IOException {
		this.socket = new ServerSocket(port, 50, InetAddress.getByName(bind));
		this.defs = defs;
		this.board = board;
	}

	/** Listen for a connection then return a set up ServerGameChannel to the GameSlave at the other end. 
	 * This includes sending the game configuration and board state. As well as being the GameInput for 
	 * the GameSlave ServerGameChannel is also a GameListener, and must be added to the list of listeners.
	 * 
	 * @return the new ServerGameChannel once a client connects 
	 * @throws IOException on network error
	 */
	public ServerGameChannel getRemoteInput() throws IOException {
		Socket con = socket.accept();
		OutputStream os = con.getOutputStream();
		os.write(defs.toString().getBytes());
		os.write(ModelToJson.weaponLocationsToJson(board).toString().getBytes());
		return new ServerGameChannel(os, con.getInputStream(), board);
	}
	
	/** Close down the socket
	 * 
	 * @throws IOException
	 */
	public void shutdown() throws IOException {
		this.socket.close();
	}

}
