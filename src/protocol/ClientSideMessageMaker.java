package protocol;

import adts.Line;

public class ClientSideMessageMaker {
    public static final String REQ_GET_BOARD_IDS = "get_board_ids";
    public static final String REQ_SET_USERNAME = "set_username";
    public static final String REQ_CREATE_BOARD = "create_board";
    public static final String REQ_GET_CURRENT_BOARD_ID = "get_current_board_id";
    public static final String REQ_GET_USERS_FOR_BOARD_ID = "get_users_for_board_id";
    public static final String REQ_JOIN_BOARD_ID = "join_board_id";
    public static final String REQ_LOGOUT = "logout";
    public static final String REQ_GET_USERS_IN_MY_BOARD = "get_users_in_my_board";
    public static final String REQ_LEAVE_BOARD = "leave_board";
    public static final String REQ_DRAW = "req_draw";
    public static final String REQ_CLEAR = "req_clear";

    public static final String RESP_BOARD_IDS = "board_ids";
    public static final String RESP_USERS_FOR_BOARD = "users_for_board_id";
    public static final String RESP_CURRENT_BOARD_ID = "current_board_id";
    public static final String RESP_FAILED = "failed";
    public static final String RESP_DONE = "done";
    public static final String RESP_LOGGED_OUT = "logged_out";
    public static final String RESP_DRAW = "draw";
    public static final String RESP_BOARD_LINES = "board_lines";

    public static String makeRequestStringGetBoardIDs() {
    	return ClientSideMessageMaker.REQ_GET_BOARD_IDS;
    }

    public static String makeRequestStringSetUsername(String newName) {
        return String.format("%s %s", ClientSideMessageMaker.REQ_SET_USERNAME,
                newName.replace(" ", "_"));
    }

    public static String makeRequestStringCreateBoard(String boardName) {
        return String.format("%s %s", ClientSideMessageMaker.REQ_CREATE_BOARD,
                boardName.replace(" ", "_"));
    }

    public static String makeRequestStringGetCurrentBoardID() {
        return ClientSideMessageMaker.REQ_GET_CURRENT_BOARD_ID;
    }

    public static String makeRequestStringGetUsersForBoardID(int boardID) {
        return String.format("%s %d",
                ClientSideMessageMaker.REQ_GET_USERS_FOR_BOARD_ID, boardID);
    }

    public static String makeRequestStringJoinBoardID(int boardID) {
        return String.format("%s %d", ClientSideMessageMaker.REQ_JOIN_BOARD_ID,
                boardID);
    }

    public static String makeRequestStringLogout() {
        return ClientSideMessageMaker.REQ_LOGOUT;
    }

    public static String makeRequestStringGetUsersInMyBoard() {
        return ClientSideMessageMaker.REQ_GET_USERS_IN_MY_BOARD;
    }

    public static String makeRequestStringLeaveBoard() {
        return ClientSideMessageMaker.REQ_LEAVE_BOARD;
    }

    public static String makeRequestStringDraw(Line line) {
        return String.format("%s %s", ClientSideMessageMaker.REQ_DRAW,
                line.toString());
    }
    
    public static String makeRequestStringClear() {
        return String.format("%s", ClientSideMessageMaker.REQ_CLEAR);
    }

}
