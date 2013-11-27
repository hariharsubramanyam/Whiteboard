package adts;

/**
 * ADT that represents a Lobby. This is a waiting zone for servers to tunnel
 * users into different parts of its code.
 * 
 */
public class Lobby {
	private final String name;

	public Lobby() {
		this.name = "";
	}

	public boolean equals(Object _that) {
		// two objects can only be equal if they are of the same type
		if (!(_that instanceof Lobby)) {
			return false;
		}
		// if they are, cast the Object into a Lobby object and check for
		// equality recursively
		Lobby that = (Lobby) _that;
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
