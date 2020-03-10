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
			
			/*
			if (msgFromServer.equals("hello from server what is your name?")) {
				Main.sayName();
			} */
		}
	}

}
