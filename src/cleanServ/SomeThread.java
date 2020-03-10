package cleanServ;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import game2020.Player;

public class SomeThread extends Thread {

	private BufferedReader inFromClient;
	private DataOutputStream outToClient;

	private static final Set<String> DIRECTIONS = Set.of("left", "up", "right", "down");

	public SomeThread(BufferedReader inFromClient, DataOutputStream outToClient) {
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
	}

	@Override
	public void run() {



			String clientSentence = null;
			String capitalizedSentence;
			List<String> listedStrFromClient;
			try {

				outToClient.writeBytes("hello from server\n");
				outToClient.flush();
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			while (true) {
				try {
					clientSentence = inFromClient.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				System.out.println("from client: " + clientSentence);
				listedStrFromClient = Arrays.asList(clientSentence.split(" "));

				if (listedStrFromClient.size() == 2 && DIRECTIONS.contains(listedStrFromClient.get(0))) {
					
					System.out.println("move trigger!!");
					String direction = listedStrFromClient.get(0);
					String name = listedStrFromClient.get(1);

					System.out.println(direction + " " + name + " update");

					
					switch (direction) {
					case "up":
						TCPServer.playerMoved(0, -1, "up", name);
						break;
					case "down":
						TCPServer.playerMoved(0, +1, "down", name);
						break;
					case "left":
						TCPServer.playerMoved(-1, 0, "left", name);
						break;
					case "right":
						TCPServer.playerMoved(+1, 0, "right", name);
						break;
					default:
						break;
					}

				}

			}
		

	}
}
