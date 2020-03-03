package game2020;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.stage.Stage;
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
	public static Image hero_right,hero_left,hero_up,hero_down;

	public static Player me;
	public static String id;
	public static Socket clientSocket;
	public static List<Player> players = new ArrayList<Player>();

	private static Label[][] fields;
	private static TextArea scoreList;
	
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

	
	// -------------------------------------------
	// | Maze: (0,0)              | Score: (1,0) |
	// |-----------------------------------------|
	// | boardGrid (0,1)          | scorelist    |
	// |                          | (1,1)        |
	// -------------------------------------------

	@Override
	public void start(Stage primaryStage) {
		try {
			clientSocket = new Socket("localhost", 6789);
			(new MsgFromServer(clientSocket)).start();
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

			image_wall  = new Image(getClass().getResourceAsStream("Image/wall4.png"),size,size,false,false);
			image_floor = new Image(getClass().getResourceAsStream("Image/floor1.png"),size,size,false,false);

			hero_right  = new Image(getClass().getResourceAsStream("Image/heroRight.png"),size,size,false,false);
			hero_left   = new Image(getClass().getResourceAsStream("Image/heroLeft.png"),size,size,false,false);
			hero_up     = new Image(getClass().getResourceAsStream("Image/heroUp.png"),size,size,false,false);
			hero_down   = new Image(getClass().getResourceAsStream("Image/heroDown.png"),size,size,false,false);

			fields = new Label[20][20];
			for (int j=0; j<20; j++) {
				for (int i=0; i<20; i++) {
					switch (board[j].charAt(i)) {
					case 'w':
						fields[i][j] = new Label("", new ImageView(image_wall));
						break;
					case ' ':					
						fields[i][j] = new Label("", new ImageView(image_floor));
						break;
					default: throw new Exception("Illegal field value: "+board[j].charAt(i) );
					}
					boardGrid.add(fields[i][j], i, j);
				}
			}
			scoreList.setEditable(false);
			
			
			grid.add(mazeLabel,  0, 0); 
			grid.add(scoreLabel, 1, 0); 
			grid.add(boardGrid,  0, 1);
			grid.add(scoreList,  1, 1);
						
			Scene scene = new Scene(grid,scene_width,scene_height);
			primaryStage.setScene(scene);
			primaryStage.show();

			scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
				switch (event.getCode()) {
				case UP:    movePlayer("up");    break;
				case DOWN:  movePlayer("down");  break;
				case LEFT:  movePlayer("left");  break;
				case RIGHT: movePlayer("right"); break;
				default: break;
				}
			});
			id = Math.random() + "";
			joinServer();
            // Setting up standard players
			
//			me = new Player(this.id,9,4,"up");
//			players.add(me);
//			fields[9][4].setGraphic(new ImageView(hero_up));

			scoreList.setText(getScoreList());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void parseDataFromServer(String dataFromServer) {
		String[] parseData = dataFromServer.split(" ");
		if (parseData.length < 2) {
			System.out.println("not understood " + dataFromServer);
			return;
		}
		String command = parseData[0];
		String id = parseData[1];
		messageFromServer(command, id);
	}
	public static void messageFromServer(String command, String id) {
		Platform.runLater(() -> {
			
			switch (command) {
			case "UP":    playerMoved(0,-1,"up", id);    break;
			case "DOWN":  playerMoved(0,+1,"down", id);  break;
			case "LEFT":  playerMoved(-1,0,"left", id);  break;
			case "RIGHT": playerMoved(+1,0,"right", id); break;
			case "NEWPLAYER": newPlayer(id); break;
			case "REMOVEPLAYER": removePlayer(id); break;
			default: break;
			}
			
			scoreList.setText(getScoreList());
		});
	}
	
	
	public static void sendBesked(String besked) {
		DataOutputStream outToServer;
		try {
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(besked + '\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void newPlayer(String navn) {
		Player np = new Player(navn,9,4,"up");
		players.add(np);
		fields[9][4].setGraphic(new ImageView(hero_up));
	}
	
	public static void removePlayer(String navn) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).getName().equals(navn)) {
				players.remove(i);
				return;
			}
		}
	}
	
	public static void joinServer() {
		sendBesked("NEWPLAYER" + " " + id);
	}
	
	public static void movePlayer(String direction) {
		sendBesked(direction + " " + id);
	}

	public static void playerMoved(int delta_x, int delta_y, String direction, String id) {
		

		Player player2move = null;
		
		for (Player player : players) {
			if (player.getName().equals(id)) {
				player2move = player;
				break;
			}
		}
		
		if (player2move == null) {
			System.out.println("could not find player " + id);
			return;
		}
		

		
		player2move.direction = direction;
		int x = player2move.getXpos(),y = player2move.getYpos();

		if (board[y+delta_y].charAt(x+delta_x)=='w') {
			player2move.addPoints(-1);
		} 
		else {
			Player p = getPlayerAt(x+delta_x,y+delta_y);
			if (p!=null) {
			  player2move.addPoints(10);
              p.addPoints(-10);
			} else {
				player2move.addPoints(1);
			
				fields[x][y].setGraphic(new ImageView(image_floor));
				x+=delta_x;
				y+=delta_y;

				if (direction.equals("right")) {
					fields[x][y].setGraphic(new ImageView(hero_right));
				};
				if (direction.equals("left")) {
					fields[x][y].setGraphic(new ImageView(hero_left));
				};
				if (direction.equals("up")) {
					fields[x][y].setGraphic(new ImageView(hero_up));
				};
				if (direction.equals("down")) {
					fields[x][y].setGraphic(new ImageView(hero_down));
				};

				player2move.setXpos(x);
				player2move.setYpos(y);
			}
		}
		scoreList.setText(getScoreList());
	}

	public static String getScoreList() {
		StringBuffer b = new StringBuffer(100);
		for (Player p : players) {
			b.append(p+"\r\n");
		}
		return b.toString();
	}

	public static Player getPlayerAt(int x, int y) {
		for (Player p : players) {
			if (p.getXpos()==x && p.getYpos()==y) {
				return p;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println("hello?");
		launch(args);
	}
}

