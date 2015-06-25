package behaviour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import behaviour.Person.State;
import behaviour.Person.Type;

public class PeopleManager {
	
	/**
	 * TODO add Random Walks
	 */
	
	private ArrayList<Person> unassigned;
	private ArrayList<Person> peopleOutside;
	private ArrayList<Person> peopleInside;
	private ArrayList<Person> peopleRandomWalks;
	private ArrayList<Person> peopleLunch;
	private ArrayList<UserProfile> profiles;
	
	public PeopleManager() {
		unassigned = new ArrayList<Person>();
		peopleOutside = new ArrayList<Person>();
		peopleInside = new ArrayList<Person>();
		peopleRandomWalks = new ArrayList<Person>();
		peopleLunch = new ArrayList<Person>();
		profiles = new ArrayList<UserProfile>();
		//createPeople();
		readPeopleFromFile();
		generateProfiles();
	}
	
	public void makeStep() {
		
		/**
		 * Disable multiple jumps in the same step
		 * For instance, it is not possible to do OUTSIDE -> INSIDE -> LUNCH in one step
		 */
		
		int t = 700;
		leaveBuilding(t);
		enterBuilding(t);
		goForLunch(t);
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
			if (getProfile(cur.getType()).getEntrance().triggerStatus(t)) {
				cur.setState(State.INSIDE);
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
			if (getProfile(cur.getType()).getLunch().triggerStatus(t)) {
				cur.setState(State.LUNCH);
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
			if (getProfile(cur.getType()).getLunchDuration().triggerStatus(t)) {
				cur.setState(State.INSIDE);
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
			if (getProfile(cur.getType()).getExit().triggerStatus(t)) {
				cur.setState(State.OUTSIDE);
				peopleOutside.add(peopleInside.remove(i));
			}
		}
		
		/**
		 * TODO add interaction RandomWalks -> Outside
		 */
	}
	
	/*public void checkRandomWalks(int t) {
	for (int i = 0; i < peopleInside.size(); ++i) {
		Person cur = peopleInside.get(i);
		if (getProfile(cur.getType()).getRandomWalks().triggerStatus(t)) {
			cur.setState(State.RANDOM_WALKS);
			peopleRandomWalks.add(peopleInside.remove(i));
		}
	}*/
	
		
	private void generateProfiles() {
		profiles.add(new UserProfile(Type.PROFESSOR));
		profiles.add(new UserProfile(Type.PAS));
		profiles.add(new UserProfile(Type.STUDENT));
	}
	
	private void createPeople() {
		for (int i = 0; i < 10; ++i) {
			unassigned.add(new Person(getRandomName(), "upc/campusnord/d6/0/008", State.UNASSIGNED, Type.PROFESSOR));
			unassigned.add(new Person(getRandomName(), "upc/campusnord/d6/0/008", State.UNASSIGNED, Type.PAS));
			unassigned.add(new Person(getRandomName(), "upc/campusnord/d6/0/008", State.UNASSIGNED, Type.STUDENT));
		}
	}
	
	private String getRandomName() {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
	}
	
	public void printPeople() {
		System.out.println("---------- OUTSIDE ----------");
		for (int i = 0; i < peopleOutside.size(); ++i) {
			Person p = peopleOutside.get(i);
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		System.out.println("---------- INSIDE ----------");
		for (int i = 0; i < peopleInside.size(); ++i) {
			Person p = peopleInside.get(i);
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		System.out.println("---------- WALKING ----------");
		for (int i = 0; i < peopleRandomWalks.size(); ++i) {
			Person p = peopleRandomWalks.get(i);
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		System.out.println("---------- LUNCH ----------");
		for (int i = 0; i < peopleLunch.size(); ++i) {
			Person p = peopleLunch.get(i);
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

	private void readPeopleFromFile() {
		try(BufferedReader br = new BufferedReader(new FileReader("res/people.txt"))) {
	        String line = br.readLine();
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
	
}
