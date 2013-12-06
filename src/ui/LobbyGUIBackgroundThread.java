package ui;

import java.io.BufferedReader;
import java.io.IOException;

import protocol.ClientSideResponseHandler;

public class LobbyGUIBackgroundThread extends Thread{

    private final LobbyGUI gui;
    private final BufferedReader in;
    public LobbyGUIBackgroundThread(LobbyGUI gui, BufferedReader in){
        this.gui = gui;
        this.in = in;
    }
    
    @Override
    public void run() {
        String serverResponse;
        try {
            while ((serverResponse = in.readLine()) != null) {
                ClientSideResponseHandler.handleResponse(serverResponse,this.gui);
            }
        } catch (IOException e) {e.printStackTrace();}
    }

}
