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

	public static Socket clientSocket;
	public static DataOutputStream outToServer;

	String[] names = new String[] { "Alfa", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "India",
			"Juliet", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango",
			"Uniform", "Victor", "Whiskey", "X-Ray", "Yankee", "Zulu" };

	public static String name = "TBA";
	public static final int size = 20;
	public static final int scene_height = size * 20 + 100;
	public static final int scene_width = size * 20 + 200;

	public static Image image_floor;
	public static Image image_wall;
	public static Image hero_right, hero_left, hero_up, hero_down;

	public static List<Player> players = new ArrayList<Player>();

	private static Label[][] fields;
	private static TextArea scoreList;

	private static String[] board = { // 20x20
			"wwwwwwwwwwwwwwwwwwww", "w        ww        w", "w w  w  www w  w  ww", "w w  w   ww w  w  ww",
			"w  w               w", "w w w w w w w  w  ww", "w w     www w  w  ww", "w w     w w w  w  ww",
			"w   w w  w  w  w   w", "w     w  w  w  w   w", "w ww ww        w  ww", "w  w w    w    w  ww",
			"w        ww w  w  ww", "w         w w  w  ww", "w        w     w  ww", "w  w              ww",
			"w  w www  w w  ww ww", "w w      ww w     ww", "w   w   ww  w      w", "wwwwwwwwwwwwwwwwwwww" };

	// -------------------------------------------
	// | Maze: (0,0) | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1) | scorelist |
	// | | (1,1) |
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
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		initStart(primaryStage);
		scoreList.setText(getScoreList());

		clientSocket = new Socket("10.24.67.70", 4444);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());

		name = names[(new Random()).nextInt(names.length)];

		MsgFromServer p = new MsgFromServer(clientSocket);
		new Thread(p).start();

	}

	public static void drawPlayer(int x, int y, String direction) {
		Platform.runLater(() -> {
			fields[x][y].setGraphic(new ImageView(hero_up));

		});
	}

	public synchronized static void playerMoved(String name, int x, int y, int point, String direction, String otherPlayer, int otherPoints) {
		
		Platform.runLater(() -> {
			
		Player player2move = null;
		for (Player player : players) {
			if (player.name.equals(otherPlayer)) {
				player.point = otherPoints;
				break;
			}
		}

		for (Player player : players) {
			if (player.name.equals(name)) {
				player2move = player;
				break;
			}
		}

		if (player2move == null) {
			System.out.println("could not find player " + name);
			player2move = new Player(name, x, y, direction);
			
			players.add(player2move);
		} else {
			int oldX = player2move.xpos;
			int oldY = player2move.ypos;
			
			if ((oldX == x && oldY == y) == false)
				fields[oldX][oldY].setGraphic(new ImageView(image_floor));
		}
		player2move.point = point;
		player2move.direction = direction;
		
		player2move.xpos = x;
		player2move.ypos = y;

		if (direction.equals("right")) {
			fields[x][y].setGraphic(new ImageView(hero_right));
		}
		;
		if (direction.equals("left")) {
			fields[x][y].setGraphic(new ImageView(hero_left));
		}
		;
		if (direction.equals("up")) {
			fields[x][y].setGraphic(new ImageView(hero_up));
		}
		;
		if (direction.equals("down")) {
			fields[x][y].setGraphic(new ImageView(hero_down));
		};
		
		scoreList.setText(getScoreList());
		
		});
	}

	public static void sendMsg(String msg) {
		try {
			outToServer.writeBytes(msg);
		} catch (Exception e) {
			System.out.println("could not send msg");
		}
	}

	public static String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p + "\r\n");
		}
		return b.toString();
	}

	public static void sayName() {
		sendMsg("name " + name + "\n");
	}

	public static void main(String[] args) {
		launch(args);
	}
}