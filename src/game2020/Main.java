package game2020;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.*;

public class Main extends Application {

	public static final int size = 20;
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right, hero_left, hero_up, hero_down;
	public static Image fire_right, fire_left, fire_up, fire_down;
	public static Image fire_horizontal, fire_vertical;
	public static Image fire_wallEast, fire_wallWest, fire_wallNorth, fire_wallSouth;

	public static String name = "TBA";
	public static List<Player> players = new ArrayList<Player>();

	private static Label[][] fields;
	private static TextArea scoreList;
	public static Socket clientSocket;
	public static DataOutputStream outToServer;

	private static String[] board = { // 20x20
			"wwwwwwwwwwwwwwwwwwww", "w        ww        w", "w w  w  www w  w  ww", "w w  w   ww w  w  ww", "w  w               w",
			"w w w w w w w  w  ww", "w w     www w  w  ww", "w w     w w w  w  ww", "w   w w  w  w  w   w", "w     w  w  w  w   w",
			"w ww ww        w  ww", "w  w w    w    w  ww", "w        ww w  w  ww", "w         w w  w  ww", "w        w     w  ww",
			"w  w              ww", "w  w www  w w  ww ww", "w w      ww w     ww", "w   w   ww  w      w", "wwwwwwwwwwwwwwwwwwww" };

	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	public void initStart(Stage primaryStage) throws Exception {

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				try {
					clientSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Platform.exit();
				System.exit(0);
			}
		});

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(0, 10, 0, 10));

		Text mazeLabel = new Text("Maze:");
		mazeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

		Text scoreLabel = new Text("Score:");
		scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

		scoreList = new TextArea();

		GridPane boardGrid = new GridPane();

		image_wall = new Image(getClass().getResourceAsStream("Image/wall4.png"), size, size, false, false);
		image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"), size, size, false, false);

		hero_right = new Image(getClass().getResourceAsStream("Image/heroRight.png"), size, size, false, false);
		hero_left = new Image(getClass().getResourceAsStream("Image/heroLeft.png"), size, size, false, false);
		hero_up = new Image(getClass().getResourceAsStream("Image/heroUp.png"), size, size, false, false);
		hero_down = new Image(getClass().getResourceAsStream("Image/heroDown.png"), size, size, false, false);

		fire_right = new Image(getClass().getResourceAsStream("Image/fireRight.png"), size, size, false, false);
		fire_left = new Image(getClass().getResourceAsStream("Image/fireLeft.png"), size, size, false, false);
		fire_up = new Image(getClass().getResourceAsStream("Image/fireUp.png"), size, size, false, false);
		fire_down = new Image(getClass().getResourceAsStream("Image/fireDown.png"), size, size, false, false);

		fire_horizontal = new Image(getClass().getResourceAsStream("Image/fireHorizontal.png"), size, size, false, false);
		fire_vertical = new Image(getClass().getResourceAsStream("Image/fireVertical.png"), size, size, false, false);
		
		fire_wallEast = new Image(getClass().getResourceAsStream("Image/fireWallEast.png"), size, size, false, false);
		fire_wallWest = new Image(getClass().getResourceAsStream("Image/fireWallWest.png"), size, size, false, false);
		fire_wallNorth = new Image(getClass().getResourceAsStream("Image/fireWallNorth.png"), size, size, false, false);
		fire_wallSouth = new Image(getClass().getResourceAsStream("Image/fireWallSouth.png"), size, size, false, false);
		
		fields = new Label[20][20];
		for (int j = 0; j < 20; j++) {
			for (int i = 0; i < 20; i++) {
				switch (board[j].charAt(i)) {
				case 'w':
					fields[i][j] = new Label("", new ImageView(image_wall));
					break;
				case ' ':
					fields[i][j] = new Label("", new ImageView(image_floor));
					break;
				default:
					throw new Exception("Illegal field value: " + board[j].charAt(i));
				}
				boardGrid.add(fields[i][j], i, j);
			}
		}
		scoreList.setEditable(false);

		grid.add(mazeLabel, 0, 0);
		grid.add(scoreLabel, 1, 0);
		grid.add(boardGrid, 0, 1);
		grid.add(scoreList, 1, 1);

		Scene scene = new Scene(grid, scene_width, scene_height);
		primaryStage.setScene(scene);
		primaryStage.show();

		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
			case UP:
				sendMsg("up " + name + "\n");
				break;
			case DOWN:
				sendMsg("down " + name + "\n");
				break;
			case LEFT:
				sendMsg("left " + name + "\n");
				break;
			case RIGHT:
				sendMsg("right " + name + "\n");
				break;
			default:
				break;
			}
		});

		// Setting up standard players
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		initStart(primaryStage);
		scoreList.setText(getScoreList());

		clientSocket = new Socket("localhost", 4444);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());

		name = names[(new Random()).nextInt(names.length)];

		MsgFromServer p = new MsgFromServer(clientSocket);
		new Thread(p).start();

	}

	public static void sendMsg(String msg) {
		try {
			outToServer.writeBytes(msg);
		} catch (Exception e) {
			System.out.println("could not send msg");
		}
	}

	public static void sayName() {
		sendMsg("name " + name + "\n");
	}
	
	public void sendData(int delta_x, int delta_y, String direction) throws IOException {
		String string = name + ":" + delta_x + ":" + delta_y + ":" + direction;
		outToServer.writeBytes(string + '\n');
	}

	// ----------------------------------------------------
	
	public static String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p + "\r\n");
		}
		return b.toString();
	}
	
	public static void updateScoreTable() {
		Platform.runLater(() -> {
			scoreList.setText(getScoreList());
		});
	}
	
	// ----------------------------------------------------
	
	public static void addPlayersOnScreen() {
		for (Player p : players) {
			Platform.runLater(() -> {
				if (p.getDirection().equals("right")) {
					fields[p.getXpos()][p.getYpos()].setGraphic(new ImageView(hero_right));
				}
				;
				if (p.getDirection().equals("left")) {
					fields[p.getXpos()][p.getYpos()].setGraphic(new ImageView(hero_left));
				}
				;
				if (p.getDirection().equals("up")) {
					fields[p.getXpos()][p.getYpos()].setGraphic(new ImageView(hero_up));
				}
				;
				if (p.getDirection().equals("down")) {
					fields[p.getXpos()][p.getYpos()].setGraphic(new ImageView(hero_down));
				}
				;
			});
		}
	}
	
	public static void removePlayersOnScreen() {
		for (Player p : players) {
			Platform.runLater(() -> {
				fields[p.getXpos()][p.getYpos()].setGraphic(new ImageView(image_floor));
			});
		}
	}
	
	public Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos() == x && p.getYpos() == y) {
				return p;
			}
		}
		return null;
	}
	
	// ----------------------------------------------------
	
	public static void addShotOnScreen() {
		// TODO
	}
	
	public static void removeShotOnScreen() {
		// TODO
	}
	
	// ----------------------------------------------------

	public static void main(String[] args) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("What is the IP of the server?");
		String ipaddress = reader.readLine();
		System.out.println("Which port do you want to connect to?");
		int port = Integer.parseInt(reader.readLine());
		System.out.println("Input player name");
		name = reader.readLine();

		clientSocket = new Socket(ipaddress, port);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		playerThread = new PlayerThread(inFromServer);

		outToServer.writeBytes(name + '\n');
		String answer = inFromServer.readLine();
		while (answer.equals("n")) {
			System.out.println("name already in use. Input another:");
			name = reader.readLine();
			outToServer.writeBytes(name + "\n");
			answer = inFromServer.readLine();
		}

		playerThread.start();
		launch(args);
	}
}