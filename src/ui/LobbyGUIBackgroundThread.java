package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import protocol.ClientSideResponseHandler;

/**
 * Use this class to send tasks to Swing. It must be used whenever mutating the
 * given JSwing object. It creates a new Thread object. Also uses the global
 * LOGGER to track serverResponses when needed.
 * 
 */
public class LobbyGUIBackgroundThread extends Thread {

	private final static Logger LOGGER = Logger
			.getLogger(LobbyGUIBackgroundThread.class.getName());

	private final LobbyGUI gui;
	private final BufferedReader in;

	/**
	 * Set the parameters using this Constructor.
	 * 
	 * @param gui
	 *            LobbyGUI instance to be modified
	 * @param in
	 *            server response
	 */
	public LobbyGUIBackgroundThread(LobbyGUI gui, BufferedReader in) {
		this.gui = gui;
		this.in = in;
	}

	@Override
	public void run() {
		String serverResponse;
		try {
			while ((serverResponse = in.readLine()) != null) {
				LOGGER.config(serverResponse);
				ClientSideResponseHandler.handleResponse(serverResponse,
						this.gui);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
