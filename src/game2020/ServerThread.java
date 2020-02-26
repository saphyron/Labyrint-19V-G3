package game2020;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {

	Socket connSocket = new Socket();

	public ServerThread(Socket connSocket) {
		this.connSocket = connSocket;
	}
	
	HashMap<String, Logic> commands = new HashMap<String, Logic>();
	
	public interface Logic {
		public String returnPayload(String input);
	}
	
	public class Repeat implements Logic {

		@Override
		public String returnPayload(String input) {
			return input;
		}
		
	}

	public void run() {
		
		commands.put("NEWPLAYER", new Repeat());
		
		BufferedReader inFromClient;
		while (true) {

			try {
				inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
				var msgFromClient = inFromClient.readLine();
				String[] commandsFromClient = msgFromClient.split(" ");
				
				//System.out.println(commands);
				
				
				
				if (commands.containsKey(commandsFromClient[0])) {
					System.out.println("A new player connected. Will now emit\n" + msgFromClient);
				}
				
				//System.out.println("Received: " + clientSentence);
				
				//ServerInit.emit(clientSentence.split(" ")[0].toUpperCase() + "\r\n");
				//outToClient.writeBytes(clientSentence.split(" ")[0].toUpperCase() + "\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				
				e.printStackTrace();
				Server.userSessions.remove(connSocket);
				return;
			}

		}

	}
}
