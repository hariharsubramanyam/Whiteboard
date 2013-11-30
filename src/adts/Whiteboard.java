package adts;

import java.util.ArrayList;
import java.util.List;

/**
 * ADT that represents an instance of a Whiteboard.
 */
public class Whiteboard {
    
    public final static int DEFAULT_WIDTH = 600;
    public final static int DEFAULT_HEIGHT = 400;
    
    /**
     * The ID of this board, does not change! 
     */
    private final int boardID;
    
	/**
	 * The name of this board
	 */
    private String boardName;
    
    /**
     * The list of lines that have been drawn 
     * The last line is the latest one that has been drawn
     */
    private final List<Line> drawnLines;
	
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
		this.drawnLines = new ArrayList<Line>();
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
	 * @param l the line to add to the list of drawn lines
	 */
	public void addLine(Line l){
	    this.drawnLines.add(l);
	}
	
	/**
	 * @return all the drawn lines
	 */
	public List<Line> getLines(){
	    return this.drawnLines;
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
	 * sets the name of the board
	 * @param boardName the new name of the board
	 */
    public synchronized void setBoardName(String boardName){
        this.boardName = boardName;
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
}
