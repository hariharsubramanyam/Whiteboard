package protocol;

import javax.swing.JFrame;

import adts.LobbyModel;
import protocol.MessageHandler;
import server.UserThread;
import ui.LobbyGUI;

/**
 * Is used by LobbyGUI to process responses from the server and update the GUI accordingly.
 */
public class ClientSideResponseHandler {
	
	 public static final String RESP_BOARD_IDS = "board_ids";
	    public static final String RESP_USERS_FOR_BOARD = "users_for_board_id";
	    public static final String RESP_CURRENT_BOARD_ID = "current_board_id";
	    public static final String RESP_FAILED = "failed";
	    public static final String RESP_DONE = "done";
	    public static final String RESP_LOGGED_OUT = "logged_out";
	    public static final String RESP_DRAW = "draw";
	    public static final String RESP_BOARD_LINES = "board_lines";

	public static void handleResponse(String input, LobbyGUI userGUI) {
        String responseType = input.split(" ")[0];

        if (responseType.equals(MessageHandler.RESP_BOARD_IDS)) {
            // Do stuff
        } else if (responseType.equals(MessageHandler.RESP_USERS_FOR_BOARD)) {
            // Do stuff
        } else if (responseType.equals(MessageHandler.RESP_CURRENT_BOARD_ID)) {
            // Do stuff
        } else if (responseType.equals(MessageHandler.RESP_FAILED)) {
            // Do stuff
        } else if (responseType.equals(MessageHandler.RESP_DONE)) {
            // Do stuff
        } else if (responseType.equals(MessageHandler.RESP_LOGGED_OUT)) {
            // Do stuff
        } else if (responseType.equals(MessageHandler.RESP_DRAW)) {
            // Do stuff
        } else if (responseType.equals(MessageHandler.RESP_BOARD_LINES)) {
            // Do stuff
        }
	}
}

