package behaviour;

import java.util.ArrayList;
import java.util.Random;

import behaviour.Person.State;
import behaviour.Person.Type;

public class PeopleManager {
	
	/**
	 * TODO add Random Walks
	 */
	
	private ArrayList<Person> peopleOutside;
	private ArrayList<Person> peopleInside;
	private ArrayList<Person> peopleRandomWalks;
	private ArrayList<Person> peopleLunch;
	private ArrayList<UserProfile> profiles;
	
	public PeopleManager() {
		peopleOutside = new ArrayList<Person>();
		peopleInside = new ArrayList<Person>();
		peopleRandomWalks = new ArrayList<Person>();
		peopleLunch = new ArrayList<Person>();
		profiles = new ArrayList<UserProfile>();
		fillPeople();
		generateProfiles();
		
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
	}
}*/
		
		
	private void generateProfiles() {
		profiles.add(new UserProfile(Type.PROFESSOR));
		profiles.add(new UserProfile(Type.PAS));
		profiles.add(new UserProfile(Type.STUDENT));
	}
	
	private void fillPeople() {
		for (int i = 0; i < 10; ++i) {
			peopleOutside.add(new Person(getRandomName(), getRandomName(), State.OUTSIDE, Type.PROFESSOR));
		}
		for (int i = 0; i < 10; ++i) {
			peopleOutside.add(new Person(getRandomName(), getRandomName(), State.OUTSIDE, Type.PAS));
		}
		for (int i = 0; i < 10; ++i) {
			peopleOutside.add(new Person(getRandomName(), getRandomName(), State.OUTSIDE, Type.STUDENT));
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
}
