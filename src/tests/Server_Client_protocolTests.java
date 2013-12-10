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
 * Testing suite used to ensure server/client side messaging works correctly.
 * The full protocol messaging supported can be found in the Requests PDF.
 * Server/client interactions are the heart of how this collaborative whiteboard
 * system works so it is key that it is thoroughly tested.
 */
public class Server_Client_protocolTests {

	/*
	 * Testing strategy
	 * 
	 * Goal: Make sure the server responds correctly to various requests given
	 * by the clients. Must test every message supported in the protocol. Of
	 * utmost priority is to check the different behaviors of clients in the
	 * lobby, in a canvas, or in different canvases. Refer to the Requests PDF
	 * for more info.
	 * 
	 * Strategy: because only one server can be occupying a socket at any given
	 * time, this test suite exploits the many other open sockets which are open
	 * for use. This way, every test can initialize an instance of a server and
	 * do work on a fresh copy. Note how each test will still connect to
	 * "localhost" but the port is always different.
	 * 
	 * IMPORTANT: in the offchance that a server is initialized on a socket
	 * which is occupied, the test will catch this exception, report to the
	 * console, and continue looking for other ports.
	 * 
	 * For most of the request/response types, we will create four clients. Two
	 * of them will join a board, one of them will join another board, and one
	 * of them will not be in any boards. This way, when we do an action, we can
	 * see it from all relevant perspectives: -Self -Other users in my board
	 * -Other users in other boards -Other users not in any boards.
	 * 
	 * Start by testing that an instance of the server compiles (test the
	 * constructur as well). Continue on to test client connections and initial
	 * welcoming messages. Advance into the protocol messages and test every
	 * possible combination. This include things like joining a board that
	 * doesn't exist, joining one where users exist and have lines drawn/no
	 * lines drawn, changing of usernames, creating boards and seeing them on
	 * the lobbyGUI table, and more which are given in detail below.
	 */

	/*
	 * Test TODO objects for the equals methods by TODO
	 */

	/*
	 * For the purpose of testing, one server and four clinets are used per
	 * test. Each request string is evaluated beforehand.
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

	/**
	 * Helper function to start up a server on a given port, and four clients,
	 * all on localhost. Uses the checkResponse() helper function to assert the
	 * clients connected to the server.
	 * 
	 * @param port
	 *            given socket number on which to open the server/connect the
	 *            clinets to.
	 * @throws IOException
	 *             if there is a connection timeout, an IOException is thrown.
	 */
	public void initialize(int port) throws IOException {

		this.server = new WhiteboardServer(port);
		this.server.serve();

		this.client1 = new SimpleClient(testHost, port);
		this.client2 = new SimpleClient(testHost, port);
		this.client3 = new SimpleClient(testHost, port);
		this.client4 = new SimpleClient(testHost, port);

		this.client1.checkResponse("welcome 0"); // userID starts at 0
		this.client2.checkResponse("welcome 1");
		this.client3.checkResponse("welcome 2");
		this.client4.checkResponse("welcome 3");
	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void create_board_test() throws IOException {
		this.initialize(port = 4444);

		// make the first board
		// TODO this.client1.sendReqAndCheckResponse(createBoardReq,
		// "board_ids 0");
		// TODO this.client2.checkResponse("board_ids 0");

		// make the second board
		// TODO this.client2.sendReqAndCheckResponse(createBoardReq,
		// "board_ids 0 1");
		// TODO this.client1.checkResponse("board_ids 0 1");
	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void get_current_board_id_test() throws IOException {
		this.initialize(port = 4445);

		// Make the first board
		this.client1.sendReqAndCheckResponse(createBoardReq, "board_ids 0");
		this.client2.checkResponse("board_ids 0");

		// this.client1 joins the board
		this.client1.makeRequest(ClientSideMessageMaker
				.makeRequestStringJoinBoardID(0));
		this.client1.getResponse();

		// Check that this.client1 is in board 0, this.client2 is not in a
		// board.
		// this.client1.sendReqAndCheckResponse(getCurrentBoardReq,
		// "current_board_id 0");
		// TODO this.client2.sendReqAndCheckResponse(getCurrentBoardReq,
		// "current_board_id -1");

		// Make the second board
		// this.client2.makeRequest(createBoardReq);
		// this.client2.getResponse();
		// this.client2 joins the new board
		// this.client2.makeRequest(ClientSideMessageMaker
		// .makeRequestStringJoinBoardID(1));
		// this.client2.getResponse();
		// Check that this.client1 is in board 0, this.client2 is in board 1
		// this.client1.sendReqAndCheckResponse(getCurrentBoardReq,
		// "current_board_id 0");
		// this.client2.sendReqAndCheckResponse(getCurrentBoardReq,
		// "current_board_id 1");

		// Move this.client1 to a new board
		// this.client1.makeRequest(createBoardReq);
		// this.client1.makeRequest(ClientSideMessageMaker
		// .makeRequestStringJoinBoardID(2));

		// Check that this.client1 is in board 0, this.client2 is in board 1
		// this.client1.sendReqAndCheckResponse(getCurrentBoardReq,
		// "current_board_id 2");
		// this.client2.sendReqAndCheckResponse(getCurrentBoardReq,
		// "current_board_id 1");

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
			Thread.sleep(10); // Sleep for 10 ms to give server time to respond.
		} catch (InterruptedException e) {
			System.out.println("Error waiting in sendReqAndCheckResponse.");
		}

		this.checkResponse(expectedResponse);
	}

}