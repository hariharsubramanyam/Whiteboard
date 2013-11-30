package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class UserThread extends Thread{

    /**
     * The socket associated with this thread
     */
    private final Socket socket;
    
    /**
     * The input stream which this thread reads from
     */
    private final BufferedReader in;
    
    /**
     * The output stream which this thread writes to 
     */
    private final PrintWriter out;
    
    /**
     * The ID of the user
     */
    private final int userID;
    
    /**
     * The list of other user thread
     */
    private final List<UserThread> otherThreads;
    
    
    /**
     * Create the user thread
     * @param socket the socket associated with this thread
     * @param userID the id of the user 
     * @param otherThreads the list of other user threads
     * @throws IOException
     */
    public UserThread(Socket socket, int userID, List<UserThread> otherThreads) throws IOException{
        this.socket = socket;
        this.userID = userID;
        this.otherThreads = otherThreads;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }
    
    /**
     * Write a message to the output stream
     * @param message the message to write
     */
    public void output(String message){
        out.println(message);
    }
    
    /**
     * @return the id of this user
     */
    public int getUserID(){
        return this.userID;
    }
    
    /**
     * Output a message to all users except this one
     * @param message the message to output
     */
    public void broadcast(String message){
        for(UserThread thread : this.otherThreads){
            if(thread.getUserID() == this.userID)
                continue;
            thread.output(message);
        }
    }

    /**
     * Welcomes the user and handles all their input
     */
    @Override
    public void run() {
        try {
            this.output(String.format("welcome %d", this.userID));
            this.broadcast(String.format("User %d has joined!", this.userID));
            handleConnection();
            } catch (Exception e) {e.printStackTrace();} 
        finally {try {socket.close();} catch (IOException e) {e.printStackTrace();}}
    }

    /**
     * Reads from the input and handles the request
     * @throws IOException
     */
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
    
    /**
     * Processes the user request and generates a response 
     * @param line the user request
     * @return the response
     */
    private String handleRequest(String line){
        return String.format("%d", this.userID);
    }
}
