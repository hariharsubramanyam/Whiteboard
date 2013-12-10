package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import adts.LobbyModel;

public class WhiteboardServer {
	private Socket socket;
	private final ServerSocket serverSocket;
	private final LobbyModel lobbyModel;
	private final List<UserThread> userThreads;
	private final Thread serverThread;
	private final WhiteboardServer thisServer;

	public WhiteboardServer(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);

		this.lobbyModel = new LobbyModel();
		this.userThreads = new ArrayList<UserThread>();
		this.thisServer = this;
		this.serverThread = new Thread(new Runnable() {
			public void run() {
				try {
					thisServer.singleThreadedServe();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	Exception e = new Exception();
	public void serve() throws IOException {
		this.serverThread.start();
	}
	
	public void singleThreadedServe() throws IOException {
		while (true) {
			socket = serverSocket.accept();
			int userID = this.lobbyModel.addUser();
			UserThread thread = new UserThread(socket, userID,
					this.userThreads, this.lobbyModel);
			this.userThreads.add(thread);
			thread.start();
		}

	}

	public static void main(String[] args) {
		int port = 4444;
		Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
		while (!arguments.isEmpty()) {
			return;
		}

		try {
		    runWhiteboardServer(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void runWhiteboardServer(int port) throws IOException {
		WhiteboardServer server;
		try {
			server = new WhiteboardServer(port);
			server.serve();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
