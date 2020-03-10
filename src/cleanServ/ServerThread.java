package cleanServ;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
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

	@Override
	public void run() {
			for (Player player: Server.players) {
				String payload = String.format("name=%s&x=%d&y=%d&points=%d&direction=%s", player.name, player.xpos, player.ypos, player.point, player.direction);
				String toSend = "update " + payload + "\n";

				System.out.println(toSend);
				Server.emit(toSend);
			}
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
						Server.playerMoved(0, -1, "up", name);
						break;
					case "down":
						Server.playerMoved(0, +1, "down", name);
						break;
					case "left":
						Server.playerMoved(-1, 0, "left", name);
						break;
					case "right":
						Server.playerMoved(+1, 0, "right", name);
						break;
					default:
						break;
					}

				}

			}
		

	}
}
