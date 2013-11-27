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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Canvas represents a drawing surface that allows the user to draw on it
 * freehand, with the mouse.
 */
public class Canvas extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// image where the user's drawing is stored
	private Image drawingBuffer;
	private Graphics tempBuffer; // TODO: use to create temporary objects
	private float lineStroke;

	// whiteboard
	private final int canvasW;
	private final int canvasH;

	private final int drawableCanvasW;
	private final int drawableCanvasH;

	// button layout
	private final float windowStroke;
	private final int margins;
	private final int windowW;
	private final int windowH;

	final List<String> buttonText;
	final HashMap<String, List<Integer>> buttonBoundaries;
	private final int numOfButtons;

	private final int buttonW;
	private final int buttonH;
	private final float buttonArc;

	private final Color buttonColor;
	private Color textColor;
	private Color lineColor;
	private final Color windowBackground;
	private Color boardColor;

	/**
	 * Make a canvas.
	 * 
	 * @param width
	 *            width in pixels
	 * @param height
	 *            height in pixels
	 */
	public Canvas(int width, int height) {
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
		this.windowW = 255; // always 255 to accomodate for the color palate
		this.windowH = height;

		// set the size of the canvas
		this.drawableCanvasW = width - 2 * margins - windowW;
		this.drawableCanvasH = height - 2 * margins;

		// hardcoded buttons for now
		this.buttonText = Arrays.asList("Eraser", "Pencil", "s:Stroke Small",
				"s:Stroke Med", "s:Stroke Large", "s:Clear board", "random");
		this.numOfButtons = buttonText.size();
		// leave 1 margin on either side
		this.buttonW = windowW - 2 * margins;
		// we use only half the height to leave space for colors
		this.buttonH = (int) ((windowH / 2.0) / numOfButtons);
		this.buttonArc = 30; // in degrees

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

		// colors
		this.buttonColor = new Color(0, 255, 127, 100); // spring green
		this.windowBackground = new Color(0, 128, 0, 255); // green
		this.textColor = new Color(0); // black
		this.lineColor = Color.BLACK; // default to black
		this.boardColor = Color.WHITE; // default to white

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

	/*
	 * Make the drawing buffer and draw some starting content for it.
	 */
	private void makeDrawingBuffer() {
		drawingBuffer = createImage(getWidth(), getHeight());
		tempBuffer = drawingBuffer.getGraphics();
		fillWithWhite();
		createButtonLayout();
	}

	/*
	 * Make the drawing buffer entirely white.
	 */
	private void fillWithWhite() {
		final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		int amountOfGrey = 130; // between 0 and 255. The closer to 0 the more
								// black.
		int alpha = 180; // creates transparent looking colors
		g.setColor(setColor(amountOfGrey, amountOfGrey, amountOfGrey, alpha));
		g.fillRect(0, 0, canvasW, canvasH);
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
	 *            size of text
	 */
	private void createText(Graphics2D g, String text, int x, int y,
			Color textColor, int option, int size) {
		g.setColor(textColor);
		Font font = new Font("ComicSans", option, size);
		g.setFont(font);
		g.drawString(text, x, y);
	}

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
	 * Creates the button window. This window contains all functional buttons as
	 * well as the color charts
	 */
	private void createButtonLayout() {
		final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		// begin by creating the window layout background
		createFilledRectangle(g, windowStroke, windowBackground, 0, 0, windowW,
				windowH);

		// iterate through each of the buttons and apply all corresponding
		// properties
		for (int i = 0; i < numOfButtons; ++i) {

			String textToDisplay = buttonText.get(i);
			int yPos1 = buttonBoundaries.get(textToDisplay).get(1);
			int adjustedButtonH = buttonH - 2 * margins;

			createRoundedFilledRectangle(g, buttonColor, margins, yPos1,
					buttonW - margins, adjustedButtonH, buttonArc, buttonArc,
					true);

			// the positioning of the text in the buttons for two line text
			int xStringPos = 5 * margins;
			if (textToDisplay.contains("s:")) {
				String modText = textToDisplay.replace("s:", "");
				String[] splitText = modText.split(" ");
				int yStringPos1 = yPos1 + buttonH / 2 - margins;
				int yStringPos2 = yPos1 + buttonH * 2 / 3 + margins;

				// this is a custom font which is bold, the 1, and size 15
				createText(g, splitText[0], xStringPos, yStringPos1, textColor,
						1, 15);
				createText(g, splitText[1], xStringPos, yStringPos2, textColor,
						1, 15);
			}

			// for single line text
			else {
				int yStringPos = yPos1 + buttonH / 2 + 2 * margins;
				createText(g, buttonText.get(i), xStringPos, yStringPos,
						textColor, 1, 15);

			}

		}

		// now create the color palate which is composed of the saturation, hue
		// square, and lum bar
		int lumBarW = 9;
		int lumBarH = 240;
		int lumBarX = windowW - lumBarW - margins;
		int lumBarY = windowH / 2 + 2 * margins;

		int palateW = 240;
		int palateH = 240;
		int palateX = margins;
		int palateY = windowH / 2 + 2 * margins;

		for (int lum = 0; lum <= 240; lum++) {
			createFilledRectangle(g, 1, setColor(5,50,50,lum), lumBarX, lumBarY, lumBarW,
					lumBarH);
			
		}

		for (int hue = 0; hue < 239; hue++) {
			for (int sat = 0; sat <= 240; sat++) {

			}
		}

	}

	/*
	 * Draw a line between two points (x1, y1) and (x2, y2), specified in pixels
	 * relative to the upper-left corner of the drawing buffer.
	 */
	private void drawLineSegment(int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		g.setStroke(new BasicStroke(this.lineStroke, 1, 1));
		g.setColor(this.lineColor);
		g.drawLine(x1, y1, x2, y2);

		// IMPORTANT! every time we draw on the internal drawing buffer, we
		// have to notify Swing to repaint this component on the screen.
		this.repaint();
	}

	private void changeBackground() {

		this.lineStroke = 1.0f;
		this.lineColor = Color.blue;
		for (int i = windowW + margins; i < canvasW - margins; i++) {
			for (int j = margins; j < canvasH - margins; j++) {
				drawLineSegment(i, j, i, j);
			}
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
	 * create a color out of any of the Color constructors
	 * 
	 * @param c
	 *            color value
	 * @return a color
	 */
	private Color setColor(int r, int g, int b, int alpha) {
		return new Color(r, g, b, alpha);
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

			drawLineSegment(lastPos[0], lastPos[1], x, y);
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
			for (String button : buttonText) {
				List<Integer> boundaries = buttonBoundaries.get(button);
				if (x >= boundaries.get(0) && x <= boundaries.get(2)
						&& y >= boundaries.get(1) && y <= boundaries.get(3)) {
					action = button;

				}
			}

			if (action.equals("Eraser")) {
				lineStroke = 25;
				lineColor = Color.WHITE;
			}

			if (action.equals("Pencil")) {
				lineStroke = 1;
				lineColor = Color.BLACK;
			}

			if (action.equals("s:Stroke Small")) {
				lineStroke = 1;
			}

			if (action.equals("s:Stroke Med")) {
				lineStroke = 5;
			}

			if (action.equals("s:Stroke Large")) {
				lineStroke = 10;
			}

			if (action.equals("s:Clear board")) {
				fillWithWhite();
			}

			if (action.equals("random")) {
				changeBackground();
			}

		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	/*
	 * Main program. Make a window containing a Canvas.
	 */
	public static void main(String[] args) {
		// set up the UI (on the event-handling thread)
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame window = new JFrame("Freehand Canvas");
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.setLayout(new BorderLayout());
				Canvas canvas = new Canvas(800, 600);
				window.add(canvas, BorderLayout.CENTER);
				window.pack();
				window.setVisible(true);
			}
		});
	}
}
