package ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import logger.BoardLogger;
import protocol.BoardListItem;
import protocol.Client;
import protocol.ClientSideMessageMaker;
import protocol.MessageHandler;
import adts.Line;
import adts.LobbyModel;
import adts.User;
import canvas.Canvas;

/**
 * 
 * This is the Controller and GUI for the Lobby. It serves as the connection
 * between the user View of the Canvas and the Server and Model. These
 * Controllers are independent instances given unique IDs by the Model.
 * 
 * IMPORTANT LOGGER INFO: The logger is initialized at the beginning of the file
 * to start collecting logs. Logs are put into levels based on their importance.
 * All exceptions are SEVERE while many of the other smaller logs like server
 * responses are INFO.
 * 
 * Thread-safety:
 * 
 * 
 * 
 * Testing strategy:
 * 
 * Because of its hard-to-test nature, all testing must be done by manually
 * using the GUI itself. The way this was done is as follows and in this order:
 * 
 * @category the first GUI is the JOptionPane used to connect to a given server
 *           IP. Test that it correctly connects (when there is a running server
 *           at the given IP) and that incorrect IPs return a JOptionPane
 *           showing the failure to connect. Also check that the Cancel button
 *           and red X will cause the entire program to exit. (verify with the
 *           Logger's WARNING: Exiting Lobby)
 * @category the second GUI is the main LobbyGUI. Start by testing the general
 *           aesthetics: make sure the labels, lists, boxes, and buttons are in
 *           the correct order and that no resizing of the window is allowed.
 *           Also make sure the tables auto-scroll by adding enough boards or
 *           users until there is an overflow.
 * @category test the username button by entering a string and watching it
 *           change the username lable.
 * @category test the create whiteboard button and expect to be automatically
 *           taken to a Canvas. Then leave the board and test that a new board
 *           is now listed in the table.
 * @category Finish testing by adding multiple users to the same LobbyModel and
 *           assuring their actions of creating boards/changing their usernames,
 *           are reflected here.
 * 
 */
public class LobbyGUI extends JFrame implements Client {

	// use the classname for the logger, this way you can refactor
	private final static Logger LOGGER = Logger.getLogger(LobbyGUI.class
			.getName());

	private static final long serialVersionUID = 1L;

	// socket and its input and output streams
	private final int port;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	// background thread to handle incoming messages
	private final LobbyGUIBackgroundThread serverMessagesThread;

	// canvas which allows drawing on whiteboard
	private Canvas canvas;

	// UI elements for changing username
	private final JLabel labelUserName;
	private final JButton btnSetUserName;
	private final JButton btnCreateBoard;

	// list of current boards
	private final JList<String> lstBoards;
	private final JScrollPane scrollLstBoards;
	private final DefaultListModel<String> lstMdlBoards;

	// list of all users
	private final JList<String> lstUsers;
	private final JScrollPane scrollLstUsers;
	private final DefaultListModel<String> lstMdlUsers;

	// layout manager
	private final GroupLayout layout;

	// self object
	private final LobbyGUI self;

	// this user object
	private User user;

	private List<BoardListItem> boardListItems;

	public LobbyGUI() {

		this.port = 4444;
		setupLogger(Level.ALL);

		// get the hostname and create the socket
		while (this.in == null) {
			try {
				String hostName = JOptionPane.showInputDialog(
						"Enter the hostname of the whiteboard server:",
						"localhost");
				if (hostName == null) {
					LOGGER.warning("Exiting Lobby");
					System.exit(0);
				}
				LOGGER.info("Hostname (IP) inputted: " + hostName);
				this.socket = new Socket(hostName, this.port);
				this.out = new PrintWriter(socket.getOutputStream(), true);
				this.in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
			} catch (Exception ex) {

				LOGGER.severe("Failed to connect to server ");
				JOptionPane.showMessageDialog(this,
						"Could not connect to given hostname. Try again.");
			}
		}

		// sets this current object
		this.self = this;

		// launch a thread to listen for messages
		this.serverMessagesThread = new LobbyGUIBackgroundThread(this, this.in);
		this.serverMessagesThread.start();

		// create the UI to view and change the username and new whiteboards
		this.labelUserName = new JLabel("User: placeholder");
		this.btnSetUserName = new JButton("Change Username");
		this.btnSetUserName.addActionListener(new SetUserNameListener());
		this.btnCreateBoard = new JButton("Create Whiteboard");
		this.btnCreateBoard.addActionListener(new CreateWhiteboardListener());

		// create the list of boards
		this.lstMdlBoards = new DefaultListModel<String>();
		this.lstBoards = new JList<String>(this.lstMdlBoards);
		this.lstBoards.setSelectedIndex(0);
		this.lstBoards.addMouseListener(new JoinBoardListener());
		this.scrollLstBoards = new JScrollPane(this.lstBoards);

		// create the list of users
		this.lstMdlUsers = new DefaultListModel<String>();
		this.lstUsers = new JList<String>(this.lstMdlUsers);
		this.lstUsers.setSelectedIndex(0);

		this.scrollLstUsers = new JScrollPane(this.lstUsers);

		Container contentPane = this.getContentPane();
		this.layout = new GroupLayout(contentPane);
		contentPane.setLayout(this.layout);
		this.layout.setAutoCreateGaps(true);
		this.layout.setAutoCreateContainerGaps(true);
		
		this.layout
				.setHorizontalGroup(this.layout
						.createParallelGroup()
						.addGroup(
								this.layout
										.createSequentialGroup()
										.addComponent(this.labelUserName, 100,
												100, 100)
										.addComponent(this.btnCreateBoard)
										.addComponent(this.btnSetUserName))
						.addGroup(
								this.layout
										.createSequentialGroup()
										.addComponent(this.scrollLstBoards,
												250, 250, 250)
										.addComponent(this.scrollLstUsers))

				);
		this.layout.setVerticalGroup(this.layout
				.createSequentialGroup()
				.addGroup(
						this.layout.createParallelGroup()
								.addComponent(this.labelUserName)
								.addComponent(this.btnCreateBoard)
								.addComponent(this.btnSetUserName))
				.addGroup(
						this.layout.createParallelGroup()
								.addComponent(this.scrollLstBoards)
								.addComponent(this.scrollLstUsers)));

		// Set title, size, and resizable
		this.setTitle("Whiteboard Lobby");
		this.setSize(500, 300);
		this.setResizable(false);

		
		this.makeRequest(ClientSideMessageMaker.makeRequestStringGetBoardIDs());
		this.makeRequest(ClientSideMessageMaker.makeRequestStringGetUsersForBoardID(LobbyModel.LOBBY_ID));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowListen());

	}

	private class WindowListen implements WindowListener {

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent e) {
			LOGGER.warning("Active LobbyGUI closed");
		}

		@Override
		public void windowClosed(WindowEvent e) {
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
		}

		@Override
		public void windowIconified(WindowEvent e) {
		}

		@Override
		public void windowOpened(WindowEvent e) {
		}
	}

	private void setupLogger(Level level) {
		try {
			BoardLogger.setup();
			LOGGER.setLevel(level);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}
	}

	public void makeRequest(String req) {
		out.println(req);
		LOGGER.fine("REQ: " + req);
	}

	public void onReceiveUserIDs(List<String> rcvdNames) {
		final List<String> userNames = rcvdNames;
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				lstMdlUsers.clear();
				for (String userName : userNames) {
					lstMdlUsers.addElement(userName);
				}
				lstUsers.setSelectedIndex(lstMdlUsers.size() - 1);
			}
		});
	}

	public void onReceiveUsernameChanged(String rcvdName) {
		final String newName = rcvdName;
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				user.setName(newName);
				labelUserName.setText("User: " + newName);
				JOptionPane.showMessageDialog(null, "Changed username to "
						+ newName);
				if (canvas != null) {
					canvas.onReceiveUsernameChanged(newName);
				}
			}
		});
	}

	public void onReceiveWelcome(int id) {

		LOGGER.info("Successful connection to server");

		this.user = new User(id);
		labelUserName.setText("User: Guest_" + String.valueOf(id));
	}

	private class SetUserNameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String newUser = JOptionPane.showInputDialog("Enter new user name");
			user.setName(newUser);
			if (newUser == null) {
				LOGGER.warning("No username set on exit of JOptionPane");
				return;
			}
			makeRequest(ClientSideMessageMaker
					.makeRequestStringSetUsername(newUser));
		}
	}

	private class CreateWhiteboardListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String newBoard = JOptionPane
					.showInputDialog("Enter new Whiteboard name");
			if (newBoard == null) {
				LOGGER.warning("No canvas created on exit of JOptionPane");
				return;
			}

			String canvasName = newBoard;
			if (canvasName.equals("")) {
				canvasName = "[No name]";
			}
			canvas = new Canvas(1000, 1000, self, user.getName(), -1,
					canvasName);
			canvas.setVisible(true);
			setVisible(false);
			out.println(ClientSideMessageMaker
					.makeRequestStringCreateBoard(newBoard));
		}
	}

	private class JoinBoardListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int selectedIndex = lstBoards.getSelectedIndex();
				for (BoardListItem boardListItem : boardListItems) {
					if (boardListItem.getBoardIndex() == selectedIndex) {
						canvas = new Canvas(1000, 1000, self, user.getName(),
								boardListItem.getBoardID(),
								boardListItem.getBoardName());
						canvas.setVisible(true);
						setVisible(false);
						out.println(MessageHandler
								.makeRequestStringJoinBoardID(boardListItem
										.getBoardID()));
					}
				}
			}
		}
	}

	@Override
	public void onReceiveDraw(Line l) {
		if (canvas != null)
			canvas.onReceiveDraw(l);
	}

	@Override
	public void onReceiveBoardLines(List<Line> ls, Set<String> userNames) {
		if (canvas != null) {
			canvas.onReceiveBoardLines(ls, userNames);
		}
	}

	@Override
	public void onReceiveClear() {
		if (canvas != null)
			canvas.onReceiveClear();
	}

	@Override
	public void onReceiveUsers(int boardID, List<String> users) {
		if (canvas != null)
			canvas.onReceiveUsers(boardID, users);
		final int finalBoardID = boardID;
		final List<String> finalUsers = users;
		SwingUtilities.invokeLater(new Thread(){
		    @Override
		    public void run() {
		        if(finalBoardID == LobbyModel.LOBBY_ID){
		            lstMdlUsers.clear();
		            for(String user : finalUsers){
		                lstMdlUsers.addElement(user);
		            }
		        }
		    };
		});
		
	}

	@Override
	public void onReceiveCurrentBoardID(int boardID) {
		if (canvas != null)
			canvas.onReceiveCurrentBoardID(boardID);
	}

	@Override
	public void onReceiveBoardIDs(Map<Integer, String> rcvdBoardNameForID) {
		final Map<Integer, String> boardNameForID = rcvdBoardNameForID;
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				boardListItems = new ArrayList<BoardListItem>();
				int i = 0;
				for (int boardID : boardNameForID.keySet()) {
					boardListItems.add(new BoardListItem(boardNameForID
							.get(boardID), i, boardID));
					i++;
				}
				lstMdlBoards.clear();
				for (BoardListItem boardListItem : boardListItems) {
					lstMdlBoards.addElement(boardListItem.getBoardName());
					;
				}
			}
		});
	}

	/**
	 * This is the main function.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// the GUI needs to be instantiated with the Controller given ID
				LobbyGUI main = new LobbyGUI();
				main.setVisible(true);
			}
		});
	}
}
