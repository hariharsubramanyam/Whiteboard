package logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Creates a Logger used for systematically enabling/disabling debug lines.
 */
public class BoardLogger {
	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;

	/**
	 * Attaches handlers to the LOGGER instance. Will also create the fileTxt
	 * file and sets the level to ALL by default.
	 * 
	 * @throws IOException
	 */
	static public void setup() throws IOException {

		// Get the global logger to configure it
		Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		fileTxt = new FileHandler("Logging.txt");
		fileTxt.setLevel(Level.ALL);
		// create txt Formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		LOGGER.addHandler(fileTxt);

		// create the conosole handler
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		LOGGER.addHandler(handler);

	}
}
