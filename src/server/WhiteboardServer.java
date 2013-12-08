package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import adts.LobbyModel;

public class WhiteboardServer {
	private final ServerSocket serverSocket;
	private final LobbyModel lobbyModel;
	private final List<UserThread> userThreads;


	public WhiteboardServer(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
		this.lobbyModel = new LobbyModel();
		this.userThreads = new ArrayList<UserThread>();

	}

	public  void serve() throws IOException {
		while (true) {
			final Socket socket = serverSocket.accept();
			int userID = this.lobbyModel.addUser();
			UserThread thread = new UserThread(socket, userID,
					this.userThreads, this.lobbyModel);
			this.userThreads.add(thread);
			thread.start();
		}

	}

	public static void main(String[] args) {
		int port = 4444;
		WhiteboardServer server;
		Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
		while (!arguments.isEmpty()) {
			return;
		}

		try {
		    server = new WhiteboardServer(port);
            server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void runWhiteboardServer(int port) throws IOException {
		WhiteboardServer server;
		try {
			server = new WhiteboardServer(port);
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void killWhiteboardServer() {
		// Kill the userThreads
		for (UserThread thread: this.userThreads){
			thread.cancel();
		}
		// Close the serverSocket
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}
