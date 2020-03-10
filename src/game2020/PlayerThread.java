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

public class PlayerThread extends Thread {
	private BufferedReader inFromServer;
	private String read = "";
	private ArrayList<Player> players;
	private boolean playing = true;

	public PlayerThread(Socket socket) throws IOException {
		super();
		inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	@Override
	public void run() {
		while (playing) {
			try {
				read = inFromServer.readLine();
				ModtagPlayers(read);
				Main.removeShotOnScreen();
				Main.removePlayersOnScreen();
				Main.players = players;
				Main.addPlayersOnScreen();
				Main.addShotOnScreen();
				Main.updateScoreTable();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void ModtagPlayers(String read) throws IOException {
		players = new ArrayList<>();
		String[] stringarray = read.split(":");
		for (int i = 0; i < stringarray.length; i += 6) {
			Player x = new Player(stringarray[i], Integer.parseInt(stringarray[i + 1]), Integer.parseInt(stringarray[i + 2]), stringarray[i + 3],
					Integer.parseInt(stringarray[i + 4]), Boolean.parseBoolean(stringarray[i + 5]));
			players.add(x);
		}
	}
}