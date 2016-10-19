package building;

import java.util.ArrayList;
import java.util.HashSet;

import behaviour.Person;
import entity.Computer;

public class Building {
	
	private int numRooms;
	private String id;
	private ArrayList<Room> rooms;
	private String[] locations;
	
	public Building(String id, ArrayList<Room> rooms){
		this.rooms = rooms;
		addSpecialRooms();
		this.numRooms = rooms.size();
		this.id = id;
		parseLocations();
	}
	
	private void addSpecialRooms() {
		rooms.add(new Room("outside", "0"));
		rooms.add(new Room("inside", "0"));
		rooms.add(new Room("salon", "0"));
	}
	
	private void parseLocations() {
		HashSet<String> locs = new HashSet<String>();
		for (Room r : rooms) {
			if (Integer.parseInt(r.getSize()) > 0) locs.add(r.getLocation());
		}
		locations = locs.toArray(new String[locs.size()]);
	}

	public String[] getLocations() {
		return locations;
	}

	public void setLocations(String[] l) {
		this.locations = l;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
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

	public void movePerson(Person p, String currentLoc) {
		for (Room r : rooms) {
			String roomLoc = r.getLocation();
			if (currentLoc.equals(roomLoc)) {
				r.removePerson(p);
			}
			else if (p.getLocation().equals(roomLoc)) {
				r.addPerson(p);
			}
		}
	}
	
	

}
