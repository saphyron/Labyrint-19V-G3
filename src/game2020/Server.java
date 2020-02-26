package game2020;

import javafx.scene.image.ImageView;

public class Server {

	public static void main(String[] args) {
		
		Player Jeppe = new Player("Jeppe",9,4,"up");
		players.add(Jeppe);
		fields[9][4].setGraphic(new ImageView(hero_up));

		Player John = new Player("John",14,15,"up");
		players.add(John);
		fields[14][15].setGraphic(new ImageView(hero_up));
		
		Player Joakim = new Player("Joakim", 14, 4, "up");
		players.add(Joakim);
		fields[14][4].setGraphic(new ImageView(hero_up));
		// TODO Auto-generated method stub

	}

}
