package adts;

/**
 * ADT that represents a Lobby. This is a waiting zone for servers to tunnel
 * users into different parts of its code.
 * 
 */
public class LobbyModel {
	private final String name;
	private LobbyGUI gui;
	
	public LobbyModel(int id) {
		this.name = "Whiteboard Lobby";
		this.gui = new LobbyGUI(id);
	}

	public boolean equals(Object _that) {
		// two objects can only be equal if they are of the same type
		if (!(_that instanceof LobbyModel)) {
			return false;
		}
		// if they are, cast the Object into a Lobby object and check for
		// equality recursively
		LobbyModel that = (LobbyModel) _that;
		return this == that; // TODO
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
