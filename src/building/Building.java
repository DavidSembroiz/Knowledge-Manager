package building;

import java.util.ArrayList;

public class Building {
	
	private int numRooms;
	private String id;
	private ArrayList<Room> rooms;
	
	public Building(String id, int numRooms){
		this.rooms = new ArrayList<Room>();
		this.numRooms = numRooms;
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
	
	

}
