package adts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ui.LobbyGUI;

/**
 * ADT that represents a Lobby. This is a waiting zone for servers to tunnel
 * users into different parts of its code.
 * 
 */
public class LobbyModel {
	private Map<Whiteboard, Integer> boards = Collections
			.synchronizedMap(new HashMap<Whiteboard, Integer>());
	private Map<Integer, Integer> users = Collections
			.synchronizedMap(new HashMap<Integer, Integer>());
	private final String name;
	private LobbyGUI gui;
	private AtomicInteger uniqueID = new AtomicInteger(0);

	public LobbyModel() {
		this.name = "Whiteboard Lobby";
		this.gui = new LobbyGUI(1);
	}

	/**
	 * THe server should use this to add unique client connections to the lobby.
	 * A timestamp is attached with the uniqueID provided globally by the
	 * instance of this Model.
	 */
	public synchronized void addUser() {
		users.put(uniqueID.getAndIncrement(), (int) System.currentTimeMillis());
	}

	/**
	 * Once the user is ready to connect to an existing Whiteboard, or create a
	 * new one, use this to link them to this board
	 * 
	 * @param w
	 *            the Whiteboard to associate the user to
	 * @param user
	 *            the unique ID of the user
	 */
	public synchronized void linkUser(Whiteboard w, Integer user) {
		if (boards.containsKey(w)) {
			this.boards.put(key, value)
		}
		this.boards.put(w, user);
	}

	/**
	 * 
	 * @return the string representation of a Lobby
	 */
	public String toString() {
		return ""; // TODO
	}

	public int hashCode() {
		return 1; // TODO
	}

}
