package adts;

/**
 * ADT that represents a Pixel. This is a used by a Whiteboard to keep track of
 * its state.
 * 
 */
public class Pixel {
	private final String[] position = new String[2];
	private final String[] color = new String[4];

	public Pixel(String x, String y, String r, String g, String b, String alpha) {
		this.position[0] = x;
		this.position[1] = y;

		this.color[0] = r;
		this.color[1] = g;
		this.color[2] = b;
		this.color[3] = alpha;
	}

	/**
	 * The act of Serializing creates a String response ready for deployment.
	 * This response follows the "RESTful" idea of creating a language-less mode
	 * of communication between a server and a client.
	 * 
	 * The format of PixelResponses is very specific:
	 * 
	 * a left curly brace
	 * 
	 * a whitespace
	 * 
	 * the word coordinates, a whitespace, a colon, a whitespace, a left
	 * parenthesis
	 * 
	 * the x-position
	 * 
	 * a comma and a whitespace
	 * 
	 * the y-position
	 * 
	 * a right parenthesis, a comma, a whitespace, the word color, a whitespace,
	 * a colon, a whitespace, a left parenthesis
	 * 
	 * each of the four color components separated by a comma and a whitespace
	 * 
	 * a right parenthesis, a right curly brace
	 * 
	 * @return the serialized object
	 */
	public synchronized String getSerializedResponse() {
	    return String.format("%s %s %s %s %s %s", 
	            this.position[0], 
	            this.position[1], 
	            this.position[2], 
	            this.color[0], 
	            this.color[1], 
	            this.color[2], 
	            this.color[3]); 
	}

	public boolean equals(Object _that) {
		// two objects can only be equal if they are of the same type
		if (!(_that instanceof Pixel)) {
			return false;
		}
		// if they are, cast the Object into a Lobby object and check for
		// equality recursively
		Pixel that = (Pixel) _that;
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
