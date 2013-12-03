package ui;

import protocol.ClientSideMessageMaker;
import protocol.ClientSideResponseHandler;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import canvas.Canvas;
import adts.LobbyModel;

/**
 * 
 * This is the Controller and GUI for the Lobby. It serves as the connection
 * between the user View of the Canvas and the Server and Model. These
 * Controllers are independent instances given unique IDs by the Model.
 * 
 * Testing strategy:
 * 
 * Because of its hard-to-test nature, all testing must be done by manually
 * using the GUI itself. The way this was done is as follows and in this order:
 * 
 * @category general aesthetics: make sure the labels, boxes, and buttons are in
 *           the correct order and that resizing the window causes the two
 *           textboxes to get wider (but not taller), with no upperbound, and
 *           that the table gets wider (no upperbound) and taller (upperbound of
 *           500 pixels).
 * 
 */
public class LobbyGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private Canvas canvas;

	private final JLabel serverIpLabel;
	private final JTextField serverIpField;
	private final JLabel portLabel;
	private final JButton connect;

	private final JLabel newBoardLabel;
	private final JTextField newBoardField;
	private final JButton createButton;

	private final JLabel userNameLabel;
	private final JLabel userNameSetLabel;
	private final JTextField userNameField;

	private final JTable currentBoards;
	private final JTable boardUsers;
	private final JTable allUsers;

	// create a grouping to help localize JObjects
	private final GroupLayout layout;

	/**
	 * This list contains two columns: one for the name of the boards and the
	 * other with the number of users. It is inside of a scroll pane object.
	 */
	private final DefaultTableModel currentBoardsModel;

	private final JScrollPane currentBoardsPane;

	/**
	 * This list contains one column: the name of the users in the board in
	 * focus. It is inside of a scroll pane object.
	 */
	private final DefaultTableModel boardUsersModel;
	private final JScrollPane boardUsersPane;

	/**
	 * This list contains one column for the name of the users. It is inside of
	 * a scroll pane object.
	 */
	private final DefaultTableModel allUsersModel;
	private final JScrollPane allUsersPane;

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	// TODO fix the above socket/out/in declarations

	private LobbyModel lobby;
	private int userID;
	private LobbyGUI lobbyGUI;

	/**
	 * The constructor for this class can be separated into three distinct
	 * operations. The first is to link an instance of our Model to this GUI
	 * instance. Then the GUI itself is initialized with all the right
	 * components using JSwing. Finally, the action listeners are initiated by
	 * calling on makeActions().
	 */
	@SuppressWarnings("serial")
	public LobbyGUI(int ID) {

		currentBoardsModel = new DefaultTableModel(0, 1) {
			@Override
			public boolean isCellEditable(int row, int column) {

				// Make the cells not editable
				return false;
			}
		};

		boardUsersModel = new DefaultTableModel(0, 1) {
			@Override
			public boolean isCellEditable(int row, int column) {

				// Make the cells not editable
				return false;
			}
		};

		allUsersModel = new DefaultTableModel(0, 1) {
			@Override
			public boolean isCellEditable(int row, int column) {

				// Make the cells not editable
				return false;
			}
		};

		this.userID = ID;
		this.lobbyGUI = this;
		/*
		 * Initialize the top row with the server information
		 */
		serverIpLabel = new JLabel("Server IP:");
		serverIpLabel.setName("serverIpLabel");
		serverIpField = new JTextField();
		serverIpField.setName("serverIpTextField");
		portLabel = new JLabel("Port: 4444");
		portLabel.setName("portLabel");
		connect = new JButton("Connect!");
		connect.setName("connect");

		/*
		 * Initialize the second row with the user information
		 */
		userNameLabel = new JLabel("User name: " + this.userID);
		userNameLabel.setName("userNameLabel");
		userNameSetLabel = new JLabel("Set name: ");
		userNameSetLabel.setName("userNameSetLabel");
		userNameField = new JTextField();
		userNameField.setName("userNameField");

		/*
		 * Initialize the third row with the board information
		 */
		newBoardLabel = new JLabel("Create new board: ");
		newBoardLabel.setName("newBoardLabel");
		newBoardField = new JTextField();
		newBoardField.setName("newBoardTextField");
		createButton = new JButton("Create and join!");
		createButton.setName("createButton");

		/*
		 * Initialize the last row with the three tables inside their own
		 * respective panes
		 */
		final String[] boardHeader = new String[] { "Board name" };
		currentBoardsModel.insertRow(0, boardHeader);
		currentBoards = new JTable(currentBoardsModel);
		currentBoards.setName("currentBoards");
		currentBoardsPane = new JScrollPane(currentBoards);
		currentBoardsPane.setName("currentBoardsPane");
		// no lines
		currentBoards.setShowHorizontalLines(false);
		currentBoards.setShowVerticalLines(false);

		final String[] userHeader = new String[] { "Users in board" };
		boardUsersModel.insertRow(0, userHeader);
		boardUsers = new JTable(boardUsersModel);
		boardUsers.setName("boardUsers");
		boardUsersPane = new JScrollPane(boardUsers);
		boardUsersPane.setName("boardUsersPane");
		// no lines
		boardUsers.setShowHorizontalLines(false);
		boardUsers.setShowVerticalLines(false);

		final String[] allUsersHeader = new String[] { "Users in lobby" };
		allUsersModel.insertRow(0, allUsersHeader);
		allUsers = new JTable(allUsersModel);
		allUsers.setName("allUsers");
		allUsersPane = new JScrollPane(allUsers);
		allUsersPane.setName("allUsersPane");
		// no lines
		allUsers.setShowHorizontalLines(false);
		allUsers.setShowVerticalLines(false);

		// Default settings and layout setup
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container cp = this.getContentPane();

		layout = new GroupLayout(cp);
		cp.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(serverIpLabel, 60, 60, 60)
								.addComponent(serverIpField, 100, 100,
										Short.MAX_VALUE)
								.addComponent(portLabel, 60, 60, 60)
								.addComponent(connect, 90, 90, 90))
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(userNameLabel, 150, 150, 150)
								.addComponent(userNameSetLabel, 60, 60, 60)
								.addComponent(userNameField, 90, 90,
										Short.MAX_VALUE))
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(newBoardLabel, 120, 120, 120)
								.addComponent(newBoardField, 90, 90,
										Short.MAX_VALUE)
								.addComponent(createButton, 130, 130, 130))
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(currentBoards, 250, 250,
										Short.MAX_VALUE)
								.addComponent(boardUsers, 200, 200,
										Short.MAX_VALUE)
								.addComponent(allUsers, 200, 200,
										Short.MAX_VALUE)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
								.addComponent(serverIpLabel, 25, 25, 25)
								.addComponent(serverIpField, 25, 25, 25)
								.addComponent(portLabel, 25, 25, 25)
								.addComponent(connect, 25, 25, 25))
				.addGroup(
						layout.createParallelGroup()
								.addComponent(userNameLabel, 25, 25, 25)
								.addComponent(userNameSetLabel, 25, 25, 25)
								.addComponent(userNameField, 25, 25, 25))
				.addGroup(
						layout.createParallelGroup()
								.addComponent(newBoardLabel, 25, 25, 25)
								.addComponent(newBoardField, 25, 25, 25)
								.addComponent(createButton, 25, 25, 25))
				.addGroup(
						layout.createParallelGroup()
								.addComponent(currentBoards, 150, 150,
										Short.MAX_VALUE)
								.addComponent(boardUsers, 150, 150,
										Short.MAX_VALUE)
								.addComponent(allUsers, 150, 150,
										Short.MAX_VALUE)));

		setVisibility(false);
		this.pack();

		// fire the action listeners
		makeActions();
	}

	/**
	 * Used to change the visibility of the server login information and the
	 * connected lobby.
	 * 
	 * @param set
	 *            boolean which is false when server information is needed and
	 *            true otherwise
	 */
	private void setVisibility(boolean set) {
		serverIpLabel.setVisible(!set);
		serverIpField.setVisible(!set);
		portLabel.setVisible(!set);
		connect.setVisible(!set);
		userNameLabel.setVisible(set);
		userNameSetLabel.setVisible(set);
		userNameField.setVisible(set);
		newBoardLabel.setVisible(set);
		newBoardField.setVisible(set);
		userNameField.setVisible(set);
		createButton.setVisible(set);
		currentBoards.setVisible(set);
		boardUsers.setVisible(set);
		allUsers.setVisible(set);
		this.pack();
	}

	public void sendPacketToServer(String drawPacket) {
		out.println(drawPacket);
	}

	public void sendPacketToCanvas() {
		System.out.println("test");
	}

	private void addRowToCurrentBoardsModel(final int tableNumber,
			final String[] input) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (tableNumber == 0) {
					currentBoardsModel.addRow(input);
				}
				else if (tableNumber == 1) {
					boardUsersModel.addRow(input);
				}
				else if (tableNumber == 2) {
					allUsersModel.addRow(input);
				}
			}
		});
	}

	private void createCanvas() {
		final String newBoardName = newBoardField.getText();

		String requestString = ClientSideMessageMaker
				.makeRequestStringCreateBoard(newBoardName);
		out.println(requestString);
		// TODO: the incoming boards will set the boards table

		newBoardField.setText("");
		String newName = userNameLabel.getText();
		String user = newName.substring(11);
		canvas = new Canvas(1000, 800, lobbyGUI, user);
		setVisible(false);
	}

	/**
	 * TODO
	 */
	private void makeActions() {

		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: do something while connecting just in case it fails to
				// connect because of a wrong IP
				setVisibility(true);
				try {
					String ip; // IP to connect to
					if (serverIpField.getText().toLowerCase()
							.equals("localhost")) {
						ip = "127.0.0.1"; // localhost IP
					} else {
						ip = serverIpField.getText();
					}

					socket = new Socket(ip, 4444);
					out = new PrintWriter(socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(socket
							.getInputStream()));

					// now create a thread for the incoming stream
					Thread incomingStream = new Thread(new Runnable() {
						public void run() {
							// When we run the thread, have it repeatedly check
							// for incoming responses (from the server).
							String serverResponse;
							while (true) {
								try {
									while ((serverResponse = in.readLine()) != null) {
										ClientSideResponseHandler
												.handleResponse(serverResponse,
														lobbyGUI);
									}
								} catch (IOException e) {
									System.out
											.println("I/O error in incomingStream in LobbyGUI.java");
								}
							}
						}
					});
					incomingStream.start();

					// populate the boards table
					String[] boardIDs = ClientSideMessageMaker
							.makeRequestStringGetBoardIDs()
							.replace("board_ids", "").split(" ");
					for (String boardID : boardIDs) {
						addRowToCurrentBoardsModel(0, new String[] {boardID});
					}

				} catch (IOException ex) {
					System.out.println("Couldn't connect");
				}

			}
		});

		userNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = userNameField.getText();
				// send new name to server
				String requestString = ClientSideMessageMaker
						.makeRequestStringSetUsername(newName);
				out.println(requestString);
				userNameLabel.setText("User name: " + newName);
				userNameField.setText("");
			}
		});

		newBoardField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createCanvas();
				// TODO why is this createCanvas() here?
			}
		});

		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String boardName = newBoardField.getText();
				String requestString = ClientSideMessageMaker
						.makeRequestStringCreateBoard(boardName);
				out.println(requestString);
				createCanvas();
			}
		});

		// setting the scroll pane to automatically scroll to the last row
		currentBoards.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				currentBoards.scrollRectToVisible(currentBoards.getCellRect(
						currentBoards.getRowCount() - 1, 0, true));
			}
		});

		// setting the scroll pane to automatically scroll to the last row
		boardUsers.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				boardUsers.scrollRectToVisible(boardUsers.getCellRect(
						boardUsers.getRowCount() - 1, 0, true));
			}
		});

		// setting the scroll pane to automatically scroll to the last row
		allUsers.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				allUsers.scrollRectToVisible(allUsers.getCellRect(
						allUsers.getRowCount() - 1, 0, true));
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
				LobbyGUI main = new LobbyGUI(1000000000);
				main.setVisible(true);
			}
		});
	}
}
