package protocol;

import java.util.ArrayList;
import java.util.List;

import ui.LobbyGUI;

/**
 * Is used by LobbyGUI to process responses from the server and update the GUI
 * accordingly.
 */
public class ClientSideResponseHandler {

	public static void handleResponse(String input, LobbyGUI userGUI) {
	    System.out.println("RESP: " + input);
	   
	    String command = input.split(" ")[0];
	    String[] tokens = input.replace(command, "").trim().split(" ");
	    
	    if(command.equals(MessageHandler.RESP_BOARD_IDS)){
	        handleBoardIDs(tokens, userGUI);
	    } else if (command.equals(MessageHandler.RESP_USERNAME_CHANGED)){
	        handleUsernameChanged(tokens, userGUI);
	    } else if (command.equals(MessageHandler.RESP_WELCOME)){
	        handleWelcome(tokens, userGUI);
	    }
	}
	
	private static void handleBoardIDs(String[] tokens, LobbyGUI userGUI){
	    List<Integer> boardIDs = new ArrayList<Integer>();
	    for (String token : tokens){
	        boardIDs.add(Integer.parseInt(token));
	    }
	    userGUI.onReceiveBoardIDs(boardIDs);
	}
	
	private static void handleUsernameChanged(String[] tokens, LobbyGUI userGUI){
	    userGUI.onReceiveUsernameChanged(tokens[0]);
    }
	
	private static void handleWelcome(String[] tokens, LobbyGUI userGUI){
	    userGUI.onReceiveWelcome(Integer.parseInt(tokens[0]));
	}
}
