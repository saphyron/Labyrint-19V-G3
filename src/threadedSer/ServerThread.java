package threadedSer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import game2020.Player;

public class ServerThread extends Thread {
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;

	private static final Set<String> DIRECTIONS = Set.of("left", "up", "right", "down");

	public ServerThread(BufferedReader inFromClient, DataOutputStream outToClient) {
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
	}

	public void run() {
		String clientSentence = null;
		String capitalizedSentence;
		List<String> listedStrFromClient = new ArrayList<>();

		System.out.println("will send hello");
		sendMsg("hello from server what is your name?");

		try {
			outToClient.writeBytes("hi");
		} catch (Exception e) {
			System.out.println("could not send hi");
		}
		while (true) {
			try {
				clientSentence = inFromClient.readLine().replaceAll("\n", "");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("from client " + clientSentence);

			listedStrFromClient = Arrays.asList(clientSentence.split(" "));

			if (listedStrFromClient.size() == 2 && listedStrFromClient.get(0).equalsIgnoreCase("spawn")) {
				String name = listedStrFromClient.get(1);
				System.out.println("a new player will be added to storage with name " + name);
				ServerOld.players.add(new Player(name, 6, 7, "up"));
			}

			else if (clientSentence.equals("requestPlayers")) {
				System.out.println("will send all players");
				System.out.println(ServerOld.allPlayers());
				sendMsg(ServerOld.allPlayers());
			}

			else if (listedStrFromClient.size() == 2 && DIRECTIONS.contains(listedStrFromClient.get(0))) {

				String direction = listedStrFromClient.get(0);
				String name = listedStrFromClient.get(1);

				switch (direction) {
				case "up":
					ServerOld.playerMoved(0, -1, "up", name);
					break;
				case "down":
					ServerOld.playerMoved(0, +1, "down", name);
					break;
				case "left":
					ServerOld.playerMoved(-1, 0, "left", name);
					break;
				case "right":
					ServerOld.playerMoved(+1, 0, "right", name);
					break;
				default:
					break;
				}

			}

			/*
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			Server.emit(capitalizedSentence); */
		}
	}

	private void sendMsg(String msg) {
		try {
			outToClient.writeBytes(msg);
		} catch (IOException e) {
			System.out.println("could not send msg to player");
		}
	}
}