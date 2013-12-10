package logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class BoardLogger {
	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;

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
