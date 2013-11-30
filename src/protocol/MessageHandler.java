package protocol;

import java.util.HashSet;
import java.util.Set;

import adts.LobbyModel;
import server.UserThread;

public class MessageHandler {
    public static final String REQ_GET_BOARD_IDS = "get_board_ids";
    public static final String REQ_SET_USERNAME = "set_username";
    public static final String REQ_CREATE_BOARD = "create_board";
    public static final String REQ_GET_CURRENT_BOARD_ID = "get_current_board_id";
    public static final String REQ_GET_USERS_FOR_BOARD_ID = "get_users_for_board_id";
    public static final String REQ_JOIN_BOARD_ID = "join_board_id";
    public static final String REQ_LOGOUT = "logout";
    public static final String REQ_GET_USERS_IN_MY_BOARD = "get_users_in_my_board";
    public static final String REQ_LEAVE_BOARD = "leave_board";
    
    
    public static final String RESP_BOARD_IDS = "board_ids";
    public static final String RESP_USERS_FOR_BOARD = "users_for_board_id";
    public static final String RESP_CURRENT_BOARD_ID = "current_board_id";
    public static final String RESP_FAILED = "failed";
    public static final String RESP_DONE = "done";
    public static final String RESP_LOGGED_OUT = "logged_out";
    
    public static void handleMessage(String input, UserThread userThread, LobbyModel lobbyModel){
        String command = input.split(" ")[0];
        
        if(command.equals(MessageHandler.REQ_GET_BOARD_IDS)){
            MessageHandler.handleRequestGetBoardIDs(input, userThread, lobbyModel);
        }else if (command.equals(MessageHandler.REQ_SET_USERNAME)){
            MessageHandler.handleRequestSetUsername(input, userThread, lobbyModel);
        }else if (command.equals(MessageHandler.REQ_CREATE_BOARD)){
            MessageHandler.handleRequestCreateBoard(input, userThread, lobbyModel);
        }else if(command.equals(MessageHandler.REQ_GET_CURRENT_BOARD_ID)){
            MessageHandler.handleRequestGetCurrentBoard(input, userThread, lobbyModel);
        }else if (command.equals(MessageHandler.REQ_GET_USERS_FOR_BOARD_ID)){
            MessageHandler.handleRequestGetUsersForBoardID(input, userThread, lobbyModel);
        }else if (command.equals(MessageHandler.REQ_JOIN_BOARD_ID)){
            MessageHandler.handleRequestJoinBoardID(input, userThread, lobbyModel);
        }else if (command.equals(MessageHandler.REQ_LOGOUT)){
            MessageHandler.handleRequestLogout(input, userThread, lobbyModel);
        }else if (command.equals(MessageHandler.REQ_GET_USERS_IN_MY_BOARD)){
            MessageHandler.handleRequestGetUsersInMyBoard(input, userThread, lobbyModel);
        }else if (command.equals(MessageHandler.REQ_LEAVE_BOARD)){
            MessageHandler.handleRequestLeaveBoard(input, userThread, lobbyModel);
        }
    }

    /**
     * Request: 'get_board_ids'
     * Response: 'board_ids [id1] [id2] [id3]...'
     * @param input 'get_board_ids' 
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestGetBoardIDs(String input, UserThread userThread,LobbyModel lobbyModel) {
        userThread.output(MessageHandler.makeResponseBoardIDs(lobbyModel.getWhiteboardIDs()));
    }
    
    
    /**
     * Request: 'set_username [newName]'
     * Response (to all users in the given board): 'users_for_board [boardID] [userName11] [userName2]...'
     * @param input 'set_username [newName]'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestSetUsername(String input, UserThread userThread, LobbyModel lobbyModel) {
        Set<String> userNames = new HashSet<String>();
        Set<Integer> userIDsOfUsersInSameBoard = new HashSet<Integer>();
        
        String[] splitString = input.split(" ");
        int userID = userThread.getUserID();
        String newName = splitString[1];
        lobbyModel.changeUserName(newName, userID);
        int boardID = lobbyModel.getBoardIDThatUserIDIsIn(userID);
        if(boardID != -1){
            userIDsOfUsersInSameBoard = lobbyModel.getUserIDsOfUsersInSameBoardAsGivenUserID(userID);
            for (int uID : userIDsOfUsersInSameBoard){
                userNames.add(lobbyModel.getUserNameForUserID(uID));
            }
        }
        
        String response = MessageHandler.makeResponseUsersForBoardID(boardID, userNames);
        userThread.broadcast(response, userIDsOfUsersInSameBoard);
        userThread.output(MessageHandler.makeResponseDone());
    }
    
    /**
     * Request: 'create_board [boardName]'
     * Response (to all users): 'board_ids [id1] [id2] [id3]...' 
     * @param input 'create_board [userID] [boardName]'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestCreateBoard(String input,UserThread userThread, LobbyModel lobbyModel) {
        String[] splitString = input.split(" ");
        int userID = userThread.getUserID();
        String boardName = splitString[1];
        int boardID = lobbyModel.addBoard(boardName);
        lobbyModel.userJoinBoard(userID, boardID);
        userThread.broadcast(MessageHandler.makeResponseBoardIDs(lobbyModel.getWhiteboardIDs()));
        userThread.output(MessageHandler.makeResponseDone());
    }
    
    /**
     * Request: 'get_current_board_id'
     * Response: 'current_board_id [boardID]' (boardID = -1 if the user is not in a board)
     * @param input 'get_current_board_id'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestGetCurrentBoard(String input, UserThread userThread, LobbyModel lobbyModel) {
        int boardID = lobbyModel.getBoardIDThatUserIDIsIn(userThread.getUserID());
        userThread.output(MessageHandler.makeResponseCurrentBoardID(boardID));
    }
    
    /**
     * Request: 'get_users_for_board_id [boardID]'
     * Response: 'users_for_board [boardID] [userName1] [userName2]...'
     * @param input 'get_users_for_board_id [boardID]'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestGetUsersForBoardID(String input, UserThread userThread, LobbyModel lobbyModel) {
        int boardID = Integer.parseInt(input.split(" ")[1]);
        Set<String> userNames = lobbyModel.getUserNamesForBoardID(boardID);
        userThread.output(MessageHandler.makeResponseUsersForBoardID(boardID, userNames));
    }
    
    /**
     * Request: 'join_board_id [boardID]'
     * Response: 'users_for_board [boardID] [userName1] [userName2]...'
     * @param input 'join_board_id [boardID]'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestJoinBoardID(String input, UserThread userThread, LobbyModel lobbyModel) {
        int boardID = Integer.parseInt(input.split(" ")[1]);
        try{
            lobbyModel.userJoinBoard(userThread.getUserID(), boardID);
            Set<Integer> userIDsOfUsersInSameBoard = lobbyModel.getUserIDsOfUsersInSameBoardAsGivenUserID(userThread.getUserID());
            Set<String> userNames = lobbyModel.getUserNamesForBoardID(boardID);
            String response = MessageHandler.makeResponseUsersForBoardID(boardID, userNames);
            userThread.broadcast(response, userIDsOfUsersInSameBoard);
            userThread.output(MessageHandler.makeResponseDone());
        }catch(Exception ex){
            userThread.output(MessageHandler.makeResponseFailed());
        }
    }
    
    /**
     * Request: 'logout'
     * Response: 'logged_out'
     * @param input 'logout'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestLogout(String input, UserThread userThread, LobbyModel lobbyModel) {
        int boardID = lobbyModel.getBoardIDThatUserIDIsIn(userThread.getUserID());
        lobbyModel.deleteUser(userThread.getUserID());
        if(boardID != -1){
            Set<Integer> userIDsOfUsersInSameBoard = lobbyModel.getUserIDsForBoardID(boardID);
            Set<String> userNames = lobbyModel.getUserNamesForBoardID(boardID);
            String response = MessageHandler.makeResponseUsersForBoardID(boardID, userNames);
            userThread.broadcast(response, userIDsOfUsersInSameBoard);
        }
        userThread.output(MessageHandler.makeResponseLoggedOut());
        userThread.closeSocket();
    }
    
    /**
     * Request: 'get_users_in_my_board'
     * Response: 'users_for_board [boardID] [userName1] [userName2]...'
     * @param input 'get_users_in_my_board'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestGetUsersInMyBoard(String input, UserThread userThread, LobbyModel lobbyModel) {
        int boardID = lobbyModel.getBoardIDThatUserIDIsIn(userThread.getUserID());
        if(boardID != -1){
            Set<String> userNames = lobbyModel.getUserNamesForBoardID(boardID);
            userThread.output(MessageHandler.makeResponseUsersForBoardID(boardID, userNames));
        }else{
            userThread.output(MessageHandler.makeResponseFailed());
        }
    }
    
    /**
     * Request: 'leave_board'
     * Response: 'users_for_board [boardID] [userName1] [userName2]...' 
     * @param input 'leave_board'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestLeaveBoard(String input, UserThread userThread, LobbyModel lobbyModel) {
        int boardID = lobbyModel.getBoardIDThatUserIDIsIn(userThread.getUserID());
        if(boardID != -1){
            lobbyModel.userLeaveBoard(userThread.getUserID(), boardID);
            Set<Integer> userIDsOfUsersInSameBoard = lobbyModel.getUserIDsForBoardID(boardID);
            Set<String> userNames = lobbyModel.getUserNamesForBoardID(boardID);
            String response = MessageHandler.makeResponseUsersForBoardID(boardID, userNames);
            userThread.broadcast(response, userIDsOfUsersInSameBoard);
        }
        userThread.output(MessageHandler.makeResponseDone());
    }
    
    /*************************************************************/
    
    /**
     * @return 'done'
     */
    private static String makeResponseDone(){
        return MessageHandler.RESP_DONE;
    }
    
    /**
     * @return 'failed'
     */
    private static String makeResponseFailed(){
        return MessageHandler.RESP_FAILED;
    }
    
    /**
     * @return 'logged_out'
     */
    private static String makeResponseLoggedOut(){
        return MessageHandler.RESP_LOGGED_OUT;
    }
    
    /**
     * @param boardID the board id
     * @return 'current_board_id [boardID]'
     */
    private static String makeResponseCurrentBoardID(int boardID){
        return String.format("%s %d", MessageHandler.RESP_CURRENT_BOARD_ID, boardID);
    }
    
    /**
     * @param boardIDs the set of IDs
     * @return 'board_ids [id1] [id2] [id3]...'
     */
    private static String makeResponseBoardIDs(Set<Integer> boardIDs){
        StringBuilder response = new StringBuilder();
        response.append(MessageHandler.RESP_BOARD_IDS);
        for(int id : boardIDs){
            response.append(" " + id);
        }
        return response.toString();
    }
    
    /**
     * @param boardID the id of the board
     * @param userNames the names of the users in the board
     * @return 'users_for_board [boardID] [userName1] [userName2]...'
     */
    private static String makeResponseUsersForBoardID(int boardID, Set<String> userNames){
        StringBuilder response = new StringBuilder();
        response.append(MessageHandler.RESP_USERS_FOR_BOARD);
        response.append(" " + boardID);
        for(String userName : userNames){
            response.append(" " + userName);
        }
        return response.toString();
    }
}
