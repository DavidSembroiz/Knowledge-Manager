package behaviour;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class PeopleManager {
	
	public enum Action {
		MOVE, LUNCH, ENTER, EXIT
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
	
	private boolean writeToFile = false;
	private PrintWriter writer;
	
	/**
	 * Initialize all the components required to manage people.
	 */
	
	private void initComponents() {
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
			if (p.getNextActionSteps() > 0) p.decreaseNextActionSteps();
			else if (!p.isActing() && p.getNextActionSteps() == 0) {
				executeAction(p);
			}
			else {
				if (p.getRemainingSteps() > 0) p.decreaseRemainingSteps();
				else assignNewAction(p);
			}
		}
	}
	
	private void executeAction(Person p) {
		p.setActing(true);
		// FINISH
		
	}

	private void assignNewAction(Person p) {
		p.setActing(false);
		/**
		 * Randomly get a new action (MOVE, LUNCH, ENTER...)
		 * Assign Person p to new destination (room)
		 * Assign action time and duration (next and remaining)
		 * 
		 */
	}
	
}
