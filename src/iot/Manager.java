package iot;

import behaviour.Event;
import behaviour.PeopleManager;
import behaviour.Person;
import building.Building;
import building.BuildingGenerator;
import building.Room;
import data.*;
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

import static java.lang.System.exit;

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

    /**
     * Building parameters
     */

    private static Boolean NEW_BUILDING;
    private static String BUILDING;
    private static int OFFICE_ROOMS, MEETING_ROOMS, CLASS_ROOMS;

    private Building building;
	private Mqtt mqtt;
	private Utils uts;
	private IdentifierDB iddb;
	private PeopleManager peopleManager;
	private ModelManager models;

    private EventsDB eventsdb;
    private BuildingsDB buildingsdb;
    private SchedulesDB schedulesdb;
    private ComfortDB comfortdb;
	


	
	public Manager() {
		CURRENT_STEP = 0;
        loadSimulationProperties();
		uts = Utils.getInstance();
		iddb = IdentifierDB.getInstance();
		eventsdb = EventsDB.getInstance();
		buildingsdb = BuildingsDB.getInstance();
		schedulesdb = SchedulesDB.getInstance();
        comfortdb = ComfortDB.getInstance();
		models = ModelManager.getInstance();
        //mqtt = new Mqtt(this, iddb);

		if (NEW_BUILDING) buildingsdb.save(new BuildingGenerator(BUILDING, OFFICE_ROOMS, MEETING_ROOMS, CLASS_ROOMS).generateBuilding());

        building = buildingsdb.fetchData();
        if (MODE == 3) building.fillSchedules();
        if (MODE == 0 && GENERATE_PEOPLE == 1) uts.generatePeople();
		peopleManager = PeopleManager.getInstance();
		peopleManager.setBuilding(building);


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
            case 3:
                learningSimulation();
                break;
            default:
                System.out.println("Case not defined.");
                exit(-1);
        }
	}


    public static String getBuildingName() {
	    return BUILDING;
    }

	private void loadSimulationProperties() {
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
            NEW_BUILDING = Boolean.parseBoolean(prop.getProperty("new_building"));
            BUILDING = prop.getProperty("building_name");
            OFFICE_ROOMS = Integer.parseInt(prop.getProperty("office_rooms"));
            MEETING_ROOMS = Integer.parseInt(prop.getProperty("meeting_rooms"));
            CLASS_ROOMS = Integer.parseInt(prop.getProperty("class_rooms"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void manageMessage(String topic, String message) {
        String soID = uts.extractIdFromTopic(topic);
        String location = iddb.getLocation(soID);
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
					Sensor s = r.fetchSensor(iddb.getModel(soID), type);
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


    private void simulate() {
		while (CURRENT_STEP < STEPS) {
            peopleManager.updateActions();
            peopleManager.computeComforts();
            if (CURRENT_STEP == 6840) emptyBuilding();
            building.fireRules();
			building.updateConsumption();
			++CURRENT_STEP;
		}
		Debugger.log("Consumption " + building.calculateAccumulatedConsumption() + " kWh");
        finish();
	}

    private void repeatSimulation() {
		ArrayList<Event> events = eventsdb.fetchData();
        while (CURRENT_STEP < STEPS) {
            peopleManager.executeActions();
            peopleManager.computeComforts();
            while (!events.isEmpty() && events.get(0).getStep() == CURRENT_STEP) {
                peopleManager.assignSpecificAction(events.remove(0));
            }
            building.fireRules();
            building.updateConsumption();
            ++CURRENT_STEP;
        }
        peopleManager.saveComforts();
        building.saveSchedules();
        Debugger.log("Consumption " + building.calculateAccumulatedConsumption() + " kWh");
        finish();
	}

    private void baseSimulation() {
        /*
         * During normal simulation, all elements are ON between working hours
         */
        while (CURRENT_STEP < STEPS) {
            ArrayList<Event> events = eventsdb.fetchData();
            while (CURRENT_STEP < STEPS) {
                peopleManager.executeActions();
                peopleManager.computeComforts();
                while (!events.isEmpty() && events.get(0).getStep() == CURRENT_STEP) {
                    Event e = events.remove(0);
                    peopleManager.assignSpecificAction(e);

                }
                building.fireRules();
                building.updateConsumption();
                ++CURRENT_STEP;
            }
            Debugger.log("Consumption " + building.calculateAccumulatedConsumption() + " kWh");
            finish();
        }
    }

    private void learningSimulation() {
        ArrayList<Event> events = eventsdb.fetchData();
        //peopleManager.fetchComforts();
        while (CURRENT_STEP < STEPS) {
            peopleManager.executeActions();
            building.performActuations();
            peopleManager.computeComforts();
            while (!events.isEmpty() && events.get(0).getStep() == CURRENT_STEP) {
                peopleManager.assignSpecificAction(events.remove(0));
            }
            building.fireRules();
            building.updateConsumption();
            ++CURRENT_STEP;
        }
        peopleManager.saveComforts();
        //building.saveSchedules();
        Debugger.log("Consumption " + building.calculateAccumulatedConsumption() + " kWh");
        finish();
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

    private void finish() {
        eventsdb.shutdown();
        buildingsdb.shutdown();
        schedulesdb.shutdown();
        comfortdb.shutdown();
        mqtt.disconnect();
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