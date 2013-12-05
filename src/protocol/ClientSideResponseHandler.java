package protocol;

import java.util.ArrayList;
import java.util.List;

import ui.LobbyGUI;

/**
 * Is used by LobbyGUI to process responses from the server and update the GUI
 * accordingly.
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
		String[] data = input.replace(responseType, "").split(" ");
		System.out.println(input);

		if (responseType.equals(MessageHandler.RESP_BOARD_IDS)) {
			for (String boardID : data) {
				userGUI.clearAllRows(0);
				userGUI.addRowToCurrentBoardsModel(0, new String[] { boardID });
			}

		} else if (responseType.equals(MessageHandler.RESP_USERS_FOR_BOARD)) {
			for (String user : data) {
				userGUI.clearAllRows(1);
				userGUI.addRowToCurrentBoardsModel(1, new String[] { user });
			}
		} else if (responseType.equals(MessageHandler.RESP_CURRENT_BOARD_ID)) {
			// Do stuff
		} else if (responseType.equals(MessageHandler.RESP_FAILED)) {
			// Do stuff
		} else if (responseType.equals(MessageHandler.RESP_DONE)) {
			// Do stuff
		} else if (responseType.equals(MessageHandler.RESP_LOGGED_OUT)) {
			// Do stuff
		} else if (responseType.equals(MessageHandler.RESP_DRAW)) {
			ArrayList<Integer> integerData = new ArrayList<Integer>();
			for (String i : data){
				integerData.add(Integer.valueOf(i));
			}
			userGUI.sendPacketToCanvas(integerData);
		} else if (responseType.equals(MessageHandler.RESP_BOARD_LINES)) {
			// Do stuff
		}
	}
}
