package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import protocol.ClientSideMessageMaker;
import server.WhiteboardServer;

/**
 * Description of test suite (what it tests, etc.) TODO
 */

public class TODOTest {

	/*
	 * Testing strategy
	 * 
	 * Goal: Make sure the server responds correctly to various requests
	 * 
	 * Strategy: For most of the request/response types, we will create four
	 * clients. Two of them will join a board, one of them will join another
	 * board, and one of them will not be in any boards. This way, when we do an
	 * action, we can see it from all relevant perspectives: -Self -Other users
	 * in my board -Other users in other boards -Other users not in any boards
	 */

	/*
	 * Test TODO objects for the equals methods by TODO
	 */
	int port;
	String testHost = "127.0.0.1"; // localhost, can be replaced to test remote
									// servers
	WhiteboardServer server;
	SimpleClient client1;
	SimpleClient client2;
	SimpleClient client3;
	SimpleClient client4;

	String createBoardReq = ClientSideMessageMaker
			.makeRequestStringCreateBoard("BoardName");
	String getCurrentBoardReq = ClientSideMessageMaker
			.makeRequestStringGetCurrentBoardID();
	String getBoardIDSReq = ClientSideMessageMaker
			.makeRequestStringGetBoardIDs();
	String logoutReq = ClientSideMessageMaker.makeRequestStringLogout();
	String leaveBoardReq = ClientSideMessageMaker.makeRequestStringLeaveBoard();
	String getUsersInyMyBoardReq = ClientSideMessageMaker
			.makeRequestStringGetUsersInMyBoard();

	public void initialize(int port) throws IOException {
		server = new WhiteboardServer(port);
		server.serve();

		client1 = new SimpleClient(testHost, port);
		client2 = new SimpleClient(testHost, port);
		client3 = new SimpleClient(testHost, port);
		client4 = new SimpleClient(testHost, port);

		client1.checkResponse("welcome 0"); // userID starts at 0
		client2.checkResponse("welcome 1");
		client3.checkResponse("welcome 2");
		client4.checkResponse("welcome 3");
	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void create_board_test() throws IOException {
		this.initialize(port = 4444);

		// make the first board
		// TODO client1.sendReqAndCheckResponse(createBoardReq, "board_ids 0");
		// TODO client2.checkResponse("board_ids 0");

		// make the second board
		// TODO client2.sendReqAndCheckResponse(createBoardReq,
		// "board_ids 0 1");
		// TODO client1.checkResponse("board_ids 0 1");
	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void get_current_board_id_test() throws IOException {
		this.initialize(port = 4445);

		// Make the first board
		client1.sendReqAndCheckResponse(createBoardReq, "board_ids 0");
		client2.checkResponse("board_ids 0");

		// client1 joins the board
		client1.makeRequest(ClientSideMessageMaker
				.makeRequestStringJoinBoardID(0));
		client1.getResponse();
		
		// Check that client1 is in board 0, client2 is not in a board.
		// client1.sendReqAndCheckResponse(getCurrentBoardReq,
		//		"current_board_id 0");
		// TODO client2.sendReqAndCheckResponse(getCurrentBoardReq,
		// "current_board_id -1");

		// Make the second board
		//client2.makeRequest(createBoardReq);
		//client2.getResponse();
		// client2 joins the new board
		//client2.makeRequest(ClientSideMessageMaker
		//		.makeRequestStringJoinBoardID(1));
		//client2.getResponse();
		// Check that client1 is in board 0, client2 is in board 1
		//client1.sendReqAndCheckResponse(getCurrentBoardReq,
		//								  "current_board_id 0");
		//client2.sendReqAndCheckResponse(getCurrentBoardReq,
		//		"current_board_id 1");

		// Move client1 to a new board
		// client1.makeRequest(createBoardReq);
		// client1.makeRequest(ClientSideMessageMaker
		//		.makeRequestStringJoinBoardID(2));

		// Check that client1 is in board 0, client2 is in board 1
		//client1.sendReqAndCheckResponse(getCurrentBoardReq,
		//		"current_board_id 2");
		//client2.sendReqAndCheckResponse(getCurrentBoardReq,
		//		"current_board_id 1");

	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void get_users_for_board_id_test() throws IOException {
		this.initialize(port = 4446);

		// Make boards
	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void multiple_clients_test3() throws IOException {

	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void multiple_clients_test4() throws IOException {

	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void multiple_clients_test5() throws IOException {

	}

}

class SimpleClient {
	String host;
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	String serverResponse;

	public SimpleClient(String host, int port) {
		try {
			this.host = host;
			this.socket = new Socket(host, port);
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (Exception ex) {
		}
	}

	public void disconnect() throws IOException {
		this.out.close();
		this.in.close();
		this.socket.close();
	}

	public void makeRequest(String req) {
		out.println(req);
	}

	/**
	 * @return The earliest response received that was not already returned from
	 *         an earlier getResponse(). If no response was received, returns
	 *         the String "No response yet."
	 */
	public String getResponse() {
		try {
			if ((serverResponse = in.readLine()) != null) {
				return serverResponse;
			} else {
				return "No response yet.";
			}
		} catch (IOException e) {
			return "Error reading in.readLine() of SimpleClient.";
		}
	}

	public void checkResponse(String expected) {
		assertEquals(expected, this.getResponse());
	}

	public void sendReqAndCheckResponse(String req, String expectedResponse) {
		this.makeRequest(req);

		try {
			Thread.sleep(1); // Sleep for 1 ms to give server time to respond.
		} catch (InterruptedException e) {
			System.out.println("Error waiting in sendReqAndCheckResponse.");
		}

		this.checkResponse(expectedResponse);
	}

}
