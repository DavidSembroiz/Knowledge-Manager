package behaviour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import domain.Register;
import behaviour.Person.State;
import behaviour.Person.Type;

public class PeopleManager {
	
	private static PeopleManager instance = new PeopleManager();
	
	private PeopleManager() {
		initComponents();
	}
	
	public static PeopleManager getInstance() {
		return instance;
	}
	
	
	private ArrayList<Person> unassigned;
	private ArrayList<Person> peopleOutside;
	private ArrayList<Person> peopleInside;
	private ArrayList<Person> peopleRandomWalks;
	private ArrayList<Person> peopleLunch;
	private ArrayList<UserProfile> profiles;
	
	private void initComponents() {
		unassigned = new ArrayList<Person>();
		peopleOutside = new ArrayList<Person>();
		peopleInside = new ArrayList<Person>();
		peopleRandomWalks = new ArrayList<Person>();
		peopleLunch = new ArrayList<Person>();
		profiles = new ArrayList<UserProfile>();
		readPeopleFromFile();
		generateProfiles();
	}
	
	public void makeStep() {
		int t = 700;
		resetChanged();
		enterBuilding(t);
		goForRandomWalk(t);
		returnFromWalk(t);
		
		goForLunch(t);
		finishLunch(t);
		
		leaveBuilding(t);
		
		printPeople();
	}
	
	private UserProfile getProfile(Type t) {
		for (int i = 0; i < profiles.size(); ++i) {
			if (profiles.get(i).getType().equals(t)) {
				return profiles.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Checks whether the person is entering the building and changes his status to INSIDE
	 * 
	 * @param t
	 */
	
	public void enterBuilding(int t) {
		for (int i = peopleOutside.size() - 1; i >= 0; --i) {
			Person cur = peopleOutside.get(i);
			if (!cur.hasChanged() && getProfile(cur.getType()).getEntrance().triggerStatus(t)) {
				cur.setState(State.INSIDE);
				cur.setChanged(true);
				peopleInside.add(peopleOutside.remove(i));
			}
		}
		/**
		 * TODO add interaction RandomWalks -> Inside
		 */
	}
	
	
	/**
	 * Checks whether the person is going for lunch and changes his status to LUNCH
	 * 
	 * @param t
	 */
	
	public void goForLunch(int t) {
		for (int i = peopleInside.size() - 1; i >= 0; --i) {
			Person cur = peopleInside.get(i);
			if (!cur.hasChanged() && !cur.hasEaten() && getProfile(cur.getType()).getLunch().triggerStatus(t)) {
				cur.setState(State.LUNCH);
				cur.setChanged(true);
				cur.setEaten(true);
				peopleLunch.add(peopleInside.remove(i));
			}
		}
	}
	
	/**
	 * Checks whether the person has finished lunch and changes his status to INSIDE
	 * 
	 * @param t
	 */
	
	public void finishLunch(int t) {
		for (int i = peopleLunch.size() - 1; i >= 0; --i) {
			Person cur = peopleLunch.get(i);
			if (!cur.hasChanged() && getProfile(cur.getType()).getLunchDuration().triggerStatus(t)) {
				cur.setState(State.INSIDE);
				cur.setChanged(true);
				peopleInside.add(peopleLunch.remove(i));
			}
		}
	}
	
	/**
	 * Checks whether the person is leaving the building and changes his status to OUTSIDE
	 * 
	 * @param t
	 */
	
	public void leaveBuilding(int t) {
		for (int i = peopleInside.size() - 1; i >= 0; --i) {
			Person cur = peopleInside.get(i);
			if (!cur.hasChanged() && getProfile(cur.getType()).getExit().triggerStatus(t)) {
				cur.setState(State.OUTSIDE);
				cur.setChanged(true);
				peopleOutside.add(peopleInside.remove(i));
			}
		}
	}
	
	public void goForRandomWalk(int t) {
		for (int i = peopleInside.size() - 1; i >= 0; --i) {
			Person cur = peopleInside.get(i);
			if (!cur.hasChanged() && getProfile(cur.getType()).getRandomWalks().triggerStatus(t)) {
				cur.setState(State.RANDOM_WALKS);
				cur.setChanged(true);
				peopleRandomWalks.add(peopleInside.remove(i));
			}
		}
	}
	
	public void returnFromWalk(int t) {
		for (int i = peopleRandomWalks.size() - 1; i >= 0; --i) {
			Person cur = peopleRandomWalks.get(i);
			if (!cur.hasChanged() && getProfile(cur.getType()).getRandomWalksDuration().triggerStatus(t)) {
				cur.setState(State.INSIDE);
				cur.setChanged(true);
				peopleInside.add(peopleRandomWalks.remove(i));
			}
		}
	}
	
		
	private void generateProfiles() {
		profiles.add(new UserProfile(Type.PROFESSOR));
		profiles.add(new UserProfile(Type.PAS));
		profiles.add(new UserProfile(Type.STUDENT));
	}
	
	
	public void printPeople() {
		if (!peopleOutside.isEmpty()) System.out.println("---------- OUTSIDE ----------");
		for (Person p : peopleOutside) {
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		if (!peopleInside.isEmpty()) System.out.println("---------- INSIDE ----------");
		for (Person p : peopleInside) {
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		if (!peopleRandomWalks.isEmpty()) System.out.println("---------- WALKING ----------");
		for (Person p : peopleRandomWalks) {
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		if (!peopleLunch.isEmpty()) System.out.println("---------- LUNCH ----------");
		for (Person p : peopleLunch) {
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
	}

	public ArrayList<Person> assignPeopleToRoom(String location) {
		ArrayList<Person> people = new ArrayList<Person>();
		for (int i = unassigned.size() - 1; i >= 0; --i) {
			Person cur = unassigned.get(i);
			if (cur.getLocation().equals(location)) {
				cur.setState(State.OUTSIDE);
				peopleOutside.add(unassigned.remove(i));
				people.add(cur);
			}
		}
		return people;
	}
	
	private void resetChanged() {
		for (Person p : peopleOutside) p.setChanged(false);
		for (Person p : peopleInside) p.setChanged(false);
		for (Person p : peopleRandomWalks) p.setChanged(false);
		for (Person p : peopleLunch) p.setChanged(false);
	}

	private void readPeopleFromFile() {
		try(BufferedReader br = new BufferedReader(new FileReader("res/people.txt"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] values = line.split(",");
	        	Person p = new Person(values[0], values[1], State.UNASSIGNED, Type.valueOf(values[2].toUpperCase()));
	        	this.unassigned.add(p);
	        }
	        
	    } catch (IOException e) {
	    	System.out.println("ERROR: Unable to read people from file.");
	    	e.printStackTrace();
	    } catch(IllegalArgumentException e) {
	    	System.out.println("ERROR: Person does not contain a valid type.");
	    	e.printStackTrace();
	    }
	}

	public boolean isAllPeopleAssigned() {
		return unassigned.isEmpty();
	}
	
	public void executeAction(String person, String action) {
		if (action.equals("enter")) {
			for (Person p : peopleOutside) {
				if (p.getName().equals(person)) {
					p.setState(State.INSIDE);
					System.out.println(p.getName() + " has entered");
				}
			}
		}
		else if (action.equals("leave")) {
			for (Person p : peopleInside) {
				if (p.getName().equals(person)) {
					p.setState(State.OUTSIDE);
					System.out.println(p.getName() + " has left");
				}
			}
		}
		else if (action.equals("lunch")) {
			for (Person p : peopleInside) {
				if (p.getName().equals(person)) {
					p.setState(State.LUNCH);
					System.out.println(p.getName() + " is going for lunch");
				}
			}
		}
		else if (action.equals("returnLunch")) {
			for (Person p : peopleLunch) {
				if (p.getName().equals(person)) {
					p.setState(State.INSIDE);
					System.out.println(p.getName() + " has come back");
				}
			}
		}
	}
}
