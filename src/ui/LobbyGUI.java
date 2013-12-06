package ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import protocol.ClientSideMessageMaker;
import protocol.ClientSideResponseHandler;
import canvas.Canvas;

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

	private final JTable boardsTable;
	private final JTable boardUsersTable;
	private final JTable lobbyUsersTable;

	// create a grouping to help localize JObjects
	private final GroupLayout layout;

	private final Map<Integer, DefaultTableModel> hashOfAllModels;

	/**
	 * This list contains one column for the name of the boards. It is inside of
	 * a scroll pane object.
	 */
	private final DefaultTableModel boardsModel;
	private final JScrollPane boardsPane;

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
	private final DefaultTableModel lobbyUsersModel;
	private final JScrollPane lobbyUsersPane;

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	// TODO fix the above socket/out/in declarations

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
	public LobbyGUI() {

		boardsModel = new DefaultTableModel(0, 1) {
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

		lobbyUsersModel = new DefaultTableModel(0, 1) {
			@Override
			public boolean isCellEditable(int row, int column) {

				// Make the cells not editable
				return false;
			}
		};

		hashOfAllModels = new HashMap<Integer, DefaultTableModel>();
		hashOfAllModels.put(0, boardsModel);
		hashOfAllModels.put(1, boardUsersModel);
		hashOfAllModels.put(2, lobbyUsersModel);

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
		// final String[] boardHeader = new String[] { "Board name" };
		// boardsModel.insertRow(0, boardHeader);
		boardsTable = new JTable(boardsModel);
		boardsTable.setName("boardsTable");
		boardsPane = new JScrollPane(boardsTable);
		boardsPane.setName("boardsPane");
		JViewport a = new JViewport();
		
		boardsPane.setColumnHeaderView(a);

		System.out.println(boardsPane.getColumnHeader());
		// no lines
		boardsTable.setShowHorizontalLines(false);
		boardsTable.setShowVerticalLines(false);

		final String[] boardUsersHeader = new String[] { "Users in board" };
		boardUsersModel.insertRow(0, boardUsersHeader);
		boardUsersTable = new JTable(boardUsersModel);
		boardUsersTable.setName("boardUsersTable");
		boardUsersPane = new JScrollPane(boardUsersTable);
		boardUsersPane.setName("boardUsersPane");
		// no lines
		boardUsersTable.setShowHorizontalLines(false);
		boardUsersTable.setShowVerticalLines(false);

		final String[] lobbyUsersHeader = new String[] { "Users in lobby" };
		lobbyUsersModel.insertRow(0, lobbyUsersHeader);
		lobbyUsersTable = new JTable(lobbyUsersModel);
		lobbyUsersTable.setName("lobbyUsersTable");
		lobbyUsersPane = new JScrollPane(lobbyUsersTable);
		lobbyUsersPane.setName("lobbyUsersPane");
		// no lines
		lobbyUsersTable.setShowHorizontalLines(false);
		lobbyUsersTable.setShowVerticalLines(false);

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
								.addComponent(boardsPane, 250, 250,
										Short.MAX_VALUE)
								.addComponent(boardUsersPane, 200, 200,
										Short.MAX_VALUE)
								.addComponent(lobbyUsersPane, 200, 200,
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
								.addComponent(boardsPane, 150, 150,
										Short.MAX_VALUE)
								.addComponent(boardUsersPane, 150, 150,
										Short.MAX_VALUE)
								.addComponent(lobbyUsersPane, 150, 150,
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
		boardsPane.setVisible(set);
		boardUsersPane.setVisible(set);
		lobbyUsersPane.setVisible(set);
		this.pack();
	}

	public void sendPacketToServer(final String drawPacket) {
		out.println(drawPacket);
	}

	public void sendPacketToCanvas(ArrayList<Integer> data) {
		canvas.drawLineSegmentPacket(data);
		out.println("get_current_board_id");
	}

	public void addRowToCurrentBoardsModel(final int tableNumber,
			final String[] input) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				hashOfAllModels.get(tableNumber).addRow(input);
			}
		});
	}

	// TODO
	public void createCanvas(int boardID) {
		String givenName = newBoardField.getText();
		final String newBoardName;
		if (givenName.equals("")) {
			newBoardName = "[no name]";
		} else {
			newBoardName = givenName;
		}

		final String requestString;

		if (boardID == -1) {
			requestString = ClientSideMessageMaker
					.makeRequestStringCreateBoard(newBoardName);
		} else {
			requestString = ClientSideMessageMaker
					.makeRequestStringJoinBoardID(boardID);
		}
		out.println(requestString);

		newBoardField.setText("");
		String newName = userNameLabel.getText();
		String user = newName.substring(11);
		canvas = new Canvas(1000, 800, lobbyGUI, user);
		setVisible(false);
	}

	public void clearAllRows(final int tableNumber) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DefaultTableModel modelToClear = hashOfAllModels
						.get(tableNumber);
				int rowCount = modelToClear.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					modelToClear.removeRow(i);
				}
			}
		});
	}

	// TODO
	public void updateBoardUsers(final int whichUserTable) {
		clearAllRows(whichUserTable);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String requestString = ClientSideMessageMaker
						.makeRequestStringGetBoardIDs();
				out.println(requestString);
				;
			}
		});
	}

	/**
	 * TODO
	 */
	private void makeActions() {

		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: do something while connecting just in case it fails to
				// connect because of a wrong IP
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

					setVisibility(true);

					// now create a thread for the incoming stream
					Thread incomingStream = new Thread(new Runnable() {
						public void run() {
							// When we run the thread, have it repeatedly check
							// for incoming responses (from the server).
							String serverResponse;
							while (true) {
								try {
									while ((serverResponse = in.readLine()) != null) {
										// System.out.println("Server Response: "
										// + serverResponse);
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
					ClientSideMessageMaker.makeRequestStringGetBoardIDs();

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
				createCanvas(-1);
			}
		});

		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createCanvas(-1);
			}
		});

		// setting the scroll pane to automatically scroll to the last row
		boardsTable.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				boardsTable.scrollRectToVisible(boardsTable.getCellRect(
						boardsTable.getRowCount() - 1, 0, true));
			}
		});

		// setting the scroll pane to automatically scroll to the last row
		boardUsersTable.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				boardUsersTable.scrollRectToVisible(boardUsersTable
						.getCellRect(boardUsersTable.getRowCount() - 1, 0, true));
			}
		});

		// setting the scroll pane to automatically scroll to the last row
		lobbyUsersTable.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				lobbyUsersTable.scrollRectToVisible(lobbyUsersTable
						.getCellRect(lobbyUsersTable.getRowCount() - 1, 0, true));
			}
		});

		boardsTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();

				if (e.getClickCount() == 2 && row > 0) {
					int boardID = row - 1;
					out.println(ClientSideMessageMaker
							.makeRequestStringJoinBoardID(boardID));
					createCanvas(boardID);
				}

				else if (e.getClickCount() == 1 && row > 0) {
					updateBoardUsers(1);
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
