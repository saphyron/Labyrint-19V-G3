package game2020;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MsgFromServer extends Thread{
	private Socket clientSocket;
	
	public MsgFromServer(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	
	public void run()  {
		while (true) {
			
			String modifiedSentence;
			
			try {
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				modifiedSentence = inFromServer.readLine();
				System.out.println("FROM server:" + modifiedSentence);
				Main.parseDataFromServer(modifiedSentence);
				//Main.playerMoved(0,-1,"up");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
		}
		
		
		
	}
}
