package behaviour;

import building.Building;
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




    /**
     * TODO Possible enhancement:
     * Modify the rooms so the number of people has a limit. When selecting the destination room,
     * create an index and, from this index, traverse the location vector to get the first available
     * room. If all rooms are full, avoid actuating.
     */


    public enum Action {
		MOVE, LUNCH, ENTER, EXIT, MEETING
	}
	
	public enum State {
		OUTSIDE, INSIDE, ROOM, SALON
	}
	
	enum Type {
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

	private void fetchProfiles() {
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

    public Person getPerson(String name) {
        for (Person p : people) {
            if (p.getName().equals(name)) return p;
        }
        return null;
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
                    /*if (Debugger.isEnabled()) Debugger.log("Action finished " + p.getName() +
                            " " + p.getCurrentAction().toString());*/
                    assignNewAction(p);
                }
            }
        }
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
                    /*if (Debugger.isEnabled()) Debugger.log("Action finished " + p.getName() +
                            " " + p.getCurrentAction().toString());*/
                    p.setActing(false);
                }

            }

        }
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
		people = new ArrayList<>();
		try {
			FileReader reader = new FileReader("./res/people.json");
			JSONObject root = (JSONObject) parser.parse(reader);
			
			JSONArray ppl = (JSONArray) root.get("people");
            for (Object per : ppl) {
                JSONObject person = (JSONObject) per;
                String type = (String) person.get("profile");
                Person p = new Person((String) person.get("name"), type, getProfile(type), generateUserParams());
                people.add(p);
            }
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	private UserParams generateUserParams() {
		Random r = new Random();
        double t = 21;
		//double t = 20 + (23 - 20) * r.nextDouble();
		double l = 100 + (800 - 100) * r.nextDouble();
		return new UserParams(t, l);
	}


	
	private void executeAction(Person p) {
		p.setActing(true);
        building.getRoom(p.getLocation()).shiftPerson(p);
        p.changeState();
	}

	public void assignSpecificAction(Event e) {
        Person p = getPerson(e.getName());
        String currentLoc = p.getLocation();
        p.assignAction(e.getAction(), e.getDest(), e.getNext(), e.getDuration());
        building.movePerson(p, currentLoc);
        if (!wasHavingLunch(p)) {
            building.unassignRoomElements(p, currentLoc);
            building.assignRoomElements(p, e.getDest());
        }
    }

	private void assignNewAction(Person p) {
		p.setActing(false);

		Action a = getNextAction(p);
        if (a == null) return;
		String dest = null;
		int next = 0, duration = 0;
		boolean assigned = false;
        if (a.equals(Action.MOVE)) {
            if (p.isInside()) {
                /*
                 * Return from lunch
                 */
                if (wasHavingLunch(p)) dest = p.getPastLocation();
                else dest = getDestination(p);
                if (dest == null) return;
                next = 1 + rand.nextInt(30);
                duration = p.getProfile().getRandomWalksDuration();
                assigned = true;
            }
        }
        if (a.equals(Action.MEETING)) {
            if (p.isInside()) {
                /*
                 * Return from lunch
                 */
                if (wasHavingLunch(p)) dest = p.getPastLocation();
                else dest = getSpecificDestination(p.getLocation(), building.getMeetingLocations());
                if (dest == null) return;
                /*
                 * From 5 to 15 minutes to arrive
                 */
                next = 30 + rand.nextInt(60);
                duration = p.getProfile().getRandomWalksDuration();
                assigned = true;
            }
        }
		else if (a.equals(Action.ENTER)) {
			if (!p.isInside() && !p.hadEntered()) {
				dest = "inside";
                /*
                 * From 10 to 30 minutes to enter, 1 step to execute action
                 */
				next = 60 + rand.nextInt(120);
				duration = 1;
                p.sethadEntered(true);
                assigned = true;
			}
		}
		else if (a.equals(Action.EXIT)) {
			if (p.isInside()) {
				dest = "outside";
                /*
                 * 1 step to exit the building, stays outside for 10 to 30 minutes
                 */
                next = 1;
				duration = 60 + rand.nextInt(120);
                assigned = true;
			}
		}
		else if (a.equals(Action.LUNCH)) {
			if (p.isInside() && !p.hadLunch()) {
				dest = "salon";
                /*
                 * lunch in the next 5 to 10 minutes
                 */
				next = 30 + rand.nextInt(30);
				duration = p.getProfile().getLunchDuration();
				p.setHadLunch(true);
                assigned = true;
			}
		}
		if (assigned) {
            p.setPastLocation(p.getLocation());
			p.assignAction(a, dest, next, duration);
			building.movePerson(p, p.getPastLocation());
            if (!wasHavingLunch(p)) {
                building.unassignRoomElements(p, p.getPastLocation());
                building.assignRoomElements(p, dest);
            }
			logEvent(p);
		}
	}

	public void logEvent(Person p) {
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

	private boolean wasHavingLunch(Person p) {
        return p.getCurrentAction().equals(Action.LUNCH);
    }


    private Action getNextAction(Person p) {
        UserProfile up = p.getProfile();
        if (p.getCurrentAction().equals(Action.LUNCH)) {
            Building.ROOM_TYPE type = building.getLocationType(p.getPastLocation());
            if (type.equals(Building.ROOM_TYPE.OFFICE) ||
            type.equals(Building.ROOM_TYPE.CLASSROOM)) return Action.MOVE;
            if (type.equals(Building.ROOM_TYPE.MEETING_ROOM)) return Action.MEETING;
        }
        if (up.getEntrance().triggerStatus(Manager.CURRENT_STEP)) return Action.ENTER;
        if (up.getRandomWalks().triggerStatus(Manager.CURRENT_STEP)) return Action.MOVE;
        if (up.getLunch().triggerStatus(Manager.CURRENT_STEP)) return Action.LUNCH;
        if (up.getExit().triggerStatus(Manager.CURRENT_STEP)) return Action.EXIT;
        if (up.getMeeting().triggerStatus(Manager.CURRENT_STEP)) return Action.MEETING;
        return null;
    }

    private String getSpecificDestination(String currentLoc, String[] locs) {
        if (locs.length == 1) return locs[0];
        int start = rand.nextInt(locs.length - 1);
        for (int i = start + 1; i != start; i = (i+1)%locs.length) {
            if (!locs[i].equals(currentLoc) &&
                    building.getRoom(locs[i]).isAvailable()) return locs[i];
        }
        return null;
    }

	private String getDestination(Person p) {
        String currentLoc = p.getLocation();
        String office = getSpecificDestination(currentLoc, building.getOfficeLocations());
        String classroom = getSpecificDestination(currentLoc, building.getClassromLocations());
        String meeting = getSpecificDestination(currentLoc, building.getMeetingLocations());
        double office_threshold = 0, class_threshold = 0, meeting_threshold = 0;

        /*
         * Thresholds must add 1
         */

        /**
         * TODO if some of the locations is null, discard and share the percentage to the rest of the locations
         */

        if (p.isProfessor()) {
            office_threshold = office == null ? 0 : 0.5;
            class_threshold = classroom == null ? 0 : 0.25;
            meeting_threshold = meeting == null ? 0 : 0.25;
        }
        else if (p.isStudent()) {
            office_threshold = office == null ? 0 : 0.2;
            class_threshold = classroom == null ? 0 : 0.6;
            meeting_threshold = meeting == null ? 0 : 0.2;
        }
        else {
            office_threshold = office == null ? 0 : 1/3;
            class_threshold = classroom == null ? 0 : 1/3;
            meeting_threshold = meeting == null ? 0 : 1/3;
        }


        double decide = rand.nextDouble();
        if (decide < office_threshold) return office;
        else if (decide < office_threshold + class_threshold) return classroom;
        else if (decide < office_threshold + class_threshold + meeting_threshold) return meeting;
        return null;
    }

	public void setBuilding(Building b) {
		this.building = b;
	}

    public ArrayList<Person> getPeople() {
        return people;
    }
}
