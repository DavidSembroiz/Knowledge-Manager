package behaviour;

import building.Building;
import domain.Debugger;
import iot.Manager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import static iot.Manager.LOG_EVENTS;


public class PeopleManager {

    public Person getPerson(String name) {
        for (Person p : people) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    public void executeActions() {
        for (Person p : people) {
            if (!p.isActing()) {
                if (p.getNextActionSteps() > 0) p.decreaseNextActionSteps();
                else if (p.getNextActionSteps() == 0) {
                    p.decreaseNextActionSteps();
                    executeAction(p);
                }
            }
            else if (p.isActing()){
                if (p.getRemainingSteps() > 0) p.decreaseRemainingSteps();
                else if (p.getRemainingSteps() == 0) {
                    p.decreaseRemainingSteps();
                    if (Debugger.isEnabled()) Debugger.log("Action finished " + p.getName() +
                                                           " " + p.getCurrentAction().toString());
                    p.setActing(false);
                }

            }

        }
    }

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
	private Building building;
	private Random rand;
	private PrintWriter writer;
	
	/**
	 * Initialize all the components required to manage people.
	 */
	
	private void initComponents() {
		rand = new Random();
		if (Manager.MODE == 0 && LOG_EVENTS) enableRecordFile();
		fetchProfiles();
		getPeopleFromFile();
	}

	public void fetchProfiles() {
		defaultProfiles = new ArrayList<>();
		for (Type t : Type.values()) {
			defaultProfiles.add(new UserProfile(t));
		}
	}
	
	
	/**
	 * Enables to recording of events that happens during the simulation to allow
	 * further repetitions.
	 */
	
	private void enableRecordFile() {
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter("res/events.log")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void flushData() {
		if (writer != null) writer.flush();
	}
	
	private UserProfile getProfile(String s) {
		for (UserProfile up : defaultProfiles) {
			if (up.getType().equals(Type.valueOf(s.toUpperCase()))) {
				return (UserProfile) up.clone();
			}
		}
		return null;
	}
	
	
	private void getPeopleFromFile() {
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

	public void updateActions() {
		for (Person p : people) {
			if (!p.isActing()) {
				if (p.getNextActionSteps() > 0) p.decreaseNextActionSteps();
				else if (p.getNextActionSteps() == 0) {
					
					/*
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
				else if(p.getRemainingSteps() == 0) {
                    p.decreaseRemainingSteps();
                    if (Debugger.isEnabled()) Debugger.log("Action finished " + p.getName() +
                            " " + p.getCurrentAction().toString());
                    assignNewAction(p);
                }
			}
		}
	}
	
	private void executeAction(Person p) {
		p.setActing(true);
        building.getRoom(p.getLocation()).shiftPerson(p);
        p.changeState();
	}

	public void assignSpecificAction(Person p, Event e) {
        String currentLoc = p.getLocation();
        p.assignAction(e.getAction(), e.getDest(), e.getNext(), e.getDuration());
        building.movePerson(p, currentLoc);
    }

	private void assignNewAction(Person p) {
		p.setActing(false);

		Action a = getNextAction(p);
        if (a == null) return;
		String dest = null, currentLoc = null;
		int next = 0, duration = 0;
		boolean assigned = false;
		if (a.equals(Action.MOVE)) {
			if (p.isInside()) {
				assigned = true;
				dest = getRandomDestination(p.getLocation());
				next = 1 + rand.nextInt(30);
				duration = p.getProfile().getRandomWalksDuration();
				currentLoc = p.getLocation();
			}
		}
		else if (a.equals(Action.ENTER)) {
			if (!p.isInside() && !p.hadEntered()) {
				assigned = true;
				dest = "inside";
				next = 1 + rand.nextInt(20);
				duration = 1;
				currentLoc = p.getLocation();
                p.sethadEntered(true);
			}
		}
		else if (a.equals(Action.EXIT)) {
			if (p.isInside()) {
				assigned = true;
				dest = "outside";
                next = 1;
				duration = 1 + rand.nextInt(20);
				currentLoc = p.getLocation();
			}
		}
		else if (a.equals(Action.LUNCH)) {
			if (p.isInside() && !p.hadLunch()) {
				assigned = true;
				dest = "salon";
				next = 1 + rand.nextInt(10);
				duration = p.getProfile().getLunchDuration();
				currentLoc = p.getLocation();
				p.setHadLunch(true);
			}
		}
		if (assigned) {
			p.assignAction(a, dest, next, duration);
			building.movePerson(p, currentLoc);
			if (LOG_EVENTS) {
				writer.println(Manager.CURRENT_STEP + "," +
							   p.getName() + "," +
							   p.getCurrentAction().toString() + "," +
						       p.getNextActionSteps() + "," +
						       p.getRemainingSteps() + "," +
						       p.getLocation());
				flushData();
			}
		}
	}

    private Action getNextAction(Person p) {
        UserProfile up = p.getProfile();
        if (up.getEntrance().triggerStatus(Manager.CURRENT_STEP)) return Action.ENTER;
        if (up.getRandomWalks().triggerStatus(Manager.CURRENT_STEP)) return Action.MOVE;
        if (up.getLunch().triggerStatus(Manager.CURRENT_STEP)) return Action.LUNCH;
        if (up.getExit().triggerStatus(Manager.CURRENT_STEP)) return Action.EXIT;
        return null;
    }

    private String getRandomDestination(String currentLoc) {
		String[] locs = building.getLocations();
		String nextLoc = locs[rand.nextInt(locs.length)];
		while (nextLoc.equals(currentLoc)) {
			nextLoc = locs[rand.nextInt(locs.length)];
		}
		return nextLoc;
	}

	public void setBuilding(Building b) {
		this.building = b;
	}
	
}
