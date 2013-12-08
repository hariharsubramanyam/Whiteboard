package protocol;

import java.util.ArrayList;
import java.util.List;

import adts.Line;
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
	    } else if (command.equals(MessageHandler.RESP_DRAW)){
	        handleDraw(tokens,userGUI);
	    } else if (command.equals(MessageHandler.RESP_BOARD_LINES)){
	        handleBoardLines(tokens, userGUI);
	    } else if (command.equals(MessageHandler.RESP_CLEAR)){
	        handleClear(tokens, userGUI);
	    }
	}
	private static void handleClear(String[] tokens, LobbyGUI userGUI){
	    userGUI.onReceiveClear();
	}
	private static void handleBoardIDs(String[] tokens, LobbyGUI userGUI){
	    List<Integer> boardIDs = new ArrayList<Integer>();
	    for (String token : tokens){
	        if(token.equals(""))
	            continue;
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
	
	private static void handleDraw(String[] tokens, LobbyGUI userGUI){
	    int x1 = Integer.parseInt(tokens[0]);
	    int y1 = Integer.parseInt(tokens[1]);
	    int x2 = Integer.parseInt(tokens[2]);
	    int y2 = Integer.parseInt(tokens[3]);
	    float strokeThickness = Float.parseFloat(tokens[4]);
	    int r = Integer.parseInt(tokens[5]);
	    int g = Integer.parseInt(tokens[6]);
	    int b = Integer.parseInt(tokens[7]);
	    int a = Integer.parseInt(tokens[8]);
	    Line l = new Line(x1, y1, x2, y2, strokeThickness, r, g, b, a);
        userGUI.onReceiveDraw(l);
    }
	
	public static void handleBoardLines(String[] tokens, LobbyGUI userGUI){
	    List<Line> lines = new ArrayList<Line>();
	    int i = 0;
	    int x1, y1, x2, y2, r, g, b, a;
	    float strokeThickness;
	    while(i < tokens.length){
	        if(tokens[0].equals(""))
	            continue;
	        x1 = Integer.parseInt(tokens[i]);
	        y1 = Integer.parseInt(tokens[i+1]);
	        x2 = Integer.parseInt(tokens[i+2]);
	        y2 = Integer.parseInt(tokens[i+3]);
	        strokeThickness = Float.parseFloat(tokens[i+4]);
	        r = Integer.parseInt(tokens[i+5]);
	        g = Integer.parseInt(tokens[i+6]);
	        b = Integer.parseInt(tokens[i+7]);
	        a = Integer.parseInt(tokens[i+8]);
	        i = i + 9;
	        lines.add(new Line(x1, y1, x2, y2, strokeThickness, r, g, b, a));
	    }
	    userGUI.onReceiveBoardLines(lines);
	}
}
