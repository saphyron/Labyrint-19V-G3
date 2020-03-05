package game2020;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MsgFromServer extends Thread {
	
	public static Socket clientSocket;
	public void run() {

		String msgFromServer = "";
		List<String> formattedMsgFromServer = new ArrayList<>();

		try {
			clientSocket = new Socket("localhost", 6789);
		} catch (Exception e) {
			System.err.println("could not connect to server");
			return;
		}
		try {
			while (true) {
				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				msgFromServer = inFromServer.readLine();
				System.out.println("FROM server: " + msgFromServer);
				
				formattedMsgFromServer = Arrays.asList(msgFromServer.split(" ")); 
				
				System.out.println(formattedMsgFromServer);
				
				if (formattedMsgFromServer.get(0).equals("spawn")) {
					Main.spawnPlayer(formattedMsgFromServer.get(1), 6, 7);
				}
			}
		} catch (IOException e) {
			System.out.println("IO Exception from server");
		}

	}
}
