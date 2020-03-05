package threadedSer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerThread extends Thread {
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;

	public ServerThread(BufferedReader inFromClient, DataOutputStream outToClient) {
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
	}

	public void run() {
		String clientSentence = null;
		String capitalizedSentence;
		
		try {
			outToClient.writeBytes("hi from server!");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while (true) {
			try {
				clientSentence = inFromClient.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("from client " + clientSentence);
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			try {
				outToClient.writeBytes(capitalizedSentence);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
