package game2020;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.ImageView;

public class Server {

	static List<Socket> userSessions = new ArrayList<>();
	
	
	public static void emit(String msg) throws IOException {
		for (Socket userSession : userSessions) {
			DataOutputStream outToClient = new DataOutputStream(userSession.getOutputStream());
			outToClient.writeBytes(msg);
		}
	}
	

	public static void main(String[] args) throws IOException {
		
		
		ServerSocket welcomeSocket = new ServerSocket(6789);
		
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			userSessions.add(connectionSocket);
			(new ServerThread(connectionSocket)).start();
		}
		
	}

	public static void emit() {
		// TODO Auto-generated method stub
		
	}

}
