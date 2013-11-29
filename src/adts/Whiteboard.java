package adts;

import interfaces.BoardInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ADT that represents an instance of a Whiteboard. It implements a
 * BoardInterface since it requires certain server-side methods such as
 * serializing/deserializng pixel information.
 * 
 */
public class Whiteboard implements BoardInterface {
	private final String title;
	private List<String> users;
	private Map<String[], String[]> pixelBoard = Collections
			.synchronizedMap(new HashMap<String[], String[]>());
	private final int width;
	private final int height;

	public Whiteboard(String title, int width, int height) {
		this.title = title;
		this.users = new ArrayList<String>();
		this.width = width;
		this.height = height;
		createEmptyPixelBoard();
	}

	private void createEmptyPixelBoard() {
		String[] whiteColor = new String[] { "255", "255", "255", "255" };
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				String[] position = new String[] { String.valueOf(i),
						String.valueOf(j) };
				pixelBoard.put(position, whiteColor);
			}
		}
	}

	public synchronized void addUser(String user) {
		users.add(user);
	}

	/**
	 * Regardless of whether the pixel exists in the map or not, this method
	 * will override the previous mapping. There is a strong precondition which
	 * dictates that the pixel position must be inside the grid made by a width
	 * by height integer pixels, as well as all colors be in R,G,B,Alpha format
	 * where all values are between 0 and 255.
	 * 
	 * @param pix
	 *            a String array with two values, the x and y coordinates of the
	 *            pixel
	 * @param color
	 *            a String array with four values, the r, g, b, and alpha color
	 *            values of the pixel
	 */
	public synchronized void changePixel(String[] pix, String[] color) {
		this.pixelBoard.put(pix, color);
	}

	public synchronized String getPixel(String[] pix) {
		String[] color = this.pixelBoard.get(pix);
		String result = String.format("%s %s %s %s %s %s", pix[0], pix[1],
				color[0], color[1], color[2], color[3]);
		return result;
	}

	/**
	 * Returns the title of the whiteboard
	 * 
	 * @return a String representation of the whtieboard's title
	 */
	public synchronized String getTitle() {
		return this.title;
	}

	@Override
	public boolean equals(Object _that) {
		// two objects can only be equal if they are of the same type
		if (!(_that instanceof Whiteboard)) {
			return false;
		}
		// if they are, cast the Object into a Whiteboard object and check for
		// equality. This is done by using the Map equals method defined as the
		// two mappings as being equal if their mappings are equal. Since these
		// are String mappings, they are equal when their String values are
		// visibly equal
		Whiteboard that = (Whiteboard) _that;
		return this.pixelBoard.equals(that.pixelBoard);
	}

	@Override
	public int hashCode() {
		return 1; // TODO
	}

}
