package iot;

import behaviour.Event;
import behaviour.PeopleManager;
import behaviour.Person;
import building.Building;
import building.Room;
import data.EventsDB;
import data.IdentifierDB;
import domain.CustomFileWriter;
import domain.Debugger;
import domain.Mqtt;
import domain.Utils;
import model.ModelManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class Manager {

	/**
	 * The record file saves all the actions in events.txt
	 */

	public static boolean LOG_EVENTS;

    /**
     * Resolution of consumption file: value/24 equals the number of samples/hour
     */

    public static int CONSUMPTION_RESOLUTION = 24;

    /**
     * Control the generation of new people
     */

    private int GENERATE_PEOPLE;
    public static int NUM_PROFESSORS, NUM_STUDENTS, NUM_PAS;

    /**
     * MODE 0: smart simulation
     * MODE 1: repeat smart simulation
     * MODE 2: base simulation
     */

    public static int MODE;

    /**
     * Simulation steps
     */

    private static int STEPS;
	public static int CURRENT_STEP;

    private Building building;
	private Mqtt mqtt;
	private Utils uts;
	private IdentifierDB awsdb;
	private EventsDB eventsdb;
	private PeopleManager peopleManager;
	private ModelManager models;
	
	private CustomFileWriter consumption_writer;
	
	public Manager() {
		CURRENT_STEP = 0;
		loadProperties();
		uts = Utils.getInstance();
		awsdb = IdentifierDB.getInstance();
		eventsdb = EventsDB.getInstance();
		models = ModelManager.getInstance();
		mqtt = new Mqtt(this, awsdb);
		
		building = uts.loadBuilding();
		if (MODE == 0 && GENERATE_PEOPLE == 1) uts.generatePeople();
		peopleManager = PeopleManager.getInstance();
		peopleManager.setBuilding(building);

        consumption_writer = new CustomFileWriter("./res/results/consumption_" + MODE + ".log");

        switch (MODE) {
            case 0:
                simulate();
                break;
            case 1:
                repeatSimulation();
                break;
            case 2:
                baseSimulation();
                break;
        }
	}

	private void loadProperties() {
		Properties prop = new Properties();
		try {
			InputStream is = new FileInputStream("manager.properties");
			prop.load(is);
			STEPS = Integer.parseInt(prop.getProperty("steps"));
            MODE = Integer.parseInt(prop.getProperty("mode"));
			LOG_EVENTS = Boolean.parseBoolean(prop.getProperty("log_events"));
			GENERATE_PEOPLE = Integer.parseInt(prop.getProperty("generate_people"));
            NUM_PROFESSORS = Integer.parseInt(prop.getProperty("professors"));
            NUM_STUDENTS = Integer.parseInt(prop.getProperty("students"));
            NUM_PAS = Integer.parseInt(prop.getProperty("pas"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void manageMessage(String topic, String message) {
        String soID = uts.extractIdFromTopic(topic);
        String location = awsdb.getLocation(soID);
        if (location != null) processMessage(message, location, soID);

        else {
			/*
			 * Unable to query database, handle messages
			 */
            System.out.println("Unable to query room number, message discarded");
        }
    }
	
	private void processMessage(String message, String location, String soID) {
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
				
				/*
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
            switch (type) {
                case "temperature":
                    s.setValue(Double.toString(16));
                    break;
                case "humidity":
                    s.setValue(Double.toString(models.getCurrentEnvironmentalHumidity()));
                    break;
                case "luminosity":
                    s.setValue(Double.toString(models.getCurrentEnvironmentalLight()));
                    break;
                case "airquality":
                    s.setValue(Double.toString(models.getCurrentAirQuality()));
                default:
                    String val = uts.getValueFromType(message, type);
                    s.setValue(val);
                    break;
            }
		}
	}

	private void baseSimulation() {
        /*
         * During normal simulation, all elements are ON between working hours
         */
        while (CURRENT_STEP < STEPS) {
            ArrayList<Event> events = uts.fetchEventsFromFile();
            while (CURRENT_STEP < STEPS) {
                peopleManager.executeActions();
                while (!events.isEmpty() && events.get(0).getStep() == CURRENT_STEP) {
                    Event e = events.remove(0);
                    peopleManager.assignSpecificAction(e);

                }
                building.fireRules();
                building.updateConsumption();
                ++CURRENT_STEP;
            }

            if (Debugger.isEnabled()) Debugger.log("Consumption " + building.calculateAccumulatedConsumption() + " kWh");
            writeHourlyConsumption();
        }
    }

    private void simulate() {

		while (CURRENT_STEP < STEPS) {
            peopleManager.updateActions();
            if (CURRENT_STEP == 6840) emptyBuilding();
            building.fireRules();
			building.updateConsumption();
			++CURRENT_STEP;
		}
		if (Debugger.isEnabled()) Debugger.log("Consumption " + building.calculateAccumulatedConsumption() + " kWh");
        writeHourlyConsumption();
	}

    private void repeatSimulation() {
		ArrayList<Event> events = uts.fetchEventsFromFile();
        while (CURRENT_STEP < STEPS) {
            peopleManager.executeActions();
            while (!events.isEmpty() && events.get(0).getStep() == CURRENT_STEP) {
                peopleManager.assignSpecificAction(events.remove(0));
            }
            building.fireRules();
            building.updateConsumption();
            ++CURRENT_STEP;
        }
        if (Debugger.isEnabled()) Debugger.log("Consumption " + building.calculateAccumulatedConsumption() + " kWh");
        writeHourlyConsumption();
	}

    private void emptyBuilding() {
        Random rand = new Random();
        peopleManager.getPeople().stream().filter(Person::isInside).forEach(p -> {
            Event leave = new Event(CURRENT_STEP, p.getName(), PeopleManager.Action.EXIT,
                    PeopleManager.State.OUTSIDE.toString().toLowerCase(), rand.nextInt(STEPS - CURRENT_STEP) + 1, 1);
            peopleManager.assignSpecificAction(leave);
            peopleManager.logEvent(p);
        });
    }


    private void writeHourlyConsumption() {
        double cons[] = building.getHourlyConsumption();
        for (double con : cons) {
            consumption_writer.write(Double.toString(con));
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