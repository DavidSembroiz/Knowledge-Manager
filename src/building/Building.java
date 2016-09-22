package building;

public class Building {
	
	private int numRooms;
	private String id;
	
	public Building(String id, int numRooms){
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
