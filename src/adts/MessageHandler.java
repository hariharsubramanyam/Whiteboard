package adts;

public class MessageHandler {
    
    public static final String REQ_WHITEBOARDS = "whiteboards";
    public static final String REQ_CREATE = "create";
    public static final String REQ_SET_USER_NAME = "setusername";
    public static final String REQ_USERS_FOR_BOARD = "getusersforboard";
    public static final String REQ_JOIN = "join";
    public static final String REQ_DRAW = "reqdraw";
    public static final String REQ_LEAVE = "leave";
    
    public static final String RESP_ALL_BOARDS = "allboards";
    public static final String RESP_BOARD_DATA = "boarddata";
    public static final String RESP_USERS_FOR_BOARD = "usersforboard";
    public static final String RESP_DRAW = "draw";
    
    public static String handleMessage(String message, LobbyModel model){
        String[] splitMessage = message.split(" ");
        if(splitMessage[0].equals(MessageHandler.REQ_WHITEBOARDS))
            return respondToWhiteboards(message, model);
        else if (splitMessage[0].equals(MessageHandler.REQ_CREATE))
            return respondToCreate(message, model);
        else if (splitMessage[0].equals(MessageHandler.REQ_SET_USER_NAME))
            return respondToSetUserName(message, model);
        else if (splitMessage[0].equals(MessageHandler.REQ_USERS_FOR_BOARD))
            return respondToGetUsersForBoard(message, model);
        else if (splitMessage[0].equals(MessageHandler.REQ_JOIN))
            return respondToJoin(message, model);
        else if (splitMessage[0].equals(MessageHandler.REQ_DRAW))
            return respondToDraw(message, model);
        else if (splitMessage[0].equals(MessageHandler.REQ_LEAVE))
            return respondToLeave(message, model);
        return "";
    }

    private static String respondToLeave(String message, LobbyModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String respondToDraw(String message, LobbyModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String respondToJoin(String message, LobbyModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String respondToGetUsersForBoard(String message,
            LobbyModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String respondToSetUserName(String message, LobbyModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String respondToCreate(String message, LobbyModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String respondToWhiteboards(String message, LobbyModel model) {
        // TODO Auto-generated method stub
        return null;
    }
}
