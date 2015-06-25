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
		int t = 700;
		resetChanged();
		leaveBuilding(t);
		enterBuilding(t);
		goForLunch(t);
		finishLunch(t);
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
			if (!cur.isChanged() && getProfile(cur.getType()).getEntrance().triggerStatus(t)) {
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
			if (!cur.isChanged() && getProfile(cur.getType()).getLunch().triggerStatus(t)) {
				cur.setState(State.LUNCH);
				cur.setChanged(true);
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
			if (!cur.isChanged() && getProfile(cur.getType()).getLunchDuration().triggerStatus(t)) {
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
			if (!cur.isChanged() && getProfile(cur.getType()).getExit().triggerStatus(t)) {
				cur.setState(State.OUTSIDE);
				cur.setChanged(true);
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
	
	
	/*private String getRandomName() {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
	}*/
	
	public void printPeople() {
		System.out.println("---------- OUTSIDE ----------");
		for (Person p : peopleOutside) {
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		System.out.println("---------- INSIDE ----------");
		for (Person p : peopleInside) {
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		System.out.println("---------- WALKING ----------");
		for (Person p : peopleRandomWalks) {
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
		System.out.println("---------- LUNCH ----------");
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

	public boolean isAllPeopleAssigned() {
		return unassigned.isEmpty();
	}
	
}
