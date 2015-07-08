package iot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

import behaviour.PeopleManager;
import behaviour.Person;
import domain.DBListener;
import domain.Database;
import domain.Mqtt;
import domain.Utils;
import domain.Register;
import models.Weather;

public class Manager {
	
	private int MODE = 1;
	private int RECORD_FILE = 1;
	
	private ArrayList<Room> rooms;
	private Mqtt mqtt;
	private Utils uts;
	private Database awsdb;
	private PeopleManager peopleManager;
	private Register reg;
	private Weather models;
	
	
	public Manager() {
		rooms = new ArrayList<Room>();
		uts = Utils.getInstance();
		reg = Register.getInstance();
		models = Weather.getInstance();
		peopleManager = PeopleManager.getInstance();
		awsdb = new Database();
		mqtt = new Mqtt(this, awsdb);
		new DBListener(mqtt, awsdb.getConnectionListener());
	}
	
	private void sleep(int s) {
		try {
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	private void simulate() {
		
		if (MODE == 0) {
			computeDumbScenarioConsumption(); 
			return;
		}
		
		if (RECORD_FILE == 1) peopleManager.enableRecordFile();
		
		while(Utils.CURRENT_STEP < Utils.STEPS) {
			System.out.println("------------------------------- STEP " + Utils.CURRENT_STEP + " -------------------------------");
			peopleManager.makeStep();
			for (Room r : rooms) r.fireRules();
			//printRooms();
			reg.computeConsumption();
			reg.printStepConsumption();
			sleep(1);
			
			peopleManager.flushData(100);
			++Utils.CURRENT_STEP;
		}
		peopleManager.closeFile();
	}
	
	private void repeatSimulation() {
		PriorityQueue<Event> events = readEventFile();
		Event e;
		while(Utils.CURRENT_STEP < Utils.STEPS) {
			System.out.println("------------------------------- STEP " + Utils.CURRENT_STEP + " -------------------------------");
			while (!events.isEmpty() && events.peek().getStep() == Utils.CURRENT_STEP) {
				e = events.poll();
				peopleManager.executeAction(e.getPerson(), e.getAction());
			}
			for (Room r : rooms) r.fireRules();
			//printRooms();
			reg.computeConsumption();
			reg.printStepConsumption();
			sleep(1);
			
			++Utils.CURRENT_STEP;
		}
	}
	
	
	private void processMessage(String topic, String message, String location, String soID) {
		Room r = getRoom(location);
		ArrayList<String> types = uts.getTypesFromMessage(message);
		for (String type : types) {
			Sensor s = r.getSensor(soID, type);
			
			/**
			 * Currently changed to fit the simulation
			 */
			
			if (type.equals("temperature")) s.setValue(Double.toString(models.getCurrentEnvironmentalTemperature()));
			else if (type.equals("humidity")) s.setValue(Double.toString(models.getCurrentEnvironmentalHumidity()));
			else if (type.equals("luminosity")) s.setValue(Double.toString(models.getCurrentEnvironmentalLight()));
			else s.setValue(uts.getValueFromType(message, type));
		}
		if (allRoomsDefined() && peopleManager.isAllPeopleAssigned()) simulate();
	}
	
	public void manageMessage(String topic, String message) {
		String soID = uts.extractIdFromTopic(topic);
		String location = awsdb.getLocation(soID);
		if (location != null) processMessage(topic, message, location, soID);
		else {
			/**
			 * TODO Unable to query database, handle messages
			 */
			System.out.println("Unable to query room number, message discarded");
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
	
	private boolean allRoomsDefined() {
		for (Room r : rooms) {
			if (!r.allSensorsDefined()) return false;
		}
		return true;
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
	
	private PriorityQueue<Event> readEventFile() {
		PriorityQueue<Event> events = new PriorityQueue<Event>();
		try(BufferedReader br = new BufferedReader(new FileReader("res/events.txt"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] values = line.split(",");
	        	events.add(new Event(values[0], values[1], Integer.parseInt(values[2])));
	        }
	        return events;
	    } catch (IOException e) {
	    	System.out.println("ERROR: Unable to read events from file.");
	    	e.printStackTrace();
	    }
		return events;
	}
	
	
	private void computeDumbScenarioConsumption() {
		
		/**
		 * In this scenario, everything is ON throughout the whole day
		 */
		
		int numRooms = rooms.size();
		
		reg.setNumComputers(numRooms);
		reg.setNumHvacs(numRooms);
		reg.setNumLights(numRooms);
		
		int cons = reg.computeConsumption();
		System.out.println("Consumption: " + cons + " W");
	}
	
}