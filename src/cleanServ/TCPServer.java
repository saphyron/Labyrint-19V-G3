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
		
		while(true){
			Socket connectionSocket = welcomSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			Thread k = new SomeThread(inFromClient, new DataOutputStream(connectionSocket.getOutputStream()));
			k.start();
		}

	}
	
	

}