package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Set;

import adts.LobbyModel;
import protocol.MessageHandler;

public class UserThread extends Thread {

	/**
	 * The socket associated with this thread
	 */
	private final Socket socket;

	/**
	 * The input stream which this thread reads from
	 */
	private final BufferedReader in;

	/**
	 * The output stream which this thread writes to
	 */
	private final PrintWriter out;

	/**
	 * The ID of the user
	 */
	private final int userID;

	/**
	 * The list of other user thread
	 */
	private final List<UserThread> otherThreads;

	/**
	 * The lobby model
	 */
	private final LobbyModel lobbyModel;

	/**
	 * Create the user thread
	 * 
	 * @param socket
	 *            the socket associated with this thread
	 * @param userID
	 *            the id of the user
	 * @param otherThreads
	 *            the list of other user threads
	 * @throws IOException
	 */
	public UserThread(Socket socket, int userID, List<UserThread> otherThreads,
			LobbyModel lobbyModel) throws IOException {
		this.socket = socket;
		this.userID = userID;
		this.otherThreads = otherThreads;
		this.lobbyModel = lobbyModel;
		this.in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream(), true);
	}

	/**
	 * Write a message to the output stream
	 * 
	 * @param message
	 *            the message to write
	 */
	public void output(String message) {
		out.println(message);
	}

	/**
	 * @return the id of this user
	 */
	public int getUserID() {
		return this.userID;
	}

	/**
	 * Output a message to all users except this one
	 * 
	 * @param message
	 *            the message to output
	 */
	public void broadcast(String message) {
		for (UserThread thread : this.otherThreads) {
			if (thread.getUserID() == this.userID)
				continue;
			thread.output(message);
		}
	}

	/**
	 * Output a message to selected set of users (except this one)
	 * 
	 * @param message
	 *            the message to outpu
	 * @param userIDs
	 *            the list of userIDs to output to
	 */
	public void broadcast(String message, Set<Integer> userIDs) {
		for (UserThread thread : this.otherThreads) {
			if (thread.getUserID() == this.userID)
				continue;
			if (userIDs.contains(thread.getUserID())) {
				thread.output(message);
			}
		}
	}
	
	public void cancel() { interrupt(); }

	/**
	 * Welcomes the user and handles all their input
	 */
	@Override
	public void run() {
		try {
			this.output(String.format("%s %d", MessageHandler.RESP_WELCOME, this.userID));
			handleConnection();
		} catch (Exception e) {
		} finally {
			MessageHandler.handleMessage(MessageHandler.REQ_LOGOUT, this,
					this.lobbyModel);
		}
	}

	/**
	 * Close the socket
	 */
	public void closeSocket() {
		try {
			this.socket.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Reads from the input and handles the request
	 * 
	 * @throws IOException
	 */
	private void handleConnection() throws IOException {
		try {
			for (String line = in.readLine(); line != null; line = in
					.readLine()) {
				MessageHandler.handleMessage(line, this, this.lobbyModel);
			}
		} finally {
			out.close();
			in.close();
		}
	}
}
