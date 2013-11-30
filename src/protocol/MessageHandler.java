package protocol;

import java.util.Set;

import adts.LobbyModel;
import server.UserThread;

public class MessageHandler {
    private static final String REQ_GET_BOARD_IDS = "get_board_ids";
    
    private static final String RESP_BOARD_IDS = "board_ids";
    private static final String RESP_FAILED = "failed";
    
    public static void handleMessage(String input, UserThread userThread, LobbyModel lobbyModel){
        String command = input.split(" ")[0];
        
        if(command.equals(MessageHandler.REQ_GET_BOARD_IDS)){
            MessageHandler.handleRequestGetBoardIDs(input, userThread, lobbyModel);
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
}
