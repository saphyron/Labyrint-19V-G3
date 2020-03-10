package game2020;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MsgFromServer extends Thread {

	private BufferedReader inFromServer;

	public MsgFromServer(Socket socket) throws IOException {
		inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void run() {
		try {
			run2();
		} catch (IOException e) {
			System.out.println("good bye");
			return;
		}
	}

	public void run2() throws IOException {
		System.out.println("My job is to listen to server");
		System.out.println(inFromServer == null);
		while (true) {
			String msgFromServer = inFromServer.readLine();
			System.out.println("FROM SERVER:" + msgFromServer);
			Main.sendMsg("modtaget!!\n");
			
			List<String> k = Arrays.asList(msgFromServer.split(" "));
			
			if (k.size() == 2 && k.get(0).equals("update")) {
				List<String> lstUpdate = Arrays.asList(k.get(1).split("&"));
				
				System.out.println(lstUpdate.get(1));
				System.out.println(lstUpdate.get(2));
				
				
				int x = Integer.parseInt(lstUpdate.get(1).split("=")[1]);
				int y = Integer.parseInt(lstUpdate.get(2).split("=")[1]);
				String name = lstUpdate.get(0).split("=")[1];
				Main.playerMoved(x,y, "up", name);
				
				//Main.drawPlayer(x, y, "dummy");
				
			}
		}
	}
}