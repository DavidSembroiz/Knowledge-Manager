package building;

import java.util.ArrayList;
import java.util.HashSet;

import entity.Computer;

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

	public Room getRoom(String location) {
		for (Room r : rooms) {
			if (r.getLocation().equals(location)) return r;
		}
		return null;
	}

	public void updateConsumption() {
		for (Room r : rooms) {
			HashSet<Object> ents = r.getEntities();
			for (Object e : ents) {
				if (e instanceof Computer) {
					int cons = ((Computer) e).getCurrentState().getConsumption();
					/* Current  */
					((Computer) e).addConsumption(cons);
					
				}
			}
		}
	}
	
	

}
