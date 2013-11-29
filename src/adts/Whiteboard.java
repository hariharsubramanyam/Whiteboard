package adts;

/**
 * ADT that represents an instance of a Whiteboard.
 */
public class Whiteboard {
    
    /**
     * The ID of this board, does not change! 
     */
    private final int boardID;
    
	/**
	 * The name of this board
	 */
    private String boardName;
    
    /**
     * The board contents.
     * The pixel at (x,y) has an RGBA color given by pixelBoard[x][y]
     */
    private final RGBA[][] pixelBoard;
	
	/**
	 * The width of the board
	 */
	private final int width;
	
	/**
	 * The height of the board
	 */
	private final int height;

	/**
	 * Creates a board with the given boardID, boardName, width, and height. 
	 * The board is cleared such that all pixels are white.
	 * @param boardID the ID of the board
	 * @param boardName the name of the board
	 * @param width the width of the board
	 * @param height this height of the board
	 */
	public Whiteboard(int boardID, String boardName, int width, int height) {
	    this.boardID = boardID;
	    this.boardName = boardName;
		this.width = width;
		this.height = height;
		this.pixelBoard = new RGBA[width][height];
		clearBoard();
	}
	
	/**
     * Creates a board with the given boardID width, and height. 
     * The board is cleared such that all pixels are white.
     * The boardName is "Board"+boardID (ex. if boardID = 2, the boardName is "Board2") 
     * @param boardID the ID of the board
     * @param width the width of the board
     * @param height this height of the board
     */
	public Whiteboard(int boardID, int width, int height){
	    this(boardID, "Board"+boardID, width, height);
	}

	/**
	 * Makes all pixels white
	 */
	public synchronized void clearBoard() {
	    RGBA white = new RGBA(255,255,255,255);
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
			    this.pixelBoard[i][j] = white;
			}
		}
	}
	
	/**
	 * Sets the pixel at (x,y) to color 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param color the color that the pixel should be
	 */
	public synchronized void setPixel(int x, int y, RGBA color){
	    this.pixelBoard[x][y] = color;
	}
	
	/**
	 * Returns the color of the pixel at (x,y)
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the color of the pixel at (x,y)
	 */
	public synchronized RGBA getPixel(int x, int y){
	    return this.pixelBoard[x][y];
	}
	
	/**
	 * @return the ID of the board
	 */
	public synchronized int getBoardID(){
	    return this.boardID;
	}
	
	/**
	 * @return the name of the board
	 */
	public synchronized String getBoardName(){
	    return this.boardName;
	}
	
	/**
	 * @return the width of the board
	 */
	public synchronized int getWidth(){
	    return this.width;
	}
	
	/**
	 * @return the height of the board
	 */
	public synchronized int getHeight(){
	    return this.height;
	}
	
	/**
	 * @param the object to check equality against
	 * @return true if both objects have the same boardID, boardName, height, width, and board contents
	 */
	@Override
	public boolean equals(Object obj) {
	    if(!(obj instanceof Whiteboard))
	        return false;
	    Whiteboard other = (Whiteboard)obj;
	    
	    if(!(other.getBoardName().equals(this.boardName)
	            && other.getWidth() == this.width
	            && other.getHeight() == this.height
	            && other.getBoardID() == this.boardID))
	        return false;
	    
	    for(int i = 0; i < this.width; i++){
	        for(int j = 0; j < this.height; j++){
	            if(!this.getPixel(i, j).equals(other.getPixel(i, j)))
	                return false;
	        }
	    }
	    
	    return true;
	}
	
	/**
	 * @return A string of the form
	 * <boardName> <width> <height> <r1> <g1> <b1> <a1> <r2> <g2> <b2> <a2> ...
	 * 
	 *  The r, g, b, and a values are listed row by row
	 */
	@Override
	public String toString() {
	    StringBuilder buildBoardAsString = new StringBuilder();
	    buildBoardAsString.append(String.format("%s %d %d", this.boardName, this.width, this.height));
	    for(int i = 0; i < this.width; i++){
	        for(int j = 0; j < this.height; j++){
	            buildBoardAsString.append(" " + this.getPixel(i, j).toString());
	        }
	    }
	    return buildBoardAsString.toString();
	}
	
	/**
	 * @return the hashcode of this class's string representation concatenated after the boardID 
	 */
	@Override
	public int hashCode() {
	    return ("" + this.getBoardID() + " " + this.toString()).hashCode();
	}
}
