package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.*;

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
	 * the WhiteboardClient table, and more which are given in detail below.
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

	@Test(timeout = 1000)	// time out in 1 second, in case test does not complete
	/*
	 * Strategy:
	 * Have client1 create a board and check the response received at client1 and client2.
	 */
	public void create_board_test() throws IOException {
		this.initialize();
		System.out.println("Started create_board_test");
		client1.makeRequest(createBoardReq);
		client1.compareRespWith("users_for_board_id -1 User3 User0 User2 User1");
		client2.compareRespWith("board_ids -1 2 3 4"); // the IDs of the users in lobby
		client1.compareRespWith("s");
		System.out.println("Passed assertions");
		client1.makeRequest(ClientSideMessageMaker.makeRequestStringJoinBoardID(0));
		
		
		client2.makeRequest(createBoardReq);
		
	}

	@Test (timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void get_current_board_id_test() throws IOException {
		// this.initialize();

	}

	@Test(timeout = 1000)
	// time out in 1 second, in case test does not complete
	public void get_users_for_board_id_test() throws IOException {
		// this.initialize();
		
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

	/**
	 * Randomly finds an open port and returns it if it is available.
	 */
	private int getAvailablePort() throws IOException {
		int port = 0;
		Random RANDOM = new Random();
		do {
			port = RANDOM.nextInt(20000) + 1000;
		} while (!isPortAvailable(port));

		return port;
	}

	/**
	 * Given a port number, it checks to see if it is in use. It returns true if
	 * it is not in use.
	 * 
	 * @param port
	 *            integer port number to check
	 */
	private boolean isPortAvailable(final int port) throws IOException {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			return true;
		} catch (final IOException e) {
		} finally {
			if (ss != null) {
				ss.close();
			}
		}

		return false;
	}

	/**
	 * Helper function to start up a server on a given port, and four clients,
	 * all on localhost. Uses the checkResponse() helper function to assert the
	 * clients connected to the server.
	 * 
	 * Thread-safety:
	 * 
	 * Since we are first checking for an open port and then creating it based
	 * off of that integer value, there is a possibility of this socket getting
	 * used before actualling connecting to it. However, this goes beyond the
	 * scope of this project and if in the future it is a nessesary feature,
	 * simply return an available socket and connect to it directly.
	 * 
	 * @param port
	 *            given socket number on which to open the server/connect the
	 *            clinets to.
	 * @throws IOException
	 *             if there is a connection timeout, an IOException is thrown.
	 */
	public void initialize() throws IOException {
		port = getAvailablePort();

		this.server = new WhiteboardServer(port);
		this.server.serve();
		
		this.client1 = new SimpleClient(testHost, port);
		this.client2 = new SimpleClient(testHost, port);
		this.client3 = new SimpleClient(testHost, port);
		this.client4 = new SimpleClient(testHost, port);
		// need to clear their incoming buffers; they currently have "welcome" as the latest response
		this.client1.in.readLine();
		this.client2.in.readLine();
		this.client3.in.readLine();
		this.client4.in.readLine();
		System.out.println("Done initializing");
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
		} catch (Exception ex) {}
	}

	public void disconnect() throws IOException {
		this.out.close();
		this.in.close();
		this.socket.close();
	}

	public void makeRequest(String req) {	
		System.out.println("Making request: " + req);
		out.println(req);

		try {
			Thread.sleep(1); // Wait for 1 ms to give server time to respond.
		} catch (InterruptedException e) {
			System.out.println("Error waiting in makeRequest.");
		}

	}
	
	/**
	 * Simply calls this client's in.readLine()
	 * @return a response from the server
	 */
	public String read() {
		try {
			return in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error in in.readLine()";
		}
	}
	
	/**
	 * Checks that the response and expected are the same strings, up to ordering of words.
	 * @param expected
	 */
	public void compareRespWith(String expected) {
		String resp= this.read();
		resp.replace("\n","");
		System.out.println("Expected: " + expected + "; got " + resp);
		String[] respArray = resp.split(" ");
		String[] expArray  = expected.split(" ");
		//System.out.println("resp");
		//for (String s: respArray) {System.out.println(s);}
		//System.out.println("exp");
		//for (String s: expArray) {System.out.println(s);}
		Arrays.sort(respArray);
		Arrays.sort(expArray);
		assertArrayEquals(respArray, expArray);
		
	}
	
}
