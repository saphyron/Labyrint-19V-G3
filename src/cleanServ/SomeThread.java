package cleanServ;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

		try {

			String clientSentence;
			String capitalizedSentence;
			List<String> listedStrFromClient;
			outToClient.writeBytes("hello from server what is your name?\n");
			Thread.sleep(1000);
			outToClient.writeBytes("hello agian..");
			while (true) {
				clientSentence = inFromClient.readLine();
				System.out.println("from client: " + clientSentence);

			}
		} catch (Exception e) {
			System.out.println("ERR");
			return;
		}

	}
}
