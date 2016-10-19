package behaviour;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import building.Building;


public class PeopleManager {
	
	public enum Action {
		MOVE, LUNCH, ENTER, EXIT;
		
		private static final Action[] ACTIONS = values();
		private static final Random rand = new Random();
		
		public static Action randomAction() {
			return ACTIONS[rand.nextInt(ACTIONS.length)];
		}
		
	}
	
	public enum State {
		OUTSIDE, INSIDE, ROOM, MEETING, SALON
	}
	
	public enum Type {
		PROFESSOR, PAS, STUDENT
	}
	
	private static PeopleManager instance = new PeopleManager();
	
	
	private PeopleManager() {
		initComponents();
	}
	
	public static PeopleManager getInstance() {
		return instance;
	}
	
	private ArrayList<Person> people;
	private ArrayList<UserProfile> defaultProfiles;
	private Building building;
	private Random rand;
	
	private boolean writeToFile = false;
	private PrintWriter writer;
	
	/**
	 * Initialize all the components required to manage people.
	 */
	
	private void initComponents() {
		rand = new Random();
		fetchProfiles();
		getPeopleFromFile();
	}

	public void fetchProfiles() {
		defaultProfiles = new ArrayList<UserProfile>();
		for (Type t : Type.values()) {
			defaultProfiles.add(new UserProfile(t));
		}
	}
	
	
	/**
	 * Enables to recording of events that happens during the simulation to allow
	 * further repetitions.
	 */
	
	public void enableRecordFile() {
		writeToFile = true;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter("res/events.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void flushData(int steps, int current) {
		if (writer != null && current % steps == 0) writer.flush();
	}
	
	public UserProfile getProfile(String s) {
		for (UserProfile up : defaultProfiles) {
			if (up.getType().equals(Type.valueOf(s.toUpperCase()))) {
				return (UserProfile) up.clone();
			}
		}
		return null;
	}
	
	
	public void getPeopleFromFile() {
		JSONParser parser = new JSONParser();
		people = new ArrayList<Person>();
		try {
			FileReader reader = new FileReader("./res/people.json");
			JSONObject root = (JSONObject) parser.parse(reader);
			
			JSONArray ppl = (JSONArray) root.get("people");
			for (int i = 0; i < ppl.size(); ++i) {
				JSONObject person = (JSONObject) ppl.get(i);
				Person p = new Person((String) person.get("name"),
						              getProfile((String) person.get("profile")),
						              generateUserParams());
				people.add(p);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	private UserParams generateUserParams() {
		Random r = new Random();
		double t = 20 + (23 - 20) * r.nextDouble();
		double l = 100 + (800 - 100) * r.nextDouble();
		return new UserParams(t, l);
	}

	public void closeFile() {
		if (writer != null) writer.close();
	}

	public void updateActions() {
		for (Person p : people) {
			if (!p.isActing()) {
				if (p.getNextActionSteps() > 0) p.decreaseNextActionSteps();
				else if (p.getNextActionSteps() == 0) {
					
					/**
					 * Action is executed and nextActionSteps is set to -1
					 */
					p.decreaseNextActionSteps();
					executeAction(p);
				}
				else if (p.getNextActionSteps() < 0) {
					assignNewAction(p);
				}
			}
			else if (p.isActing()) {
				if (p.getRemainingSteps() > 0) p.decreaseRemainingSteps();
				else if(p.getRemainingSteps() == 0) assignNewAction(p);
			}
		}
	}
	
	private void executeAction(Person p) {
		p.setActing(true);
		p.changeState();
		
	}

	private void assignNewAction(Person p) {
		p.setActing(false);
		/**
		 * Randomly get a new action (MOVE, LUNCH, ENTER...)
		 * Assign Person p to new destination (room)
		 * Assign action time and duration (next and remaining)
		 * 
		 */
		Action a = Action.randomAction();
		if (a.equals(Action.MOVE)) {
			if (p.isInside()) {
				String dest = getRandomDestination();
				int next = 1 + rand.nextInt(1);
				int duration = 1 + rand.nextInt(1);
				String currentLoc = p.getLocation();
				p.assignAction(a, dest, next, duration);
				building.movePerson(p, currentLoc);
			}
		}
		else if (a.equals(Action.ENTER)) {
			if (!p.isInside()) {
				int next = 1 + rand.nextInt(1);
				int duration = 1;
				p.assignAction(a, "inside", next, duration);
				building.movePerson(p, "outside");
			}
		}
		else if (a.equals(Action.EXIT)) {
			if (p.isInside()) {
				int next = 1 + rand.nextInt(1);
				int duration = 1;
				p.assignAction(a, "outside", next, duration);
				building.movePerson(p, "inside");
			}
		}
		else if (a.equals(Action.LUNCH)) {
			if (p.isInside() && !p.hadLunch()) {
				int next = 1 + rand.nextInt(1);
				int duration = 1 + rand.nextInt(1);
				String currentLoc = p.getLocation();
				p.setHadLunch(true);
				p.assignAction(a, "salon", next, duration);
				building.movePerson(p, currentLoc);
			}
		}
	}

	private String getRandomDestination() {
		String[] locs = building.getLocations();
		return locs[rand.nextInt(locs.length)];
	}

	public void enterPeople() {
		for (Person p : people) {
			int next = 1 + rand.nextInt(1);
			int duration = 1;
			p.assignAction(Action.ENTER, "inside", next, duration);
			building.movePerson(p, "outside");
		}
	}

	public void setBuilding(Building b) {
		this.building = b;
	}
	
}
