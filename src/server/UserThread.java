package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class UserThread extends Thread{

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final int userID;
    private final List<UserThread> otherThreads;
    
    
    public UserThread(Socket socket, int userID, List<UserThread> otherThreads) throws IOException{
        this.socket = socket;
        this.userID = userID;
        this.otherThreads = otherThreads;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }
    
    public void output(String message){
        out.println(message);
    }
    
    public int getUserID(){
        return this.userID;
    }
    
    public void broadcast(String message){
        for(UserThread thread : this.otherThreads){
            if(thread.getUserID() == this.userID)
                continue;
            thread.output(message);
        }
    }

    @Override
    public void run() {
        try {
            out.println(String.format("Welcome, user %d", this.userID));
            this.broadcast(String.format("User %d has joined!", this.userID));
            handleConnection();
            } catch (Exception e) {e.printStackTrace();} 
        finally {try {socket.close();} catch (IOException e) {e.printStackTrace();}}
    }

    private void handleConnection() throws IOException {
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String output = handleRequest(line);
                if (output != null) {
                    out.println(output);
                    if (output.equals("Leave whiteboard")) {
                        break;
                    }
                }
            }
        }
        finally {
            out.close();
            in.close();
        }
    }
    
    private String handleRequest(String line){
        return String.format("%d", this.userID);
    }
}
