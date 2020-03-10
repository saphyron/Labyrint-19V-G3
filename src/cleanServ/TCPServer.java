package cleanServ;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import game2020.Player;

public class TCPServer {

	public static void main(String[] args) throws Exception {

		ServerSocket welcomSocket = new ServerSocket(4444);

		while (true) {
			Socket connectionSocket = welcomSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			var outFromClient = new DataOutputStream(connectionSocket.getOutputStream());
			lstOutToClient.add(outFromClient);
			Thread k = new SomeThread(inFromClient, outFromClient);
			k.start();
		}

	}

	private static List<DataOutputStream> lstOutToClient = new ArrayList<>();
	
	public static void emit(String msg) {
		for (DataOutputStream outToClient : lstOutToClient) {
			try {
				outToClient.writeBytes(msg);
			} catch (IOException e) {
				System.out.println("could not send msg from emit call");
			}
		}
	}
	
	
	static List<Player> players = new ArrayList<Player>();

	public static String allPlayers() {

		StringBuilder out = new StringBuilder();

		for (Player player : players) {
			out.append(player.getAllInfo() + "\n");
		}

		return out.toString();
	}
	

	
	private static  String[] board = {    // 20x20
			"wwwwwwwwwwwwwwwwwwww",
			"w        ww        w",
			"w w  w  www w  w  ww",
			"w w  w   ww w  w  ww",
			"w  w               w",
			"w w w w w w w  w  ww",
			"w w     www w  w  ww",
			"w w     w w w  w  ww",
			"w   w w  w  w  w   w",
			"w     w  w  w  w   w",
			"w ww ww        w  ww",
			"w  w w    w    w  ww",
			"w        ww w  w  ww",
			"w         w w  w  ww",
			"w        w     w  ww",
			"w  w              ww",
			"w  w www  w w  ww ww",
			"w w      ww w     ww",
			"w   w   ww  w      w",
			"wwwwwwwwwwwwwwwwwwww"
	};
	
	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}

	static public void playerMoved(int delta_x, int delta_y, String direction, String name) {

		Player me = null;
		for (Player player : players) {
			if (player.name.equalsIgnoreCase(name)) {
				me = player;
			}
		}

		if (me == null) {
			System.out.println("123could not find player " + name);
			players.add(new Player(name, 1, 1, "up"));
			
			String payload = String.format("name=%s&x=%d&y=%d&points=%d&direction=%s", name, 1, 1, 0, "up");
			String toSend = "update " + payload + "\n";

			System.out.println("_______" + toSend);
			emit(toSend);

			return;
		}

		String collisionPlayerName = null;
		int collisionPlayerPoints = -1;

		me.direction = direction;
		int x = me.getXpos(), y = me.getYpos();

		if (board[y + delta_y].charAt(x + delta_x) == 'w') {
			me.addPoints(-1);
			System.out.println("[serv] player walked into a wall");
		} else {
			Player p = getPlayerAt(x + delta_x, y + delta_y);
			if (p != null) {
				me.addPoints(10);
				p.addPoints(-10);

				collisionPlayerPoints = p.point;
				collisionPlayerName = p.name;
				System.out.println("[serv] player walked into a player");
			} else {
				me.addPoints(1);

				x += delta_x;
				y += delta_y;

				me.setXpos(x);
				me.setYpos(y);

				System.out.println("[serv] player walked into a emty place");
			}
		}

		String payload = String.format("name=%s&x=%d&y=%d&points=%d&direction=%s", name, x, y, me.point, direction);

		if (collisionPlayerName != null) {
			payload += String.format("cname=%s&cpoints=%d", collisionPlayerName, collisionPlayerPoints);
		}

		String toSend = "update " + payload + "\n";

		System.out.println(toSend);
		emit(toSend);

	}

}