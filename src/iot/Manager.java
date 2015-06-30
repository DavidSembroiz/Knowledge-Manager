package iot;

import java.util.ArrayList;

import behaviour.PeopleManager;
import behaviour.Person;
import domain.DBListener;
import domain.Database;
import domain.Mqtt;
import domain.Utils;
import domain.Register;

public class Manager {
	
	private ArrayList<Room> rooms;
	private Mqtt mqtt;
	private Utils uts;
	private Database awsdb;
	private PeopleManager peopleManager;
	private Register reg;
	
	
	public Manager() {
		rooms = new ArrayList<Room>();
		uts = Utils.getInstance();
		peopleManager = new PeopleManager();
		awsdb = new Database();
		mqtt = new Mqtt(this, awsdb);
		reg = new Register(10, 10, 10);
		new DBListener(mqtt, awsdb.getConnectionListener());
	}
	
	
	private void simulate() {
		for (int step = 0; step < 10; step++) {
			peopleManager.makeStep();
			for (Room r : rooms) r.fireRules();
			printRooms();
			reg.printStepConsumption(step);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void processMessage(String topic, String message, String location, String soID) {
		Room r = getRoom(location);
		
		ArrayList<String> types = uts.getTypesFromMessage(message);
		for (String type : types) {
			Sensor s = r.getSensor(soID, type);
			
			s.setValue(uts.getValueFromType(message, type));
		}
		simulate();
		//if (peopleManager.isAllPeopleAssigned()) simulate();
	}
	
	public void manageMessage(String topic, String message) {
		String soID = uts.extractIdFromTopic(topic);
		String location = awsdb.getLocation(soID);
		if (location != null) processMessage(topic, message, location, soID);
		else {
			/**
			 * TODO Unable to query database, handle messages
			 */
			System.out.println("Unable to query room number, message queued");
		}
	}
	
	public Room getRoom(String location) {
		Room r = roomExists(location);
		if (r == null) r = registerRoom(location, peopleManager.assignPeopleToRoom(location));
		return r;
	}
	
	
	private Room registerRoom(String location, ArrayList<Person> people) {
		Room r = new Room(location, awsdb, people);
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