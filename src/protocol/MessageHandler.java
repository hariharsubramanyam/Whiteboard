package protocol;

import java.util.HashSet;
import java.util.Set;

import adts.LobbyModel;
import server.UserThread;

public class MessageHandler {
    private static final String REQ_GET_BOARD_IDS = "get_board_ids";
    private static final String REQ_SET_USERNAME = "set_username";
    
    private static final String RESP_BOARD_IDS = "board_ids";
    private static final String RESP_USERS_FOR_BOARD = "users_for_board";
    private static final String RESP_FAILED = "failed";
    
    //SET_USERNAME: set_username [userID] [newUserName]
    
    public static void handleMessage(String input, UserThread userThread, LobbyModel lobbyModel){
        String command = input.split(" ")[0];
        
        if(command.equals(MessageHandler.REQ_GET_BOARD_IDS)){
            MessageHandler.handleRequestGetBoardIDs(input, userThread, lobbyModel);
        }
        else if (command.equals(MessageHandler.REQ_SET_USERNAME)){
            MessageHandler.handleRequestSetUsername(input, userThread, lobbyModel);
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
     * Request: 'set_username [userID] [newName]'
     * Response (to all users in the given board): 'users_for_board [boardID] [user1] [user2]...'
     * @param input 'set_username [userID] [newName]'
     * @param userThread the user's thread
     * @param lobbyModel the lobby model
     */
    private static void handleRequestSetUsername(String input, UserThread userThread, LobbyModel lobbyModel) {
        Set<String> userNames = new HashSet<String>();
        Set<Integer> userIDsOfUsersInSameBoard = new HashSet<Integer>();
        String[] splitString = input.split(" ");
        int userID = Integer.parseInt(splitString[1]);
        String newName = splitString[2];
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
        response.append(MessageHandler.REQ_SET_USERNAME);
        response.append(" " + boardID);
        for(String userName : userNames){
            response.append(" " + userName);
        }
        return response.toString();
    }
}
