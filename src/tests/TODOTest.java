package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

import ui.LobbyGUI;
import ui.LobbyGUIBackgroundThread;
import server.WhiteboardServer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import protocol.ClientSideMessageMaker;

/**
 * This is the test suite for TODO . It starts by testing to make sure all the
 * equals(), toString(), and hashCode() methods work correctly as they are the
 * foundation for the entire project. It ends by testing the more specific
 * methods for each class. Must supply valid inputs for the tests as defined by
 * the preconditions in each class to pass these tests.
 */

public class TODOTest {
	
	/*
	 * Testing strategy
	 * 
	 * Goal: make sure every TODO method works correctly
	 * 
	 * Strategy: Run a WhiteboardServer, create three instances 
	 *           of LobbyGUI and test various requests/responses.
	 *           
	 */

	/*
	 * Test TODO objects for the equals methods by TODO
	 */
	int port = 4444;
	String testHost = "127.0.0.1";   //localhost, can be replaced to test remote servers
	WhiteboardServer server;
	SimpleClient client1;
	SimpleClient client2;
	SimpleClient client3;
	
	
	public void initializeServerAndClients() throws IOException {
		// Initialize a WhiteboardServer locally on port (4444 by default)
		server = new WhiteboardServer(port);
		server.serve();
		
		// Initialize clients, connecting them to server
		//client1 = new SimpleClient(testHost);
		//client2 = new SimpleClient(testHost);
		//client3 = new SimpleClient(testHost);
		//terminateServerAndClients();
		
	}
	
	public void terminateServerAndClients() {
		server.killWhiteboardServer();
	}


	@Test
	public void test_get_board_ids() throws IOException {
		this.initializeServerAndClients();
		// Have client1 create a board named "Board1"
		// String createBoardReq = ClientSideMessageMaker.makeRequestStringCreateBoard("Board1");
		//client1.makeRequest(createBoardReq);
		// client1.checkResponse("welcome 0");  // The first connector gets a userID of 0.
		
		//this.terminateServerAndClients();
	}
	
}
class SimpleClient {
	String host;
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	String serverResponse;
	
	public SimpleClient(String host) {
		try{
		    this.host = host;
		    this.socket = new Socket(host, 4444);
		    this.out = new PrintWriter(socket.getOutputStream(), true);
		    this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(Exception ex){}

	}
	
	public void makeRequest(String req){
	    out.println(req);
	    System.out.println("REQ: " + req);
	}
	
	/**
	 * 
	 * @return The earliest response received that was not 
	 * 		   already returned from an earlier getResponse(). 
	 * 		   If no response was received, returns the String "No response yet."
	 */
	public String getResponse() {
		try {
            if ((serverResponse = in.readLine()) != null) {
                return serverResponse;
            	}
            else {return "No response yet.";}
        } 
		catch (IOException e) {
			return "Error reading in.readLine of SimpleClient."; 
		}
	}

	public void checkResponse(String expected) {
		assertEquals(expected, this.getResponse());
	}
	
}




