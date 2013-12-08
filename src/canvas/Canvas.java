package canvas;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import protocol.Client;
import protocol.ClientSideMessageMaker;
import protocol.MessageHandler;
import ui.LobbyGUI;
import adts.Line;

/**
 * Canvas represents a drawing surface that allows the user to draw on it
 * freehand, with the mouse.
 */
public class Canvas extends JPanel implements Client {
	/**
	 * This is the GUI acting as the View for a Whiteboard. It is drawn in such
	 * a way that almost every component is dependent on the Constructor
	 * parameters.
	 */
	private static final long serialVersionUID = 1L;

	// image where the user's drawing is stored
	private Image drawingBuffer;

	/*
	 * Dimensions defined by their component name followed by X, x-coordinate,
	 * Y, y-coordinate, W, width, and H, height
	 */

	/**
	 * The dimension for margins applied where needed. Defaults to 3.
	 */
	private final int margins;

	/**
	 * Width of entire Canvas.
	 */
	private final int canvasW;
	/**
	 * Height of entire Canvas.
	 */
	private final int canvasH;

	/**
	 * Width of drawable part of the Canvas.
	 */
	private final int drawableCanvasW;
	/**
	 * Height of drawable part of the Canvas.
	 */
	private final int drawableCanvasH;

	/**
	 * Height of the button window layout
	 */
	private final int windowW;
	/**
	 * Width of the button window layout
	 */
	private final int windowH;

	/**
	 * Width of each individual button. Set to width of button window layout
	 * minus 2 margins.
	 */
	private final int buttonW;
	/**
	 * Height of each individual button. Set to the height of the Canvas.
	 */
	private final int buttonH;
	/**
	 * In degrees, the radius of the square corners
	 */
	private final float buttonArc;
	/**
	 * Y position of the current color button square
	 */
	private int currentColorSquareY;
	/**
	 * Size of the squares in the color palate
	 */
	private int sizeColorSquare;
	/**
	 * List with String representation of the text to display for each button.
	 */
	final List<String> buttonText;
	/**
	 * A Map from button text (used as the identifier) to the x,y coordinates of
	 * the button.
	 */
	final HashMap<String, List<Integer>> buttonBoundaries;
	/**
	 * A Map from Color button (used as the identifier) to the x,y coordinates
	 * of the button.
	 */
	final HashMap<Color, List<Integer>> colorButtonBoundaries;
	/**
	 * Total number of buttons. Used to determine the relative height of each
	 * button.
	 */
	private final int numOfButtons;

	/**
	 * The instance of the JFram we are constructing
	 */
	private final JFrame window;

	/**
	 * Width of shape drawing. It is the number of pixels any given line will
	 * draw above/below. It is always odd so as to allow for equal number of
	 * pixels above and below.
	 */
	private float lineStroke;
	private final float windowStroke;

	/**
	 * The active list of users connected to the board. It gets wiped and
	 * redrawn every time the controller provides new inputs
	 */
	private List<String> userNames;

	// Color properties of different components in the board
	private final Color buttonColor;
	private Color textColor;
	private Color lineColor;
	private final Color windowBackground;
	private Color boardColor;
	private final List<Color> basicColors;

	private final LobbyGUI lobby;
	
	private String user;

	/**
	 * Make a canvas.
	 * 
	 * @param width
	 *            width in pixels
	 * @param height
	 *            height in pixels
	 * @param lobby
	 *            the instance of the LobbyGUI Controller which handles the data
	 *            traffic
	 * @param user
	 *            every Canvas must be instantiated with a user String which is
	 *            the userName of the user who started it
	 */
	public Canvas(int width, int height, LobbyGUI lobby, String user) {
	    this.userNames = new ArrayList<String>();
		this.lobby = lobby;
		this.user = user;

		window = new JFrame("Freehand Canvas");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new BorderLayout());

		this.setPreferredSize(new Dimension(width, height));
		this.lineStroke = 1; // default to 1 pixel

		addDrawingController();
		// note: we can't call makeDrawingBuffer here, because it only
		// works *after* this canvas has been added to a window. Have to
		// wait until paintComponent() is first called.

		// set the size of the canvas
		this.canvasW = width;
		this.canvasH = height;

		// set default values of components in the canvas
		this.windowStroke = 0; // no border on button window
		this.margins = 3; // a margin size of 3 applied evenly throughout
		this.windowW = width / 5; // always 1/5th of the width
		this.windowH = height;

		// set the size of the canvas
		this.drawableCanvasW = width - 2 * margins - windowW;
		this.drawableCanvasH = height - 2 * margins;

		/*
		 * Button properties: text, boundaries, color, margins
		 */

		this.buttonText = Arrays.asList("Eraser", "Pencil", "Stroke Small",
				"Stroke Med", "Stroke Large", "Clear board", "LEAVE BOARD");
		this.numOfButtons = buttonText.size();
		// leave 1 margin on either side
		this.buttonW = windowW - 2 * margins;
		// we use only a third the height to leave space for colors
		this.buttonH = (int) ((windowH / 3.0) / numOfButtons);
		this.buttonArc = 30;
		// for the color palate
		this.sizeColorSquare = (int) ((windowW - 2 * margins) / 4f);
		
		// define boundaries of buttons
		this.buttonBoundaries = new HashMap<String, List<Integer>>();
		for (int i = 0; i < numOfButtons; ++i) {
			int xPos1 = margins;
			int yPos1 = margins + i * buttonH;
			int xPos2 = buttonW - margins;
			int yPos2 = (i + 1) * buttonH - margins;

			buttonBoundaries.put(buttonText.get(i),
					Arrays.asList(xPos1, yPos1, xPos2, yPos2));
		}

		this.colorButtonBoundaries = new HashMap<Color, List<Integer>>();

		// initialize colors
		this.buttonColor = new Color(100, 100, 100, 100); // light black
		this.windowBackground = new Color(141, 233, 181, 255); // light green
		this.textColor = new Color(0); // black
		this.lineColor = Color.BLACK; // default to black
		this.boardColor = Color.WHITE; // default to white
		this.basicColors = Arrays.asList(Color.BLACK, Color.BLUE, Color.CYAN,
				Color.DARK_GRAY, Color.GRAY, Color.GREEN, Color.MAGENTA,
				Color.ORANGE, Color.PINK, Color.RED, Color.WHITE, Color.YELLOW);

		window.add(this, BorderLayout.CENTER);
		window.pack();
		// Initialize the user list
		window.setVisible(true);
		
		
	}

	/**
	 * Controller can use this function to add users to the board.
	 * 
	 * @param users
	 *            a String carray omposed of every username
	 */
	public void createUserList(List<String> users) {
		userNames = new ArrayList<String>();
		for (String user : users) {
			userNames.add(user);
		}
		createUserTable();
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		// If this is the first time paintComponent() is being called,
		// make our drawing buffer.
		if (drawingBuffer == null) {
			makeDrawingBuffer();
		}

		// Copy the drawing buffer to the screen.
		g.drawImage(drawingBuffer, 0, 0, null);
	}

	/**
	 * Make the drawing buffer and draw some starting content for it. It draws
	 * all necessary starting components such as backgrounds and buttons
	 */
	private void makeDrawingBuffer() {
		drawingBuffer = createImage(getWidth(), getHeight());
		fillWithWhite();
		createButtonLayout();
		List<String> oneUser = new ArrayList<String>();
		oneUser.add(this.user);
	    createUserList(oneUser);
	}

	/**
	 * Make the drawing buffer's background. This includes a GRAY back rectangle
	 * and the white "drawable canvas" on top.
	 */
	public void fillWithWhite() {
		final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		/*
		 * Create a gray layer to separate the button window, in green, from the
		 * drawable area, in white.
		 */
		Color grayBackground = Color.GRAY;
		g.setColor(grayBackground);
		g.fillRect(0, 0, canvasW, canvasH);

		/*
		 * Create the white drawable area and the button window
		 */
		g.setColor(boardColor);
		g.fillRect(margins + windowW, margins, drawableCanvasW, drawableCanvasH);
		createButtonLayout();

		// IMPORTANT! every time we draw on the internal drawing buffer, we
		// have to notify Swing to repaint this component on the screen.
		this.repaint();
	}

	/**
	 * Creates a rectangle with a given stroke width, color, x and y positions
	 * as referenced from the top left corner, and width and height
	 * 
	 * @param g
	 *            Graphics2D object to modify
	 * @param stroke
	 *            a float representing the number of pixels to give the edge of
	 *            the rectangle. Must be odd and greater than 0.
	 * @param color
	 *            a Color object to match the fill and stroke color
	 * @param x
	 *            x-position with increasing coordinate left to right, starting
	 *            at left wall
	 * @param y
	 *            y-position with increasing coordinate top to bottom, starting
	 *            at top wall
	 * @param width
	 *            width of rectangle
	 * @param height
	 *            height of rectangle
	 */
	private void createFilledRectangle(Graphics2D g, float stroke, Color color,
			int x, int y, int width, int height) {
		g.setStroke(setStrokeWidth(stroke));
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

	/**
	 * Prints text to the graphics provided
	 * 
	 * @param g
	 *            Graphics2D object to modify
	 * @param text
	 *            String to print
	 * @param x
	 *            x-position of the left-aligned text
	 * @param y
	 *            y-position of the top-aligned text
	 * @param textColor
	 *            Color given to the text
	 * @param option
	 *            int that specifies "normal", 0, "bold", 1, "italic", 2
	 * @param size
	 *            text size
	 */
	private void createText(Graphics2D g, String text, int x, int y,
			Color textColor, int option, int size) {
		g.setColor(textColor);
		Font font = new Font("Verdana", option, size);
		g.setFont(font);
		g.drawString(text, x, y);
	}

	/**
	 * Used to draw a rounded rectangle to a provided Graphics2D object. Can be
	 * filled or not.
	 * 
	 * @param g
	 *            Graphics2D object to modify
	 * 
	 * @param color
	 *            a Color object to match the fill and stroke color
	 * @param x
	 *            x-position with increasing coordinate left to right, starting
	 *            at left wall
	 * @param y
	 *            y-position with increasing coordinate top to bottom, starting
	 *            at top wall
	 * @param width
	 *            width of rectangle
	 * @param height
	 *            height of rectangle
	 * @param xArc
	 *            value in degrees of the radius of the corners
	 * @param yArc
	 *            value in degrees of the radius of the corners
	 * @param fill
	 *            boolean which is true if the rectangle is to be filled. False
	 *            otherwise.
	 */
	private void createRoundedFilledRectangle(Graphics2D g, Color color, int x,
			int y, int width, int height, float xArc, float yArc, boolean fill) {
		g.setColor(color);
		Shape button = new RoundRectangle2D.Float(x, y, width, height, xArc,
				yArc);
		if (fill) {
			g.fill(button);
		}
		g.draw(button);
	}

	/**
	 * Draws the button layout window as well as the buttons and their text.
	 * 
	 * @param g
	 *            the Graphics2D object to work with
	 */
	private void createButtonsAndText(Graphics2D g) {
		for (int i = 0; i < numOfButtons; ++i) {

			String textToDisplay = buttonText.get(i);
			int yPos1 = buttonBoundaries.get(textToDisplay).get(1);
			int adjustedButtonH = buttonH - 2 * margins;

			createRoundedFilledRectangle(g, buttonColor, margins, yPos1,
					buttonW - margins, adjustedButtonH, buttonArc, buttonArc,
					true);

			// the x-position of the text's left starting position
			int xStringPos = 5 * margins;
			int yStringPos = yPos1 + buttonH / 2 + margins;

			createText(g, buttonText.get(i), xStringPos, yStringPos, textColor,
					1, 15);

		}
	}

	/**
	 * Draws the color palate and the "current color" square.
	 * 
	 * @param g
	 *            the Graphics2D object to work with
	 */
	private void createColorPalate(Graphics2D g) {
		int sizeColorSquare = (int) ((windowW - 2 * margins) / 4f);
		int beginPosY = windowH / 3 + margins;

		/*
		 * After drawing them, append their boundaries to link an action to them
		 */
		Iterator<Color> useColor = basicColors.iterator();
		for (int i = 0; i < 4; ++i) {
			int xPos = margins + i * sizeColorSquare;
			for (int j = 0; j < 3; ++j) {
				Color colorSquare = useColor.next();
				int yPos = beginPosY + j * sizeColorSquare;
				createFilledRectangle(g, 1, colorSquare, xPos, yPos,
						sizeColorSquare, sizeColorSquare);
				this.colorButtonBoundaries.put(
						colorSquare,
						Arrays.asList(xPos, yPos, xPos + sizeColorSquare, yPos
								+ sizeColorSquare));
			}
		}
	}

	/**
	 * Create a small square representing the current color selected. The
	 * y-position is the beginning position of the color squares plus the number
	 * of squares high, in this case 3, plus a margins.
	 * 
	 * @param g
	 *            the Graphics2D object to work with
	 */
	private void createCurrentColorSquare(Graphics2D g) {
		int yStringPos = windowH / 3 + margins + 3 * this.sizeColorSquare
				+ this.sizeColorSquare / 2;
		int xStringPos = 3 * margins;
		createText(g, "Current color: ", xStringPos, yStringPos, textColor, 1,
				15);

		this.currentColorSquareY = yStringPos - this.sizeColorSquare * 2 / 3;
		int ySquarePos = this.currentColorSquareY + this.sizeColorSquare / 3;
		int xSquarePos = windowW - margins - sizeColorSquare / 4 * 3;

		createFilledRectangle(g, 1, lineColor, xSquarePos, ySquarePos,
				sizeColorSquare / 2, sizeColorSquare / 2);
	}

	/**
	 * The Canvas has a table with all the users connected to it. This
	 * information is handled by the function which the controller calls on to
	 * add users.
	 * 
	 */
	private void createUserTable() {
	    final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		int yPos = this.currentColorSquareY + windowW / 4;
		int xPos = margins;
		yPos = yPos + margins;
		int tableWidth = windowW - 2 * margins;
		int tableHeight = windowH - yPos - margins;

		g.setColor(Color.CYAN);
		// the background of the table
		g.fillRect(xPos, yPos, tableWidth, tableHeight);

		g.setColor(Color.BLACK);
		g.setStroke(setStrokeWidth(2));
		g.drawRect(xPos, yPos, tableWidth, tableHeight);

		// the user header
		g.drawRect(xPos, yPos, tableWidth, tableHeight / 10);
		int xStringPos = 3 * margins;
		int yStringPos = yPos + tableHeight / 15;
		createText(g, "Active Users", xStringPos, yStringPos, textColor, 1, 15);

		// insert the active users supplied by the controller
		int startingY = yPos + tableHeight / 15 + 3 * margins;
		int heightOfString = tableHeight / 15;
		for (int i = 0; i < userNames.size(); i++) {
			String tableEntry = String.valueOf(i + 1) + ". " + userNames.get(i);
			createText(g, tableEntry, xStringPos, startingY + heightOfString
					* (i + 1), textColor, 1, 13);
		}
		this.repaint();
	}

	/**
	 * Creates the button window. This window contains all functional buttons as
	 * well as the color charts
	 */
	private void createButtonLayout() {
		final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		// window layout background
		createFilledRectangle(g, windowStroke, windowBackground, 0, 0, windowW,
				windowH);

		createButtonsAndText(g);
		createColorPalate(g);
		createCurrentColorSquare(g);
		createUserTable();
	}

	/*
	 * Draw a line between two points (x1, y1) and (x2, y2), specified in pixels
	 * relative to the upper-left corner of the drawing buffer.
	 */
	public synchronized void drawLineSegment(Line l, boolean withRepaint) {
		Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		g.setStroke(new BasicStroke(l.getStrokeThickness(), 1, 1));
		g.setColor(new Color(l.getR(), l.getG(), l.getB(), l.getA()));

		g.drawLine(l.getX1(), l.getY1(), l.getX2(), l.getY2());
		if (withRepaint) {
			this.repaint();
		}
	}

	/**
	 * Set strokeWidth
	 * 
	 * @param s
	 *            number of pixels used when drawing
	 */
	private Stroke setStrokeWidth(float s) {
		return new BasicStroke(s);
	}

	/**
	 * Since our button layout is part of the canvas, we have to make sure we
	 * don't draw over it.
	 * 
	 * @param x
	 *            an int representing the potential x position to draw on
	 * @return a new, adjusted value of x
	 */
	private int[] adjustedPos(int x, int y) {
		int newX = x;
		int newY = y;
		if (x < windowW + lineStroke / 2.0 + margins) {
			newX = (int) (windowW + (lineStroke / 2.0) + margins);
		} else if (x > canvasW - lineStroke / 2.0 - margins) {
			newX = (int) (canvasW - margins - lineStroke / 2.0);
		}

		if (y < lineStroke / 2.0 + margins) {
			newY = (int) (lineStroke / 2.0 + margins);
		} else if (y > canvasH - lineStroke / 2.0 - margins) {
			newY = (int) (canvasH - margins - lineStroke / 2.0);
		}
		int[] result = new int[2];
		result[0] = newX;
		result[1] = newY;
		return result;

	}

	/*
	 * Add the mouse listener that supports the user's freehand drawing.
	 */
	private void addDrawingController() {
		DrawingController controller = new DrawingController();
		addMouseListener(controller);
		addMouseMotionListener(controller);
	}

	/*
	 * DrawingController handles the user's freehand drawing.
	 */
	private class DrawingController implements MouseListener,
			MouseMotionListener {
		// store the coordinates of the last mouse event, so we can
		// draw a line segment from that last point to the point of the next
		// mouse event.
		private int[] lastPos = new int[2];

		/*
		 * When mouse button is pressed down, start drawing.
		 */
		public void mousePressed(MouseEvent e) {

			lastPos = adjustedPos(e.getX(), e.getY());

		}

		/*
		 * When mouse moves while a button is pressed down, draw a line segment.
		 */
		public void mouseDragged(MouseEvent e) {

			int[] pos = adjustedPos(e.getX(), e.getY());
			int x = pos[0];
			int y = pos[1];

			Line l = new Line(lastPos[0], lastPos[1], x, y, lineStroke,
					lineColor.getRed(), lineColor.getGreen(),
					lineColor.getBlue(), lineColor.getAlpha());
			lobby.makeRequest(ClientSideMessageMaker.makeRequestStringDraw(l));
			lastPos = adjustedPos(x, y);
		}

		public void mouseMoved(MouseEvent e) {
		}

		/*
		 * This is used for button selection. It gives buttons actions.
		 */
		public void mouseClicked(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();

			// check where the click happened and if within a button's
			// boundaries, act accordingly
			String action = "";
			for (String button : buttonBoundaries.keySet()) {
				List<Integer> boundaries = buttonBoundaries.get(button);
				if (x >= boundaries.get(0) && x <= boundaries.get(2)
						&& y >= boundaries.get(1) && y <= boundaries.get(3)) {
					action = button;
				}
			}

			Color colorAction = null;
			for (Color color : colorButtonBoundaries.keySet()) {
				List<Integer> boundaries = colorButtonBoundaries.get(color);
				if (x >= boundaries.get(0) && x <= boundaries.get(2)
						&& y >= boundaries.get(1) && y <= boundaries.get(3)) {
					colorAction = color;
				}
			}

			final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

			if (action.equals("Eraser")) {
				lineStroke = 25;
				lineColor = Color.WHITE;
				createCurrentColorSquare(g);
				repaint();
			}

			if (action.equals("Pencil")) {
				lineStroke = 1;
				if (lineColor.equals(Color.WHITE)) {
					lineColor = Color.BLACK;
				}
				createCurrentColorSquare(g);
				repaint();

			}

			if (action.equals("Stroke Small")) {
				lineStroke = 1;
			}

			if (action.equals("Stroke Med")) {
				lineStroke = 5;
			}

			if (action.equals("Stroke Large")) {
				lineStroke = 11;
			}

			if (action.equals("Clear board")) {
				lobby.makeRequest(ClientSideMessageMaker
						.makeRequestStringClear());
			}

			if (action.equals("LEAVE BOARD")) {
				window.dispose();
				lobby.setVisible(true);
			}

			if (colorAction != null) {
				lineColor = colorAction;
				createCurrentColorSquare(g);
				repaint();

			}

		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	@Override
	public void onReceiveUsernameChanged(String rcvdName) {
		return;
	}

	@Override
	public void onReceiveBoardIDs(List<Integer> rcvdIDs) {
		return;
	}

	@Override
	public void onReceiveWelcome(int id) {
		return;
	}

	@Override
	public void onReceiveDraw(Line l) {
		final Line line = l;
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				drawLineSegment(line, true);
			}
		});

	}

	@Override
	public void onReceiveBoardLines(List<Line> ls) {
		final List<Line> lines = ls;
		SwingUtilities.invokeLater(new Thread() {
			@Override
			public void run() {
				for (Line line : lines) {
					drawLineSegment(line, false);
				}
				repaint();
			}
		});

	}

    @Override
    public void onReceiveClear() {
        this.fillWithWhite();
    }

    @Override
    public void onReceiveUsers(List<String> users) {
        this.createUserList(users);
    }

}
