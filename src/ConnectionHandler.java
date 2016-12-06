import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable{
	private TCPServer.OnMessageReceived messageListener;
	private BufferedReader in;
	private Socket client;
	
	public ConnectionHandler (TCPServer.OnMessageReceived list, Socket socket){
		messageListener = list;
		client = socket;
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("S : Error during initialisation of the client input stream");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		 try {

             //in this while we wait to receive messages from client (it's an infinite loop)
             //this while it's like a listener for messages
             while (true) {
                 String message = in.readLine();

                 if (message != null && messageListener != null) {
                     //call the method messageReceived from ServerBoard class
                     messageListener.messageReceived(message);
                 }
             }

         } catch (Exception e) {
             System.out.println("S: Error");
             e.printStackTrace();
         } finally {
             try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             System.out.println("S: Done.");
         }
	}
	
	

}
