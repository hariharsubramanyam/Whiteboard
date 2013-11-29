package adts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ui.LobbyGUI;

/**
 * ADT that represents a Lobby. This is a waiting zone for servers to tunnel
 * users into different parts of its code.
 * 
 */
public class LobbyModel {
	private Map<String, List<User>> usersForBoardName;
	private List<User> users;
	private List<Whiteboard> boards;
	private AtomicInteger uniqueUserID;

	public LobbyModel() {
	    uniqueUserID = new AtomicInteger(0);
	    usersForBoard = Collections.synchronizedMap(new HashMap<Whiteboard, List<User>>());
	    users = Collections.synchronizedList(new ArrayList<User>());
	}
	
	/**
	 * @return the list of all the whiteboard names
	 */
	public synchronized List<String> getWhiteboardNames(){
	    
	}

	/**
	 * THe server should use this to add unique client connections to the lobby.
	 * A timestamp is attached with the uniqueID provided globally by the
	 * instance of this Model.
	 */
	public synchronized void addUser() {
		users.put(uniqueUserID.getAndIncrement(), (int) System.currentTimeMillis());
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
		ArrayList<Integer> currentUsers = new ArrayList<Integer>();
		if (usersForBoard.containsKey(w)) {
			currentUsers = (ArrayList<Integer>) this.usersForBoard.get(w);
			currentUsers.add(user);
			this.usersForBoard.put(w, currentUsers);
		}
		else {
			currentUsers.add(user);
			this.usersForBoard.put(w, currentUsers);
		}
		
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
