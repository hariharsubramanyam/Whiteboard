package adts;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ADT that represents a Lobby. This is a waiting zone for servers to tunnel
 * users into different parts of its code.
 * 
 * Rep Invariant: A user can only be in one board!
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
     * Key = user ID Value = user with the given ID
     */
    private final Map<Integer, User> userForID;

    /**
     * Key = board ID Value = board with the given ID
     */
    private final Map<Integer, Whiteboard> boardForID;

    /**
     * Key = board ID Value = list of IDs of users who are using the board with
     * the given ID
     */
    private final Map<Integer, Set<Integer>> userIDsForBoardID;

    public LobbyModel() {
        uniqueUserID = new AtomicInteger(0);
        uniqueBoardID = new AtomicInteger(0);
        userForID = Collections.synchronizedMap(new HashMap<Integer, User>());
        boardForID = Collections
                .synchronizedMap(new HashMap<Integer, Whiteboard>());
        userIDsForBoardID = Collections
                .synchronizedMap(new HashMap<Integer, Set<Integer>>());
    }

    /**
     * @return the set of all the whiteboard names
     */
    public synchronized Set<String> getWhiteboardNames() {
        Set<String> whiteboardNames = new HashSet<String>();
        for (Whiteboard wb : this.boardForID.values()) {
            whiteboardNames.add(wb.getBoardName());
        }
        return whiteboardNames;
    }

    /**
     * @return the set of all whiteboard IDs
     */
    public synchronized Set<Integer> getWhiteboardIDs() {
        return this.boardForID.keySet();
    }

    /**
     * @param userID
     * @return the user ids of all the users who are in the same board(s) as the
     *         user with the given userID
     */
    public synchronized Set<Integer> getUserIDsOfUsersInSameBoardAsGivenUserID(
            int userID) {
        Set<Integer> userIDs = new HashSet<Integer>();
        // iterate through each boardID
        for (int boardID : this.boardForID.keySet()) {
            // if the set of users ids in that board include the given userID
            if (this.userIDsForBoardID.get(boardID).contains(userID)) {
                userIDs.addAll(this.userIDsForBoardID.get(boardID));
            }
        }
        return userIDs;
    }

    /**
     * @param userID
     *            the id of the user
     * @return the board ID of the board that the user with the given userID is
     *         in, or -1 if the user is not in any board
     */
    public synchronized int getBoardIDThatUserIDIsIn(int userID) {
        for (int boardID : this.boardForID.keySet()) {
            if (this.userIDsForBoardID.get(boardID).contains(userID)) {
                return boardID;
            }
        }
        return -1;
    }

    public synchronized String getUserNameForUserID(int userID) {
        return this.userForID.get(userID).getName();
    }

    /**
     * Returns the user names for the given boardID
     * 
     * @param boardID
     *            the id of the board
     * @return the set of users in the given board
     * @throws IllegalArgumentException
     *             if the boardID does not exist
     */
    public synchronized Set<String> getUserNamesForBoardID(int boardID) {
        if (!(this.boardForID.keySet().contains(boardID)))
            throw new IllegalArgumentException(String.format(
                    "boardID=%d does not exist!", boardID));
        Set<String> userNames = new HashSet<String>();
        for (Integer userID : this.userIDsForBoardID.get(boardID)) {
            userNames.add(this.userForID.get(userID).getName());
        }
        return userNames;
    }

    /**
     * Returns the user names for the given boardName
     * 
     * @param boardName
     *            the name of the board
     * @return the set of users in the given board
     * @throws IllegalArgumentException
     *             if the boardName does not exist
     */
    public synchronized Set<String> getUserNamesForBoardName(String boardName) {
        return getUserNamesForBoardID(getBoardIDForBoardName(boardName));
    }

    /**
     * Change the username for a user with the given userID
     * 
     * @param newName
     *            the name that we should change to
     * @param userID
     *            the id of the user
     * @throws IllegalArgumentException
     *             if the userID does not exist
     */
    public synchronized String changeUserName(String newName, int userID) {
        if (!(this.userForID.keySet().contains(userID)))
            throw new IllegalArgumentException(String.format(
                    "userID=%d does not exist!", userID));
        Set<String> names = new HashSet<String>();
        for(Integer u : this.userForID.keySet()){
            if(u != userID){
                names.add(this.userForID.get(u).getName());
            }
        }
        if(!names.contains(newName)){
            this.userForID.get(userID).setName(newName);
            return newName;
        }
        int incrementer = 1;
        String formattedName = "%s(%d)";
        while(names.contains(String.format(formattedName, newName,incrementer)))
            incrementer++;
        this.userForID.get(userID).setName(String.format(formattedName, newName,incrementer));
        return String.format(formattedName, newName,incrementer);
    }

    /**
     * Change the username for a user with the given userName
     * 
     * @param newName
     *            the name that we should change to
     * @param userName
     *            the name of the user
     * @throws IllegalArgumentException
     *             if the userName does not exist
     */
    public synchronized void changeUserName(String newName, String userName) {
        changeUserName(newName, getUserIDForUserName(userName));
    }

    /**
     * Adds a user to the lobby
     * 
     * @param name
     *            the name of the user
     * @return the id of the user who was added
     */
    public synchronized int addUser(String name) {
        int id = this.uniqueUserID.getAndIncrement();
        this.userForID.put(id, new User(id, name));
        return id;
    }

    /**
     * Adds a user to the lobby with an automatically assigned name
     * 
     * @return the id of the user who was added
     */
    public synchronized int addUser() {
        int id = this.uniqueUserID.getAndIncrement();
        this.userForID.put(id, new User(id));
        this.changeUserName(this.userForID.get(id).getName(), id);
        return id;
    }

    /**
     * Adds a board to the lobby
     * 
     * @param name
     *            the name of the board
     * @param width
     *            the width of the board
     * @param height
     *            the height of the board
     * @return the id of the board that was added
     */
    public synchronized int addBoard(String name, int width, int height) {
        int id = this.uniqueBoardID.getAndIncrement();
        Whiteboard board = new Whiteboard(id, name, width, height);
        this.userIDsForBoardID.put(id, new HashSet<Integer>());
        Set<String> userNames = new HashSet<String>();
        for(Whiteboard brd: this.boardForID.values()){
            userNames.add(brd.getBoardName());
        }
        if(!userNames.contains(name)){
            this.boardForID.put(id, board);
            return id;
        }
        int incrementer = 1;
        while(userNames.contains(String.format("%s(%d)",name,incrementer))){
            incrementer++;
        }
        this.boardForID.get(id).setBoardName(String.format("%s(%d)",name,incrementer));
        return id;
    }

    /**
     * Adds a board to the lobby with a default height and width
     * 
     * @param name
     *            the name of the board
     * @return the id of the board that was added
     */
    public synchronized int addBoard(String name) {
        return this.addBoard(name, Whiteboard.DEFAULT_WIDTH, Whiteboard.DEFAULT_HEIGHT);
    }

    /**
     * Adds a board to the lobby with an automatically generated name and
     * default height and width
     * 
     * @return the id of the board that was added
     */
    public synchronized int addBoard() {
        return this.addBoard("Board", Whiteboard.DEFAULT_WIDTH, Whiteboard.DEFAULT_HEIGHT);
    }

    /**
     * Adds the user with the given userID to the board with the given boardID.
     * If the user is in a board (different from the one with the given
     * boardID), then the user is removed from that board
     * 
     * @param userID
     *            the id of the user to be added
     * @param boardID
     *            the id of the board that the user should be added to
     * @throws IllegalArgumentException
     *             if the userID or boardID do not exist
     */
    public synchronized void userJoinBoard(int userID, int boardID) {
        if (!(this.boardForID.keySet().contains(boardID)))
            throw new IllegalArgumentException(String.format(
                    "boardID=%d does not exist!", boardID));
        if (!(this.userForID.keySet().contains(userID)))
            throw new IllegalArgumentException(String.format(
                    "userID=%d does not exist!", userID));
        for (int bID : this.boardForID.keySet()) {
            if (this.userIDsForBoardID.get(bID).contains(userID)) {
                this.userIDsForBoardID.get(bID).remove(userID);
            }
        }
        Set<Integer> userIDs = this.userIDsForBoardID.get(boardID);
        userIDs.add(userID);
    }

    /**
     * Adds the user with the given userID to the board with the given boardName
     * 
     * @param userID
     *            the id of the user to be added
     * @param boardName
     *            the name of the board that the user should be added to
     * @throws IllegalArgumentException
     *             if the userID or boardName do not exist
     */
    public synchronized void userJoinBoard(int userID, String boardName) {
        if (!(this.userForID.keySet().contains(userID)))
            throw new IllegalArgumentException(String.format(
                    "userID=%d does not exist!", userID));
        int boardID = getBoardIDForBoardName(boardName);
        for (int bID : this.boardForID.keySet()) {
            if (this.userIDsForBoardID.get(bID).contains(userID)) {
                this.userLeaveBoard(userID, bID);
            }
        }
        Set<Integer> userIDs = this.userIDsForBoardID.get(boardID);
        userIDs.add(userID);
    }

    /**
     * Removes the user with the given userID from the board with the given
     * boardID
     * 
     * @param userID
     *            the id of the user to be added
     * @param boardID
     *            the id of the board that the user should be removed from
     * @throws IllegalArgumentException
     *             if the userID or boardID do not exist
     */
    public synchronized void userLeaveBoard(int userID, int boardID) {
        if (!(this.boardForID.keySet().contains(boardID)))
            throw new IllegalArgumentException(String.format(
                    "boardID=%d does not exist!", boardID));
        if (!(this.userForID.keySet().contains(userID)))
            throw new IllegalArgumentException(String.format(
                    "userID=%d does not exist!", userID));
        Set<Integer> userIDs = this.userIDsForBoardID.get(boardID);
        userIDs.remove(userID);
    }

    /**
     * Removes the user with the given userID to the board with the given
     * boardName
     * 
     * @param userID
     *            the id of the user to be added
     * @param boardName
     *            the name of the board that the user should be removed from
     * @throws IllegalArgumentException
     *             if the userID or boardName do not exist
     */
    public synchronized void userLeaveBoard(int userID, String boardName) {
        if (!(this.userForID.keySet().contains(userID)))
            throw new IllegalArgumentException(String.format(
                    "userID=%d does not exist!", userID));
        int boardID = getBoardIDForBoardName(boardName);
        Set<Integer> userIDs = this.userIDsForBoardID.get(boardID);
        userIDs.remove(userID);
    }

    /**
     * Returns the boardID for the given boardName
     * 
     * @param boardName
     *            the name of the board
     * @return the id of the board with the given name
     * @throws IllegalArgument
     *             exception if the boardName does not exist
     */
    private int getBoardIDForBoardName(String boardName) {
        int boardID = -1;
        for (Whiteboard wb : this.boardForID.values()) {
            if (wb.getBoardName().equals(boardName)) {
                boardID = wb.getBoardID();
            }
        }
        if (boardID == -1)
            throw new IllegalArgumentException(String.format(
                    "the board name = %s does not exist!", boardName));
        return boardID;
    }

    /**
     * Returns the userID for the given userName
     * 
     * @param userName
     *            the name of the user
     * @return the id of the user with the given name
     * @throws IllegalArgument
     *             exception if the userName does not exist
     */
    private int getUserIDForUserName(String userName) {
        int userID = -1;
        for (User user : this.userForID.values()) {
            if (user.getName().equals(userName)) {
                userID = user.getID();
            }
        }
        if (userID == -1)
            throw new IllegalArgumentException(String.format(
                    "the user name = %s does not exist!", userName));
        return userID;
    }

    /**
     * Delete user from set of users and remove the user from all the boards
     * 
     * @param userID
     *            the id of the user to delete
     */
    public void deleteUser(int userID) {
        this.userForID.remove(userID);
        for (int boardID : this.userIDsForBoardID.keySet()) {
            this.userIDsForBoardID.get(boardID).remove(userID);
        }
    }

    /**
     * Returns the user ids in the board with the given boardID
     * 
     * @param boardID
     *            the id of the board
     * @return the set of user ids of the users in the board with the given
     *         board id
     */
    public Set<Integer> getUserIDsForBoardID(int boardID) {
        return this.userIDsForBoardID.get(boardID);
    }

    /**
     * Draws a line on a board
     * 
     * @param l
     *            the line to add
     * @param boardID
     *            the id of the board we should add the line to
     */
    public void addLineToBoardID(Line l, int boardID) {
        if (!(this.boardForID.keySet().contains(boardID)))
            throw new IllegalArgumentException(String.format(
                    "boardID=%d does not exist!", boardID));
        this.boardForID.get(boardID).addLine(l);
    }

    /**
     * Gets the lines for the board with the given boardID
     * 
     * @param boardID
     *            the id of the board
     * @return the lines on that board
     */
    public List<Line> getLinesForBoardID(int boardID) {
        if (!(this.boardForID.keySet().contains(boardID)))
            throw new IllegalArgumentException(String.format(
                    "boardID=%d does not exist!", boardID));
        return this.boardForID.get(boardID).getLines();
    }
    
    /**
     * Clears the board with the given ID
     * @param boardID the board to clear
     */
    public void clearBoard(int boardID){
        this.boardForID.get(boardID).clearBoard();
    }
  
    /**
     * @return the whiteboards
     */
    public Collection<Whiteboard> getWhiteboards(){
        return this.boardForID.values();
    }
}
