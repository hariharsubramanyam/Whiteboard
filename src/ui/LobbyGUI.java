package ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import adts.LobbyModel;

/**
 * 
 * TODO: this is from my Jotto GUI. We need to modify it.
 * 
 * Multi-threaded GUI for playing the game Jotto. This simple class creates the
 * layout for playing the game by using JSwing components. The user inputs
 * textual guesses of the word which get sent to a remote server as a GET
 * request. The server then, behind the scenes and on its own thread, returns
 * either a valid or invalid response. Regardless of the time it takes for the
 * server to respond, the user can continue to make use of the GUI, hence its
 * multi-threaded behavior.
 * 
 * Thread-safe argument:
 * 
 * As mentioned before, multi-threading occurs only during server calls in the
 * typeGuess textbox. The only object being worked on, however, is the table.
 * Therefore, we make this mutable object thread-safe in the following way.
 * There is an initial set of three atomic integers.
 * 
 * The first is the gameCounter which is incremented (in an atomic way) only
 * when a new game is created. Getting this value (atomically) is used to assert
 * that each thread is still connected to the puzzle that was active when the
 * thread was created.
 * 
 * The second and third are the totalRows and rowCounter atomic integers. The
 * rowCounter value is assigned to a local integer as soon as a thread is
 * created, thereby giving this thread a unique identifier to its target row
 * later on. The totalRows variable, however, is not assigned in the thread
 * because it is the reference variable. It is the link between the disconnected
 * thread state and the current state of the puzzle. Since both of these are
 * atomic, and there is no more data being transferred between threads, we can
 * safely assume that the GUI is completely thread-safe.
 * 
 * Lastly, there is a the issue of how to safely pass the text from the
 * typeGuess textbox to each of the threads in a safe manner. If two users
 * activate the listener for this textbox, then there could be race conditions
 * where the String in the textbox from one user is used for both threads.
 * However, there is only one user, in this case the GUI, so we will never have
 * multiple calls to this method, making the GUI, once again, thread-safe.
 * 
 * 
 * 
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
 * @category puzzle number label, button, and textbox: test that clicking on the
 *           New Puzzle button changes the puzzle label and if the new puzzle
 *           textbox had a valid number, check to see that the label is
 *           correctly changed. Any non-valid entry will causes the puzzle
 *           number to be randomly assigned.
 * @category guess label and table: to test that these were working correctly
 *           (after asserting that the Model worked as planned), enter puzzle
 *           number 5555 and check to see the correct behavior ("You win!" on
 *           column 2 and the guess in column 1) when entering the string
 *           "vapid" in the guess box. Since the Model works, we don't test any
 *           other entries except for an invalid entry to see it appear on the
 *           second column.
 * @category testing the scroll pane: we expect the pane to automatically scroll
 *           to the latest entered row so put in enough rows to see this
 *           behavior happen.
 * @category threading: still in puzzle 5555, test the String "vapi*" and expect
 *           the GUI to allow for other submissions. Enter others and watch the
 *           table keep populating itself and, after certain time, the response
 *           for "vapi*" returns. The thread-safety is also tested by sending
 *           "vapi*" and immediately creating a New Puzzle. Waiting should not
 *           do anything since the late response gets tossed.
 * 
 */
public class LobbyGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// Use these objects in the GUI:
	private final JLabel puzzleNumber;
	private final JButton newPuzzleButton;
	private final JTextField newPuzzleNumber;
	private final JLabel typeGuess;
	private final JTextField guess;
	private final JTable guessTable;

	// create a grouping to help localize JObjects
	private final GroupLayout layout;
	// initialize the table model which will be used in guessTable. It is
	// initialized to start with 0 rows and 3 columns
	private final DefaultTableModel tableModel = new DefaultTableModel(0, 3);
	private final JScrollPane pane;

	private LobbyModel lobby;
	private int userID = 0;

	// to allow for multi-threading guess commands, we must protect against user
	// inputs after a delayed response. For this reason, we create these atomic
	// integers to keep track of delayed behavior.

	// rowCounter will give each thread an identifier to know where to post its
	// delayed response. Then guessTry is the thread-safe text from
	// the guessText textbox
	private AtomicInteger rowCounter = new AtomicInteger(-1);

	// if a new game is initialized while a thread is still waiting for server
	// response, this response will be discarded. this counter is incremented
	// whenever the puzzle button or puzzle text field are fired
	private AtomicInteger gameCounter = new AtomicInteger(0);

	// these three strings are the three possible error messages we can receive
	// from the server
	private final List<String> errorMessages = Arrays
			.asList("error 0: Ill-formatted request.",
					"error 1: Non-number puzzle ID.",
					"error 2: Invalid guess. Length of guess != 5 or guess is not a dictionary word.");

	/**
	 * The constructor for this class can be separated into three distinct
	 * operations. The first is to link an instance of our Model to this GUI
	 * instance. Then the GUI itself is initialized with all the right
	 * components using JSwing. Finally, the action listeners are initiated by
	 * calling on makeActions().
	 */
	public LobbyGUI(int ID) {
		this.userID = ID;

		// Initialize the top row consisting of the three elements for
		// displaying/changing puzzle number
		puzzleNumber = new JLabel("Puzzle #" + String.valueOf(userID));
		puzzleNumber.setName("puzzleNumber");
		newPuzzleNumber = new JTextField();
		newPuzzleNumber.setName("newPuzzleNumber");
		newPuzzleButton = new JButton("New Puzzle");
		newPuzzleButton.setName("newPuzzleButton");

		// Initialize the second row with the two elements to do guesses
		typeGuess = new JLabel("Type guess here:");
		typeGuess.setName("typeGuess");
		guess = new JTextField();
		guess.setName("guess");

		// Initialize the last row with the guess table inside the scroll pane
		guessTable = new JTable(tableModel);
		guessTable.setName("guessTable");

		pane = new JScrollPane(guessTable);
		pane.setName("pane");

		// get rid of the tableHeader
		guessTable.setTableHeader(null);

		// no lines
		guessTable.setShowHorizontalLines(false);
		guessTable.setShowVerticalLines(false);

		// Default settings and layout setup
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container cp = this.getContentPane();

		layout = new GroupLayout(cp);
		cp.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// Create the horizontal settings for the window. These are three
		// components in parallel where the first is a sequential group of the
		// three puzzle components, the second is another sequential group of
		// the guess label and text fields, and the last is the table component.
		// Note that the lower limit dimensions are hardcoded to prevent the
		// user from shrinking the window so much that it starts to disfigure
		// the components. However, they can extend the window to expand the
		// size of the horizontal text fields.
		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup()
								.addComponent(puzzleNumber, 90, 90, 90)
								.addComponent(newPuzzleButton, 100, 100, 100)
								.addComponent(newPuzzleNumber, 120, 120,
										Short.MAX_VALUE))
				.addGroup(
						layout.createSequentialGroup().addComponent(typeGuess)
								.addComponent(guess, 220, 220, Short.MAX_VALUE))
				.addComponent(pane));

		// Create the vertical settings for the window. These are layed out
		// exactly as before except now we have a sequential group of two
		// parallel groups and a component. Again, for all but the table itself,
		// the heights are hardcoded.
		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup()
								.addComponent(puzzleNumber, 25, 25, 25)
								.addComponent(newPuzzleButton, 25, 25, 25)
								.addComponent(newPuzzleNumber, 25, 25, 25))
				.addGroup(
						layout.createParallelGroup()
								.addComponent(typeGuess, 25, 25, 25)
								.addComponent(guess, 25, 25, 25))
				.addComponent(pane, 200, 200, 500));

		this.pack();

		// fire the action listeners
		makeActions();
	}

	/**
	 * Function used when an action listener needs to create a new puzzle. If a
	 * correctly parsed puzzle number (integers grater than 0) is supplied in
	 * the newPuzzleNumber text field, the new puzzle will be initialized with
	 * this puzzle number. Otherwise, a random number between 1 and 10000 is
	 * picked.
	 * 
	 * The thread variables are also initialized to indicate the addition of a
	 * new game.
	 */
	private void enterNewPuzzle() {
		// retrieve the text from the puzzle number text field and create a new
		// puzzle instance, either random or with the value supplied
		String in = newPuzzleNumber.getText();

		// only integers greater than 0 should be allowed. leading 0's are
		// ignored: the string "0001" is equivalent to "1" in terms of puzzle
		// selected
		if (in.matches("[0]*[1-9][0-9]*")) {
			// remove the leading 0s
			String out = in.replaceFirst("[0]+", "");
			puzzle = new JottoModel(in);
			userID = out;
			System.out.println(out);
		} else {
			int randInt = (int) (Math.random() * 10000) + 1;
			userID = String.valueOf(randInt);
			puzzle = new JottoModel(userID);
		}
		// change the puzzle number label to display the correct puzzle number
		// and clear the text in the text field
		puzzleNumber.setText("Puzzle #" + userID);
		newPuzzleNumber.setText("");
		// erase the table entries
		tableModel.setRowCount(0);

		// reset the row counts and increment the game counter
		rowCounter.set(-1);
		gameCounter.incrementAndGet();
	}

	/**
	 * There are a total of three action listeners and those are on the
	 * newPuzzleButton to create a new puzzle, on newPuzzleNumber to also create
	 * a new puzzle, and on the guess text field to submit a guess String.
	 * 
	 * The guess action listener functions normally except that it runs the
	 * server GET request in a background thread to allow for the user to
	 * continue making guesses while the server works through the request.
	 */
	private void makeActions() {
		// both puzzle action listeners use the enterNewPuzzle() method and
		// react exactly the same. The only difference is that the button is
		// fired on click while the text field is fired on enter
		newPuzzleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterNewPuzzle();
			}
		});

		newPuzzleNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enterNewPuzzle();
			}
		});

		// setting the scroll pane to automatically scroll to the last row
		guessTable.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				guessTable.scrollRectToVisible(guessTable.getCellRect(
						guessTable.getRowCount() - 1, 0, true));
			}
		});

		// Multi-threaded guess text field response
		guess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// begin by incrementing the current row number
				rowCounter.incrementAndGet();

				// assign the current value of the guess text field to this
				// string
				final String textboxText = guess.getText();
				// clear the text field
				guess.setText("");

				// initialize a String array of size three to be used by the
				// tableModel to insert responses.
				final String[] response = new String[3];
				// the first column always has the text from the text field
				// regardless of the response.
				response[0] = textboxText;
				tableModel.insertRow(rowCounter.get(), response);

				// the user will now see his/her guess and will wait to fill in
				// the other two columns which is a task being processed by this
				// background thread
				Thread backgroundThread = new Thread(new Runnable() {
					public void run() {
						// right when we enter the thread, we get the counter
						// number. This way if the server takes so long to
						// respond that the user already started a new puzzle,
						// the thread will terminate with no further action
						// being taken.
						int myCounter = gameCounter.get();
						// to keep track of the row to assign the delayed value
						// to, we get the value of the row when the thread was
						// started.
						final int currentRow = rowCounter.get();

						// call on the Model to make the GET request
						String unparsedResponse = puzzle.makeGuess(textboxText);
						// if the response is in the errorMessages array, it
						// sets the second column to the error message below.
						if (errorMessages.contains(unparsedResponse)) {
							response[1] = "Invalid guess.";
						}
						// otherwise, we know we have two whitespaces so we
						// split on this and assign the second and third values
						// to the second and third columns.
						else {
							String[] parsedResponse = unparsedResponse
									.split(" ");
							response[1] = parsedResponse[1];
							response[2] = parsedResponse[2];
							// if the guess was correct, set column 1 to You
							// win! and column 2 to nothing.
							if (parsedResponse[2].equals("5")) {
								response[1] = "You win!";
								response[2] = "";
							}
						}
						// now only assign values if we are still in the same
						// game, otherwise do nothing
						if (gameCounter.get() == myCounter) {
							refreshInUIThread(response[1], currentRow, 1);
							refreshInUIThread(response[2], currentRow, 2);

						}
					}
				});
				backgroundThread.start();
			}
		});
	}

	/**
	 * Since we have separate Threads running background work, we need to
	 * thread-safely mutate the UI. This function allows us to do this. In
	 * particular, there are two mutating operations done on the GUI table which
	 * are inserting the values for columns 2 and 3 of the table.
	 * 
	 * @param response
	 *            the String representation of the value to insert into int
	 *            column
	 * @param currentRow
	 *            the row at which to insert the response's value
	 * @param column
	 *            the column number at which to insert the response's value
	 */
	private void refreshInUIThread(final String response, final int currentRow,
			final int column) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tableModel.setValueAt(response, currentRow, column);
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
				LobbyGUI main = new LobbyGUI();
				main.setVisible(true);
			}
		});
	}
}
