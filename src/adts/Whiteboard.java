package adts;

import interfaces.BoardInterface;

/**
 * ADT that represents an instance of a Whiteboard. It implements a
 * BoardInterface since it requires certain server-side methods such as
 * serializing/deserializng pixel information.
 * 
 */
public class Whiteboard implements BoardInterface {
	private final String title;
	
	public Whiteboard() {
		this.title = "";
	}
	
	@Override
	public boolean equals(Object _that) {
		// two objects can only be equal if they are of the same type
		if (!(_that instanceof Whiteboard)) {
			return false;
		}
		// if they are, cast the Object into a Whiteboard object and check for
		// equality recursively
		Whiteboard that = (Whiteboard) _that;
		return this == that; // TODO
	}

	/**
	 * 
	 * @return the string representation of a Whiteboard
	 */
	@Override
	public String toString() {
		return ""; // TODO
	}

	@Override
	public int hashCode() {
		return 1; // TODO
	}

}
