package game2020;

import java.io.DataOutputStream;

public class Player {
	public String name;
	public int xpos;
	public int ypos;
	public String direction;
	public int point;
	public boolean shooting;
	private DataOutputStream outToPlayer;

	public Player(String name, int xpos, int ypos, String direction, int point, boolean shooting) {
		this.name = name;
		this.xpos = xpos;
		this.ypos = ypos;
		this.direction = direction;
		this.point = point;
		this.shooting = shooting;
	}

	public String getName() {
		return name;
	}

	// ----------------------------------------------------
	
	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public boolean isShooting() {
		return shooting;
	}

	public void setShooting(boolean shooting) {
		this.shooting = shooting;
	}
	
	// ----------------------------------------------------
	
	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}
	
	public void addPoints(int p) {
		point += p;
	}

	// ----------------------------------------------------
	
	public DataOutputStream getOutToPlayer() {
		return outToPlayer;
	}

	public void setOutToPlayer(DataOutputStream outToPlayer) {
		this.outToPlayer = outToPlayer;
	}
	
	public String getAllInfo() {
		return name + " " + xpos + " " + ypos + " " + point + " " + direction;
	}
	
	@Override
	public String toString() {
		return name + ":   " + point;
	}
}