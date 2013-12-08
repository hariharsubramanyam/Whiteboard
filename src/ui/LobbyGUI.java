package ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import adts.Line;
import adts.User;
import protocol.Client;
import protocol.ClientSideMessageMaker;
import protocol.MessageHandler;
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
public class LobbyGUI extends JFrame implements Client{

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
	private final JTextField txtSetUserName;
	private final JButton btnSetUserName;

	// list of current boards
	private final JList lstBoards;
	private final JScrollPane scrollLstBoards;
	private final DefaultListModel lstMdlBoards;
	
	// button to join board;
	private final JButton btnJoinBoard;
	
	// layout manager
	private final GroupLayout layout;

	// self object
	private final LobbyGUI self;
	
	// this user object
	private User user;
	
	public LobbyGUI(){
	    // get the hostname and create the socket
	    try{
	        String hostName = JOptionPane.showInputDialog("Enter the hostname of the whiteboard server:","localhost");
    	    this.socket = new Socket(hostName, 4444);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    }catch(Exception ex){
	        ex.printStackTrace();
	    }
	    
	    // sets this current object
	    this.self = this;
            
        // launch a thread to listen for messages
        this.serverMessagesThread = new LobbyGUIBackgroundThread(this, this.in);
        this.serverMessagesThread.start();
        
        // create the UI to view and change the username
        this.txtSetUserName = new JTextField("No Username");
        this.txtSetUserName.addActionListener(new SetUserNameListener());
        this.btnSetUserName = new JButton("Change Username");
        this.btnSetUserName.addActionListener(new SetUserNameListener());
        
        // create the list of boards 
        this.lstMdlBoards = new DefaultListModel<String>();
        this.lstMdlBoards.addElement("Create a new board...");
        this.lstBoards = new JList<String>(this.lstMdlBoards);
        this.lstBoards.setSelectedIndex(0);
        this.scrollLstBoards = new JScrollPane(this.lstBoards);
       
        // join board button
        this.btnJoinBoard = new JButton("Join Board");
        this.btnJoinBoard.addActionListener(new JoinBoardListener());
        
        Container contentPane = this.getContentPane();
        this.layout = new GroupLayout(contentPane);
        contentPane.setLayout(this.layout);
        this.layout.setAutoCreateGaps(true);
        this.layout.setAutoCreateContainerGaps(true);
        this.layout.setHorizontalGroup(this.layout
                .createParallelGroup()
                    .addGroup(this.layout
                            .createSequentialGroup()
                                .addComponent(this.txtSetUserName)
                                .addComponent(this.btnSetUserName))
                    .addComponent(this.scrollLstBoards)
                    .addComponent(this.btnJoinBoard)
                );
        this.layout.setVerticalGroup(this.layout
                .createSequentialGroup()
                    .addGroup(this.layout
                            .createParallelGroup()
                                .addComponent(this.txtSetUserName,30,30,30)
                                .addComponent(this.btnSetUserName))
                    .addComponent(this.scrollLstBoards)
                    .addComponent(this.btnJoinBoard)
                );
        
        // Set title, size, and resizable
        this.setTitle("Whiteboard Lobby");
        this.setSize(300, 300);
        this.setResizable(false);
        
        this.makeRequest(ClientSideMessageMaker.makeRequestStringGetBoardIDs());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
	}
	
	public void makeRequest(String req){
	    out.println(req);
	    System.out.println("REQ: " + req);
	}
	
	public void onReceiveBoardIDs(List<Integer> rcvdIDs){
	    final List<Integer> boardIDs = rcvdIDs;
	    SwingUtilities.invokeLater(new Thread(){
	        @Override
	        public void run() {
	            lstMdlBoards.clear();
	            for(int boardID : boardIDs){
	                lstMdlBoards.addElement("Board " + boardID);
	            }
                lstMdlBoards.addElement("Create a new board...");
                lstBoards.setSelectedIndex(lstMdlBoards.size()-1);
	        }
	    });
	}
	
	public void onReceiveUsernameChanged(String rcvdName){
	    final String newName = rcvdName;
        SwingUtilities.invokeLater(new Thread(){
            @Override
            public void run() {
                txtSetUserName.setText(newName);
                JOptionPane.showMessageDialog(null, "Changed username to " + newName);
            }
        });
	}
	
	public void onReceiveWelcome(int id){
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

	
	private class SetUserNameListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            user.setName(txtSetUserName.getText());
            String userName = user.getName();
            makeRequest(ClientSideMessageMaker.makeRequestStringSetUsername(userName));
        }
	}
	
	private class JoinBoardListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedItem = (String)lstBoards.getSelectedValue();
            canvas = new Canvas(1000, 1000, self, user.getName());
            canvas.setVisible(true);
            setVisible(false);
            if(selectedItem.equals("Create a new board...")){
                out.println(MessageHandler.makeRequestStringCreateBoard("MyBoard"));
            }else{
                out.println(MessageHandler.makeRequestStringJoinBoardID(Integer.parseInt(selectedItem.replace("Board ", ""))));
            }
            
        }
	}

    @Override
    public void onReceiveDraw(Line l) {
        canvas.onReceiveDraw(l);
    }

    @Override
    public void onReceiveBoardLines(List<Line> ls) {
        canvas.onReceiveBoardLines(ls);
    }

    @Override
    public void onReceiveClear() {
        canvas.onReceiveClear();
    }

    @Override
    public void onReceiveUsers(List<String> users) {
        canvas.onReceiveUsers(users);
    }
}
