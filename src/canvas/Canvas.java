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
	private float lineStroke;

	private final float windowStroke;
	private final Color windowBackground;
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
	private final Color textColor;
	private Color lineColor;

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

		// set default values of components in the canvas
		windowStroke = 0; // no border on button window
		margins = 3; // a margin size of 3 applied evenly throughout
		windowW = width / 9; // always 1/9th the width of the entire canvas
		windowH = height;

		// hardcoded buttons for now
		buttonText = Arrays.asList("Erase", "Pencil", "s:Stroke Small",
				"s:Stroke Med", "s:Stroke Large");
		numOfButtons = buttonText.size();
		// leave 1 margin on either side
		buttonW = windowW - 2 * margins;
		// we use only half the height to leave space for colors
		buttonH = (windowH / 2) / numOfButtons - (2 * margins);
		buttonArc = 30; // in degrees

		// define boundaries of buttons
		buttonBoundaries = new HashMap<String, List<Integer>>();
		for (int i = 0; i < numOfButtons; ++i) {
			int xPos1 = margins;
			int yPos1 = margins + i * (buttonH + margins);
			int xPos2 = buttonW - margins;
			int yPos2 = (i + 1) * (buttonH - margins);
			buttonBoundaries.put(buttonText.get(i),
					Arrays.asList(xPos1, yPos1, xPos2, yPos2));
		}

		// colors
		buttonColor = new Color(0, 255, 127, 100); // spring green
		windowBackground = new Color(0, 128, 0); // green
		textColor = new Color(0); // black
		lineColor = Color.BLACK; // default to black

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
	 * Set strokeWidth
	 * 
	 * @param s
	 *            number of pixels used when drawing
	 */
	private Stroke setStrokeWidth(float s) {
		return new BasicStroke(s);
	}

	/**
	 * Set line color
	 * 
	 * @param c
	 *            color value
	 * @return a color
	 */
	private Color setLineColor(Color c) {
		return c;
	}

	/*
	 * Make the drawing buffer and draw some starting content for it.
	 */
	private void makeDrawingBuffer() {
		drawingBuffer = createImage(getWidth(), getHeight());
		fillWithWhite();
		createButtonLayout();

	}

	/*
	 * Make the drawing buffer entirely white.
	 */
	private void fillWithWhite() {
		final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// IMPORTANT! every time we draw on the internal drawing buffer, we
		// have to notify Swing to repaint this component on the screen.
		this.repaint();
	}

	/**
	 * Creates the button window. This window contains all functional buttons as
	 * well as the color charts
	 */
	private void createButtonLayout() {
		final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		// begin by making sure we have the right coloring
		g.setStroke(setStrokeWidth(windowStroke));
		g.setColor(windowBackground);
		g.fillRect(0, 0, windowW, windowH);

		// iterate through each of the buttons and apply all corresponding
		// properties
		for (int i = 0; i < numOfButtons; ++i) {

			String textToDisplay = buttonText.get(i);
			int yPos = buttonBoundaries.get(textToDisplay).get(1);

			g.setColor(buttonColor);
			Shape button = new RoundRectangle2D.Float(margins, yPos, buttonW,
					buttonH, buttonArc, buttonArc);
			g.fill(button);
			g.draw(button);
			g.setColor(textColor);

			// this is a custom font which is bold, the 1, and size 15
			Font font = new Font("ComicSans", 1, 15);
			g.setFont(font);

			// the positioning of the text in the buttons for two line text
			int xStringPos = buttonW / 4;
			if (textToDisplay.contains("s:")) {
				String modText = textToDisplay.replace("s:", "");
				String[] splitText = modText.split(" ");
				int yStringPos1 = yPos + buttonH / 2 - margins;
				int yStringPos2 = yPos + buttonH * 2 / 3 + margins;

				g.drawString(splitText[0], xStringPos, yStringPos1);
				g.drawString(splitText[1], xStringPos, yStringPos2);
			}

			// for single line text
			else {
				int yStringPos = yPos + buttonH / 2 + 2 * margins;
				g.drawString(buttonText.get(i), xStringPos, yStringPos);
			}

		}

	}

	/*
	 * Draw a line between two points (x1, y1) and (x2, y2), specified in pixels
	 * relative to the upper-left corner of the drawing buffer.
	 */
	private void drawLineSegment(int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

		g.setStroke(setStrokeWidth(this.lineStroke));
		g.setColor(setLineColor(this.lineColor));
		g.drawLine(x1, y1, x2, y2);

		// IMPORTANT! every time we draw on the internal drawing buffer, we
		// have to notify Swing to repaint this component on the screen.
		this.repaint();
	}

	/**
	 * Since our button layout is part of the canvas, we have to make sure we
	 * don't draw over it.
	 * 
	 * @param x
	 *            an int representing the potential x position to draw on
	 * @return a new, adjusted value of x
	 */
	private int adjustedX(int x) {
		int newX = x;
		if (x < windowW + lineStroke / 2.0) {
			newX = (int) (windowW + (lineStroke / 2.0));
		}
		return newX;
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
		private int lastX, lastY;

		/*
		 * When mouse button is pressed down, start drawing.
		 */
		public void mousePressed(MouseEvent e) {
			lastX = adjustedX(e.getX());
			lastY = e.getY();
		}

		/*
		 * When mouse moves while a button is pressed down, draw a line segment.
		 */
		public void mouseDragged(MouseEvent e) {
			int x = adjustedX(e.getX());
			int y = e.getY();

			drawLineSegment(lastX, lastY, x, y);
			lastX = adjustedX(x);
			lastY = y;
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

			if (action.equals("Erase")) {
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
