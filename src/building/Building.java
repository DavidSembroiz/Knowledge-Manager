package building;

import java.util.ArrayList;

public class Building {
	
	private int numRooms;
	private String id;
	private ArrayList<Room> rooms;
	
	public Building(String id, ArrayList<Room> rooms){
		this.rooms = rooms;
		this.numRooms = rooms.size();
		this.id = id;
	}

	public int getNumRooms() {
		return numRooms;
	}

	public void setNumRooms(int numRooms) {
		this.numRooms = numRooms;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<Room> getRooms() {
		return this.rooms;
	}
	
	

}
