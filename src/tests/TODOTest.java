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
	 * Goal: make sure every TODO method works correctly
	 * 
	 * Strategy: Run a WhiteboardServer, create three instances TODO
	 *           of LobbyGUI and test various requests/responses.
	 *           
	 */

	/*
	 * Test TODO objects for the equals methods by TODO
	 */
	int port = 4444;
	String testHost = "127.0.0.1";   //localhost, can be replaced to test remote servers
	
	@Test(timeout=1000) // time out in 1 second, in case test does not complete
	public void multiple_clients_create_and_get_board_ids_test() throws IOException {
	
		String createBoardReq = ClientSideMessageMaker.makeRequestStringCreateBoard("newBoard");
		String getBoardIDsReq = ClientSideMessageMaker.makeRequestStringGetBoardIDs();
		
		// Initialize server and client1, client2.
		WhiteboardServer server = new WhiteboardServer(port);
		server.serve();
		SimpleClient client1 = new SimpleClient(testHost);
		SimpleClient client2 = new SimpleClient(testHost);
				
		client1.checkResponse("welcome 0");   // userID starts at 0
		client2.checkResponse("welcome 1");
		
		client1.sendReqAndCheckResponse(getBoardIDsReq, "board_ids"); // no boards yet.
		
		// Have client1 create a board named "newBoard".
		// client1.sendReqAndCheckResponse(createBoardReq,"done"); // The first board is board 0"
		
		// client2 should have been notified that client1 made a board
		// client2.checkResponse("board_ids 0");
		
		// client 3 connects and makes a board.
		SimpleClient client3 = new SimpleClient(testHost);
		client3.checkResponse("welcome 2"); // userID is 2.
		// client3.sendReqAndCheckResponse(createBoardReq,"board_ids 0 1");
		// client1.checkResponse("board_ids 0 1");
		// client2.checkResponse("board_ids 0 1");
		
	}	
	
	@Test(timeout=1000) // time out in 1 second, in case test does not complete
	public void multiple_clients_test1() throws IOException {
		
	}
	
	@Test(timeout=1000) // time out in 1 second, in case test does not complete
	public void multiple_clients_test2() throws IOException {
		
	}
	
	@Test(timeout=1000) // time out in 1 second, in case test does not complete
	public void multiple_clients_test3() throws IOException {
		
	}
	
	@Test(timeout=1000) // time out in 1 second, in case test does not complete
	public void multiple_clients_test4() throws IOException {
		
	}
	
	@Test(timeout=1000) // time out in 1 second, in case test does not complete
	public void multiple_clients_test5() throws IOException {
		
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
	
	public void disconnect() throws IOException {
		this.out.close();
		this.in.close();
		this.socket.close();	
	}
	
	public void makeRequest(String req){
	    out.println(req);
	    System.out.println("REQ: " + req);
	}
	
	/**
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
			System.out.println("Error waiting in sendReqAndCheckResponse.");}  
		
		this.checkResponse(expectedResponse); 
	}
	
}




