package iot;

import java.util.ArrayList;

import domain.DBListener;
import domain.Database;
import domain.Mqtt;
import domain.Utils;

public class Manager {
	
	private ArrayList<Room> rooms;
	private Mqtt mqtt;
	private Utils uts;
	private Database awsdb;
	
	
	public Manager() {
		rooms = new ArrayList<Room>();
		uts = new Utils();
		awsdb = new Database();
		mqtt = new Mqtt(this, uts, awsdb);
		mqtt.start();
		new DBListener(mqtt, awsdb.getConnectionListener());
		//manageMessage("asdasdsa/1432288139512153dd1f071164edca16fb7772ea3e5e9",
		//		      "{\"lastUpdate\":1432544883339,\"channels\":{\"luminosity\":{\"current-value\":627}}}");
	}
	
	public void manageMessage(String topic, String message) {
		String soID = uts.extractIdFromTopic(topic);
		String location = awsdb.getLocation(soID);
		
		Room r = getRoom(location);
		
		ArrayList<String> types = uts.getTypesFromMessage(message);
		for (String type : types) {
			Sensor s = r.getSensor(soID, type);
			s.setValue(uts.getValueFromType(message, type));
		}
		
		r.fireRules();
		printRooms();
	}
	
	public Room getRoom(String location) {
		Room r = roomExists(location);
		if (r == null) r = registerRoom(location);
		return r;
	}
	
	
	private Room registerRoom(String location) {
		Room r = new Room(location);
		rooms.add(r);
		return r;
	}
	
	private Room roomExists(String location) {
		for (Room r : rooms) {
			if (r.getLocation().equals(location)) return r;
		}
		return null;
	}
	
	private void printRooms() {
		for (int i = 0; i < rooms.size(); ++i) {
			Room r = rooms.get(i);
			ArrayList<Sensor> sens = r.getSensors();
			System.out.println("Room " + r.getLocation());
			for (int j = 0; j < sens.size(); ++j) {
				Sensor s = sens.get(j);
				System.out.println("--- Sensor " + s.getSoID());
				System.out.println("----- Type " + s.getType());
				System.out.println("----- Value " + s.getValue());
			}
			
			
		}
	}
}
