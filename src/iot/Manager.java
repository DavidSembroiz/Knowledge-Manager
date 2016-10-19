package iot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Properties;

import behaviour.PeopleManager;
import building.Building;
import building.Room;
import domain.Database;
import domain.Debugger;
import domain.Mqtt;
import domain.Utils;
import models.Weather;

public class Manager {
	
	
	/**
	 * The record file saves all the actions in events.txt
	 */
	
	private int EVENTS_FILE;
	
	private int GENERATE_PEOPLE;
	
	private int STEPS;
	public static int CURRENT_STEP;

	
	private Properties prop;
	
	private Building building;
	private Mqtt mqtt;
	private Utils uts;
	private Database awsdb;
	private PeopleManager peopleManager;
	private Weather models;
	
	
	
	public Manager() {
		CURRENT_STEP = 0;
		loadProperties();
		uts = Utils.getInstance();
		awsdb = Database.getInstance();
		models = Weather.getInstance();
		mqtt = new Mqtt(this, awsdb);
		
		building = uts.loadBuilding();
		if (GENERATE_PEOPLE == 1) uts.generatePeople();
		peopleManager = PeopleManager.getInstance();
		peopleManager.setBuilding(building);
		
		simulate();
	}
	
	

	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("manager.properties");
			prop.load(is);
			STEPS = Integer.parseInt(prop.getProperty("steps"));
			EVENTS_FILE = Integer.parseInt(prop.getProperty("events_file"));
			GENERATE_PEOPLE = Integer.parseInt(prop.getProperty("generate_people"));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private void processMessage(String topic, String message, String location, String soID) {
		Room r = building.getRoom(location);
		if (r != null) {
			ArrayList<String> types = uts.getTypesFromMessage(message);
			for (String type : types) {
				System.out.println(type);
				if (!r.sensorExists(soID, type)) {
					Sensor s = r.fetchSensor(awsdb.getModel(soID), type);
					s.setSoID(soID);
				}
				Sensor s = r.getSensor(soID, type);
				printBuilding();
				
				/**
				 * Currently changed to fit the simulation
				 * 
				 * Initialize sensors with proper data instead of RNG data
				 */
				
				initializeValue(s, message);
			}
		}
		else {
			System.out.println("Unable to find the ROOM, message discarded");
		}
	}
	
	private void initializeValue(Sensor s, String message) {
		if (s != null) {
			String type = s.getType();
			if (type.equals("temperature")) s.setValue(Double.toString(16));
			else if (type.equals("humidity")) s.setValue(Double.toString(models.getCurrentEnvironmentalHumidity()));
			else if (type.equals("luminosity")) s.setValue(Double.toString(models.getCurrentEnvironmentalLight()));
			else {
				String val = uts.getValueFromType(message, type);
				s.setValue(val);
			}
		}
	}



	private void simulate() {
		peopleManager.enterPeople();
		while (CURRENT_STEP < 1000) {
			if (Debugger.isEnabled()) Debugger.log("Step " + CURRENT_STEP);
			peopleManager.updateActions();
			building.updateConsumption();
			++CURRENT_STEP;
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
	
	
	/**
	 * Calculates the number of hours that everyone has been inside the building.
	 * It assumes that everyone who enter eventually leaves.
	 */
	
	/*private int checkWorkingHours() {
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
	}*/
	

	private void printBuilding() {
		ArrayList<Room> rooms = building.getRooms();
		for (Room r : rooms) {
			System.out.println("ROOM " + r.getLocation());
			System.out.println("Size " + r.getSize());
			ArrayList<Sensor> sensors = r.getSensors();
			for (Sensor s : sensors) {
				System.out.println("Sensor " + s.getType() + " " + s.getSoID() + " " + s.getValue());
			}	
		}
	}
	
}