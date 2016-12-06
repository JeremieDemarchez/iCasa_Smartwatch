import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The class extends the Thread class so we can receive and send messages at the same time
 */
public class TCPServer extends Thread {

    public static final int SERVERPORT = 4444;
    private boolean running = false;
    private List<PrintWriter> listPrintWriter;
    private OnMessageReceived messageListener;

    public static void main(String[] args) {

        //opens the window where the messages will be received and sent
        ServerBoard frame = new ServerBoard();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * Constructor of the class
     * @param messageListener listens for the messages
     */
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Method to send the messages from server to client
     * @param message the message sent by the server
     */
    public void sendMessage(String message){
    	List<PrintWriter> toSuppr = new ArrayList<PrintWriter>();
    	for(PrintWriter mOut : listPrintWriter){
    		try{
    			if (mOut != null && !mOut.checkError()) {
    				mOut.println(message);
    				mOut.flush();
    			}
    			else{
    				toSuppr.add(mOut);
    			}
    		}catch(Exception e){
    			toSuppr.add(mOut);
    			System.err.println("Client disconnected, connection closed for this client.");
    			e.printStackTrace();
    		}
    	}
    	for(PrintWriter p : toSuppr){
    		listPrintWriter.remove(p);
    	}
    }

    @Override
    public void run() {
        super.run();
        listPrintWriter = new ArrayList<PrintWriter>();

        running = true;

        try {
            System.out.println("S: Connecting...");

     	   //create a server socket. A server socket waits for requests to come in over the network.
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);
            
           while(running){
               //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
               Socket client = serverSocket.accept();
               
               System.out.println("S: Receiving...");
               
               //sends the message to the client
               PrintWriter mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
               listPrintWriter.add(mOut);
               
               new Thread(new ConnectionHandler(this.messageListener, client)).start();
               
           }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the ServerBoard
    //class at on startServer button click
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}