package iot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
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
	
	/**
	 * MODE to run the different version alternatives:
	 * 
	 *  0: dumb scenario, everything is on throughout working hours
	 *  2: repeat simulation
	 *  Rest: normal simulation
	 */
	
	private int MODE = 2;
	
	/**
	 * The record file saves all the actions in events.txt
	 */
	
	private int RECORD_FILE = 1;
	
	/**
	 * If NEW_PEOPLE == 1, then a new group of people is generated before the simulation
	 */
	
	private int NEW_PEOPLE = 0;
	
	private int PROFESSOR_NUM_ROOMS = 100;
	private int STUDENT_NUM_ROOMS = 0;
	private int PAS_NUM_ROOMS = 0;
	
	private ArrayList<Room> rooms;
	private Mqtt mqtt;
	private Utils uts;
	private Database awsdb;
	private PeopleManager peopleManager;
	private Register reg;
	private Weather models;
	
	
	public Manager() {
		
		uts = Utils.getInstance();
		reg = Register.getInstance();
		
		if (MODE == 1 && NEW_PEOPLE == 1) {
			uts.generatePeople(PROFESSOR_NUM_ROOMS, STUDENT_NUM_ROOMS, PAS_NUM_ROOMS);
		}
		
		peopleManager = PeopleManager.getInstance();
		
		if (MODE == 0) {
			dumbScenario();
			terminate();
		}
		
		
		rooms = new ArrayList<Room>();
		models = Weather.getInstance();
		awsdb = Database.getInstance();
		mqtt = new Mqtt(this, awsdb);
		new DBListener(mqtt, awsdb.getConnectionListener());
		
		/**
		 * If the scenario is set to dumb, simulation can be performed without handling messages
		 */

		//sendInitialMessages();
	}
	
	/*private void sleep(int s) {
		try {
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/
	
	private void terminate() {
		System.exit(0);
	}

	
	private void simulate() {
		
		if (MODE == 2) {
			repeatSimulation();
			terminate();
		}
		
		if (RECORD_FILE == 1) peopleManager.enableRecordFile();
		
		while(Utils.CURRENT_STEP < Utils.STEPS) {
			//System.out.println("------------------------------- STEP " + Utils.CURRENT_STEP + " -------------------------------");
			if (Utils.CURRENT_STEP % 10 == 0) peopleManager.makeStep();
			for (Room r : rooms) r.fireRules();
			//System.out.println(reg.getNumHvacs() + " " + reg.getNumMaintHvacs());
			
			reg.computeConsumption();
			peopleManager.flushData(100, Utils.CURRENT_STEP);
			++Utils.CURRENT_STEP;
		}
		reg.writeConsumptionToFile();
		reg.printTotalConsumption();
		peopleManager.closeFile();
		terminate();
	}
	
	private void repeatSimulation() {
		PriorityQueue<Event> events = readEventFile();
		Event e;
		while(!events.isEmpty() && Utils.CURRENT_STEP < Utils.STEPS) {
			System.out.println("------------------------------- STEP " + Utils.CURRENT_STEP + " -------------------------------");
			while (!events.isEmpty() && events.peek().getStep() == Utils.CURRENT_STEP) {
				e = events.poll();
				peopleManager.executeAction(e.getPerson(), e.getAction());
			}
			for (Room r : rooms) r.fireRules();
			int cur = reg.computeConsumption();
			System.out.println("Current consumption: " + cur + " Watts");
			//sleep(1);
			
			++Utils.CURRENT_STEP;
		}
		reg.writeConsumptionToFile();
		reg.printTotalConsumption();
		terminate();
	}
	
	
	private void processMessage(String topic, String message, String location, String soID) {
		Room r = getRoom(location);
		ArrayList<String> types = uts.getTypesFromMessage(message);
		for (String type : types) {
			System.out.println(type);
			Sensor s = r.getSensor(soID, type);	
			
			/**
			 * Currently changed to fit the simulation
			 * 
			 * Initialise sensors with proper data instead of RNG data
			 */
			
			if (type.equals("temperature")) s.setValue(Double.toString(models.getCurrentEnvironmentalTemperature()));
			else if (type.equals("humidity")) s.setValue(Double.toString(models.getCurrentEnvironmentalHumidity()));
			else if (type.equals("luminosity")) s.setValue(Double.toString(models.getCurrentEnvironmentalLight()));
			else s.setValue(uts.getValueFromType(message, type));
		}
		if (allRoomsDefined() && peopleManager.isAllPeopleAssigned()) simulate();
	}
	
	private void sendInitialMessages() {
		ArrayList<String> ids = mqtt.getIds();
		System.out.println(ids.size());
		String xm1000Message = "{\"lastUpdate\":1441174408196,"
								+ "\"channels\":{"
						   	 	+ "\"humidity\":{\"current-value\":0},"
						   		+ "\"luminosity\":{\"current-value\":0},"
						   		+ "\"temperature\":{\"current-value\":0}}}";
		
		String computerMessage = "{\"lastUpdate\":1441174408196,"
				   			   		+ "\"channels\":{"
							   		+ "\"computer\":{\"current-value\":0}}}";
		
		for (String id : ids) {
			String topic = "API_KEY/" + id;
			String model = awsdb.getModel(id);
			switch(model) {
			case "XM1000":
				manageMessage(topic, xm1000Message);
				break;
			case "Computer":
				manageMessage(topic, computerMessage);
				break;
			}
		}
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
		Room r = new Room(location, awsdb, people, reg.getSensorsPerRoom());
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
				System.out.println("Finish");
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
	
	private String getRoomFromPeople(String name) {
		HashMap<String, ArrayList<Map.Entry<String, String>>> ppl = peopleManager.getPeopleFromFile();
		Iterator it = ppl.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, ArrayList<Entry<String, String>>> pair = (Entry<String, ArrayList<Entry<String, String>>>) it.next();
			for (int i = 0; i < pair.getValue().size(); ++i) {
				Map.Entry<String, String> vals = pair.getValue().get(i);
				if (vals.getKey().equals(name)) return pair.getKey();
			}
		}
		return null;
	}
	
	/**
	 * Calculates the number of hours that everyone has been inside the building.
	 * It assumes that everyone who enter eventually leaves.
	 */
	
	/**
	 * TODO: fix shared rooms, currently they are counted as separated rooms.
	 * Get the maximum working hours of every room with the aid of ppl map.
	 */
	
	private int checkWorkingHours() {
		HashMap<String, Integer> timesEnter = new HashMap<String, Integer>();
		HashMap<String, Integer> timesLeave = new HashMap<String, Integer>();
		try(BufferedReader br = new BufferedReader(new FileReader("res/events.txt"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] values = line.split(",");
	        	if (values[1].equals("enter")) {
	        		String room = getRoomFromPeople(values[0]);
	        		timesEnter.put(room, Math.min(timesEnter.containsKey(room) ? timesEnter.get(room) : 10000, Integer.parseInt(values[2])));
	        	}
	        	else if (values[1].equals("leave")) {
	        		String room = getRoomFromPeople(values[0]);
	        		timesLeave.put(room, Math.max(timesLeave.containsKey(room) ? timesLeave.get(room) : 0, Integer.parseInt(values[2])));
	        	}
	        }
	        int totalTime = 0;
	        Iterator<Entry<String, Integer>> it = timesEnter.entrySet().iterator();
	        while (it.hasNext()) {
	        	Map.Entry<String, Integer> pair = it.next();
	        	if (timesLeave.containsKey(pair.getKey())) {
	        		totalTime +=  timesLeave.get(pair.getKey()) - pair.getValue();
	        	}
	        }
	        
	        calculateConsumptionHistory(timesEnter, timesLeave);
	        
	        return totalTime;
	    } catch (IOException e) {
	    	System.out.println("ERROR: Unable to read events from file.");
	    	e.printStackTrace();
	    }
		return 0;
	}
	
	
	private void calculateConsumptionHistory(HashMap<String, Integer> timesEnter, HashMap<String, Integer> timesLeave) {
		try(PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter("res/cons.txt")))) {
			int roomCons = reg.computeConsumption();
			int activeRooms = 0;
			Iterator<Entry<String, Integer>> itEnter;
			for (int i = 0; i < uts.STEPS; ++i) {
				itEnter = timesEnter.entrySet().iterator();
				while (itEnter.hasNext()) {
					Map.Entry<String, Integer> pair = itEnter.next();
					if (pair.getValue() <= i && timesLeave.get(pair.getKey()) > i) {
						activeRooms++;
					}
				}
				wr.println(activeRooms * roomCons);
				//if (i%100 == 0) System.out.println(i + " " + activeRooms);
				activeRooms = 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dumbScenario() {
		
		/**
		 * In this scenario, everything is ON throughout the whole day
		 * It firstly calculates the time everyone is inside the building.
		 */
		
		reg.setNumComputers(1);
		reg.setNumHvacs(1);
		reg.setNumLights(1);
		int cons = reg.computeConsumption();
		int workingHours = checkWorkingHours();
		double totalCons = cons * (workingHours/360.0);
		System.out.println("Total dumb consumption: " + totalCons + " W");
	}
	
}