package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

import adts.Whiteboard;

/**
 * Thread-safety of MineSweeper game:
 * 
 * Summary:
 * 
 * This server uses a hybrid queue messaging system/mutex to lock the instance
 * of the board where individual threads can make calls to a single public
 * synchronized method on the Board, action(). Since we don't want the user to
 * queue an action and continue working (like messaging systems are supposed to
 * work), it makes sense not to make use of a fully integrated queue messaging
 * system. This hybrid of the two clears the code of all concurrency bugs.
 * 
 * Details:
 * 
 * As discussed in the Board class, the only way to mutate the workingBoard is
 * through the action() method. This method is thread-safe thanks to Java's
 * synchronized feature. This means only one Thread can ever call on
 * workingBoard.action() making this server thread-safe.
 * 
 * The only other public methods of the Board class are equals(), toString(),
 * hashCode(), checkRep(), getSize(), and the constructors. However, we argue
 * that none of these pose potential thread-threats since none of them mutate
 * objects and only toString() could show delayed data but this would simply be
 * at most one action() late.
 * 
 * Thread-safety check list:
 * 
 * 1: concurrency locks:
 * 
 * a) confinement: only one Thread modifying the object at a time, we have one
 * Board instance so locking every private method on Board yields thread-safety
 * 
 * b) strong immutability: both the reference and object are immutable, not
 * applicable here since we are working with a mutable Board object
 * 
 * c) thread safe data types: use synchronized Collections to create the Map of
 * Cells and synchronized data types to perform atomic operations when the Board
 * lock is obtained. We only ever initialize the Board instance with risky
 * operations but this is done when there is only the server thread running
 * 
 * d) locking: use synchronize on methods to lock "this" Board's every method
 * 
 * 2: dead locks: not possible because there is only one instance of Board so at
 * any given point, only one Thread is mutating it
 * 
 * a) lock ordering: there is no lock ordering implemented here.
 * 
 * b) coarse grain locking: there is a big lock on "this" Board therefore it may
 * be considered coarse since every method is synchronized but, again, having a
 * single instance of Board eliminates problems with thread-safety
 * 
 * As a last point to make, the static Integer numberOfPlayers could introduce
 * concurrency bugs therefore, when incrementing, decrementing, and printing it,
 * we make sure to synchronize on the Integer
 * 
 */
public class WhiteboardServer {
	private static Map<Integer, Whiteboard> a = Collections
			.synchronizedMap(new HashMap<Integer, Whiteboard>());
	private final ServerSocket serverSocket;
	private static Integer numberOfPlayers = 0; // we may need this
	/**
	 * True if the server should _not_ disconnect a client.
	 */
	private final boolean debug;

	/**
	 * Make a WhiteboardServer that listens for connections on port.
	 * 
	 * @param port
	 *            port number, requires 0 <= port <= 65535
	 */
	public WhiteboardServer(int port, boolean debug) throws IOException {
		serverSocket = new ServerSocket(port);
		this.debug = debug;
	}

	/**
	 * Run the server, listening for client connections and handling them. Never
	 * returns unless an exception is thrown.
	 * 
	 * @throws IOException
	 *             if the main server socket is broken (IOExceptions from
	 *             individual clients do *not* terminate serve())
	 */
	public void serve() throws IOException {
		while (true) {
			// block until a client connects
			final Socket socket = serverSocket.accept();
			synchronized (numberOfPlayers) {
				numberOfPlayers++;
			}

			Thread thread = new Thread(new Runnable() {
				public void run() {
					// handle the client
					try {
						handleConnection(socket);
					} catch (IOException e) {
						e.printStackTrace(); // but don't terminate serve()
					} finally {
						try {
							synchronized (numberOfPlayers) {
								numberOfPlayers--;
							}
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			thread.start();
		}
	}

	/**
	 * Handle a single client connection. Returns when client disconnects.
	 * 
	 * @param socket
	 *            socket where the client is connected
	 * @throws IOException
	 *             if connection has an error or terminates unexpectedly
	 */
	private void handleConnection(Socket socket) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

		try {
			synchronized (numberOfPlayers) {
				out.println("Welcome to this whiteboard."
						+ String.valueOf(numberOfPlayers)
						+ " people are playing including you. Type 'help' for help.");
			}

			/**
			 * Here is where we will be receiving the serialized "JSON" objects
			 */
			for (String line = in.readLine(); line != null; line = in
					.readLine()) {
				String output = handleRequest(line);
				if (output != null) {
					out.println(output);
					if (output.equals("Leave whiteboard")) {
						break;
					}
				}
			}
		} finally {
			out.close();
			in.close();
		}
	}

	/**
	 * Handler for client input, performing requested operations and returning
	 * an output message.
	 * 
	 * TODO: Right now it's all crap. We need to invoke the Deserializer here.
	 * Its function will be to receive this input String and turn it into a Map.
	 * It then accesses the data (which should be Pixel data) and modifies the
	 * master Whiteboard instance that this user is currently in. The last step
	 * is to send to all users connected to this Whiteboard, the command to
	 * paint their own boards.
	 * 
	 * TODO: the return statement isn't 
	 * 
	 * @param input
	 *            message from client
	 * @return message to client
	 */
	private String handleRequest(String input) {
//		String regex = "(look)|(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|"
//				+ "(deflag -?\\d+ -?\\d+)|(help)|(bye)";
//		if (!input.matches(regex)) {
//			// invalid input
//			return null;
//		}
//		String[] tokens = input.split(" ");
//		if (tokens[0].equals("look")) {
//			return workingBoard.action("look", 0, 0);
//		} else if (tokens[0].equals("help")) {
//			return "Possible actions to take: "
//					+ "look: print the current state of the board "
//					+ "dig: dig a cell on the board "
//					+ "flag: put a flag on a cell "
//					+ "deflag: remove a flag from a cell "
//					+ "bye: exit the server.";
//		} else if (tokens[0].equals("bye")) {
//			return "Goodbye";
//		} else {
//			int x = Integer.parseInt(tokens[1]);
//			int y = Integer.parseInt(tokens[2]);
//			if (tokens[0].equals("dig")) {
//				return workingBoard.action("dig", x, y);
//			} else if (tokens[0].equals("flag")) {
//				return workingBoard.action("flag", x, y);
//			} else if (tokens[0].equals("deflag")) {
//				return workingBoard.action("deflag", x, y);
//			}
//		}
//		
		// Should never get here--make sure to return in each of the valid cases
		// above.
		throw new UnsupportedOperationException();
	}

	/**
	 * TODO: Start the server.
	 * 
	 */
	public static void main(String[] args) {
		// Command-line argument parsing is provided. Do not change this method.
		boolean debug = true;
		int port = 4444; // default port

		Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
		while (!arguments.isEmpty()) {
			// Deserialize arguments
			return;
		}

		try {
			runWhiteboardServer(port, debug);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO: more crap. This should be the "Lobby" getting initialized. The
	 * Lobby is a place where users are in this sort of limbo until they are
	 * assigned an instance of a Whiteboard.
	 * 
	 * Start a MinesweeperServer running on the specified port, with either a
	 * random new board or a board loaded from a file. Either the file or the
	 * size argument must be null, but not both.
	 * 
	 * @param debug
	 *            TODO
	 * @param port
	 *            The network port on which the server should listen.
	 */
	public static void runWhiteboardServer(int port, boolean debug) throws IOException {

		WhiteboardServer server;
		try {
			server = new WhiteboardServer(port, debug);
			server.serve();
			// TODO: something with a Lobby
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
