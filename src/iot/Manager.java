package iot;

import behaviour.Event;
import behaviour.PeopleManager;
import behaviour.Person;
import building.Building;
import building.Room;
import domain.Database;
import domain.Debugger;
import domain.Mqtt;
import domain.Utils;
import models.Weather;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Properties;

public class Manager {

	/**
	 * The record file saves all the actions in events.txt
	 */

	public static boolean LOG_EVENTS;

	private int GENERATE_PEOPLE;

    public static int MODE;
	public static int STEPS;
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
		//mqtt = new Mqtt(this, awsdb);
		
		building = uts.loadBuilding();
		if (MODE == 0 && GENERATE_PEOPLE == 1) uts.generatePeople();
		peopleManager = PeopleManager.getInstance();
		peopleManager.setBuilding(building);
		
		if (MODE == 0) simulate();
        else if (MODE == 1) repeatSimulation();
	}
	
	

	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("manager.properties");
			prop.load(is);
			STEPS = Integer.parseInt(prop.getProperty("steps"));
            MODE = Integer.parseInt(prop.getProperty("mode"));
			LOG_EVENTS = Boolean.parseBoolean(prop.getProperty("log_events"));
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
		while (CURRENT_STEP < STEPS) {
			if (Debugger.isEnabled() && CURRENT_STEP%500 == 0) Debugger.log("Step " + CURRENT_STEP);
			peopleManager.updateActions();
            building.fireRules();
			building.updateConsumption();
			++CURRENT_STEP;
		}
        if (Debugger.isEnabled()) Debugger.log("Consumption " + building.calculateFinalConsumption() + " kWh");
	}

	private void repeatSimulation() {
		PriorityQueue<Event> events = uts.fetchEventsFromFile();
        while (CURRENT_STEP < STEPS) {
            if (Debugger.isEnabled() && CURRENT_STEP%500 == 0) Debugger.log("Step " + CURRENT_STEP);
            peopleManager.executeActions();
            building.fireRules();
            building.updateConsumption();
            while (!events.isEmpty() && events.peek().getStep() == CURRENT_STEP) {
                Event e = events.poll();
                if (Debugger.isEnabled()) Debugger.log("Event " + e.getAction().toString() + " for " + e.getName());
                Person p = peopleManager.getPerson(e.getName());
                peopleManager.assignSpecificAction(p, e);

            }
            ++CURRENT_STEP;
        }
        if (Debugger.isEnabled()) Debugger.log("Consumption " + building.calculateFinalConsumption() + " kWh");
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