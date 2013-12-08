package ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import protocol.Client;
import protocol.ClientSideMessageMaker;
import protocol.MessageHandler;
import adts.Line;
import adts.User;
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
public class LobbyGUI extends JFrame implements Client {

	private static final long serialVersionUID = 1L;

	// socket and its input and output streams
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

	public LobbyGUI() {
		// get the hostname and create the socket
		try {
			String hostName = JOptionPane
					.showInputDialog(
							"Enter the hostname of the whiteboard server:",
							"localhost");
			this.socket = new Socket(hostName, 4444);
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// sets this current object
		this.self = this;

		// launch a thread to listen for messages
		this.serverMessagesThread = new LobbyGUIBackgroundThread(this, this.in);
		this.serverMessagesThread.start();

		// create the UI to view and change the username and new whiteboards
		this.labelUserName = new JLabel("User: guest");
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
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void makeRequest(String req) {
		out.println(req);
		System.out.println("REQ: " + req);
	}

	public void onReceiveBoardIDs(List<Integer> rcvdIDs) {
		final List<Integer> boardIDs = rcvdIDs;
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				lstMdlBoards.clear();
				for (int boardID : boardIDs) {
					lstMdlBoards.addElement("Board " + boardID);
				}
				lstBoards.setSelectedIndex(lstMdlBoards.size() - 1);
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
			}
		});
	}

	public void onReceiveWelcome(int id) {
		System.out.println("Here");
		this.user = new User(id);
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

	private class SetUserNameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String newUser = JOptionPane.showInputDialog("Enter new user name");
			user.setName(newUser);
			makeRequest(ClientSideMessageMaker
					.makeRequestStringSetUsername(newUser));
		}
	}
	private class CreateWhiteboardListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String newBoard = JOptionPane
					.showInputDialog("Enter new Whiteboard name");
			canvas = new Canvas(1000, 1000, self, user.getName());
			canvas.setVisible(true);
			setVisible(false);
			out.println(ClientSideMessageMaker
					.makeRequestStringCreateBoard(newBoard));
		}
	}

	private class JoinBoardListener extends MouseAdapter {
	    @Override
	    public void mouseClicked(MouseEvent e) {
	        if(e.getClickCount() == 2){
	            String selectedItem = (String) lstBoards.getSelectedValue();
	            canvas = new Canvas(1000, 1000, self, user.getName());
	            canvas.setVisible(true);
	            setVisible(false);
	            out.println(MessageHandler.makeRequestStringJoinBoardID(Integer.parseInt(selectedItem.replace("Board ", ""))));
	            
	        }
	    }
	}

    @Override
    public void onReceiveDraw(Line l) {
        if(canvas != null)
            canvas.onReceiveDraw(l);
    }

    @Override
    public void onReceiveBoardLines(List<Line> ls, Set<String> userNames) {
        if(canvas != null){
            canvas.onReceiveBoardLines(ls, userNames);
        }
    }

    @Override
    public void onReceiveClear() {
        if(canvas != null)
            canvas.onReceiveClear();
    }

    @Override
    public void onReceiveUsers(List<String> users) {
        if(canvas != null)
            canvas.onReceiveUsers(users);
    }
}
