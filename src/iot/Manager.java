package iot;

import java.util.*;

import domain.*;

public class Manager {
	
	private static final int NOT_FOUND = -1;
	
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
	}
	
	public void manageMessage(String topic, String message) {
		String soID = uts.extractIdFromTopic(topic);
		String location = awsdb.getLocation(soID);
		
		Room r = getRoom(location);
		
		ArrayList<String> types = uts.getTypesFromMessage(message);
		ArrayList<Sensor> sens = new ArrayList<Sensor>();
		for (int i = 0; i < types.size(); ++i) {
			Sensor s = r.getSensor(soID, types.get(i));
			s.setValue(uts.getValueFromType(message, types.get(i)));
			sens.add(s);
		}
		printRooms();
	}
	
	public Room getRoom(String location) {
		int pos = roomExists(location);
		if (pos >= 0) return rooms.get(pos);
		return registerRoom(location);
	}
	
	
	private Room registerRoom(String location) {
		Room r = new Room(location);
		rooms.add(r);
		return r;
	}
	
	private int roomExists(String location) {
		for (int i = 0; i < rooms.size(); ++i) {
			if (rooms.get(i).getLocation().equals(location)) return i;
		}
		return NOT_FOUND;
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
