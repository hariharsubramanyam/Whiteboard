package interfaces;

/**
 * Interface that represents any board. This could be a Whiteboard or any other
 * type of drawing Board. The idea here is that if we have time leftover, we can
 * create other types of Boards like Blackboards, or GraphingBoards. The objects
 * are mutable. The equals, toString, and hashCode methods work generically.
 * Read their documentation for full specs.
 * 
 **/

/*
 * Representation BoardInterface = Whiteboard(board: Whiteboard, users:
 * List<Users>)
 */
public interface BoardInterface {

	/**
	 * Tests the equality of one BoardInterface to to another, such that two
	 * expressions with equal attributes (observationally indistinguishable) are
	 * considered equal
	 * 
	 * @param _that
	 *            expression to compare to
	 * @return whether or not the two Boards are equal
	 */
	@Override
	public boolean equals(Object _that);

	/**
	 * Returns the string representation of the BoardInterface
	 * 
	 * @returns the BoardInterface as a string
	 */
	@Override
	public String toString();

	/**
	 * Calculates the hashcode for this BoardInterface. HashCode for two equal
	 * Boards will be identical.
	 * 
	 * @return the hashcode for the BoardInterface
	 */
	@Override
	public int hashCode();
}
