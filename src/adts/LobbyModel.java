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
    
    /**
     * A counter used to assign unique ids to each user
     */
    private final AtomicInteger uniqueUserID;
    
    /**
     * A counter used to assign unique ids to each board
     */
    private final AtomicInteger uniqueBoardID;
    
    /**
     * Key = user ID
     * Value = user with the given ID
     */
    private final Map<Integer, User> userForID;
    
    /**
     * Key = board ID
     * Value = board with the given ID 
     */
    private final Map<Integer, Whiteboard> boardForID;
    
    /**
     * Key = board ID
     * Value = list of IDs of users who are using the board with the given ID
     */
    private final Map<Integer, List<Integer>> userIDsForBoardID;

	public LobbyModel() {
	    uniqueUserID = new AtomicInteger(0);
	    uniqueBoardID = new AtomicInteger(0);
	    userForID = Collections.synchronizedMap(new HashMap<Integer, User>());
	    boardForID = Collections.synchronizedMap(new HashMap<Integer, Whiteboard>());
	    userIDsForBoardID = Collections.synchronizedMap(new HashMap<Integer, List<Integer>>());
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
