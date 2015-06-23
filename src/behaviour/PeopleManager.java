package behaviour;

import java.util.ArrayList;
import java.util.Random;

import behaviour.Person.State;
import behaviour.Person.Type;

public class PeopleManager {
	
	private ArrayList<Person> people;
	private ArrayList<UserProfile> profiles;
	
	public PeopleManager() {
		people = new ArrayList<Person>();
		profiles = new ArrayList<UserProfile>();
		fillPeople();
		generateProfiles();
		
		int t = 300;
		checkEntrance(t);
		checkRandomWalks(t);
		checkExit(t);
	}
	
	private UserProfile getProfile(Type t) {
		for (int i = 0; i < profiles.size(); ++i) {
			if (profiles.get(i).getType().equals(t)) {
				return profiles.get(i);
			}
		}
		return null;
	}
	
	public void checkEntrance(int t) {
		for (int i = 0; i < people.size(); ++i) {
			Person cur = people.get(i);
			if (cur.getState().equals(State.OUTSIDE) || cur.getState().equals(State.RANDOM_WALKS)) {
				if (getProfile(cur.getType()).getEntrance().triggerStatus(t)) {
					System.out.println("Entered the room");
					cur.setState(State.INSIDE);
				}
			}
		}
	}
	
	public void checkRandomWalks(int t) {
		for (int i = 0; i < people.size(); ++i) {
			Person cur = people.get(i);
			if (cur.getState().equals(State.INSIDE)) {
				if (getProfile(cur.getType()).getRandomWalks().triggerStatus(t)) {
					System.out.println("Left for a random walk");
					cur.setState(State.RANDOM_WALKS);
				}
			}
		}
	}
	
	public void checkExit(int t) {
		for (int i = 0; i < people.size(); ++i) {
			Person cur = people.get(i);
			if (cur.getState().equals(State.INSIDE) || cur.getState().equals(State.RANDOM_WALKS)) {
				if (getProfile(cur.getType()).getExit().triggerStatus(t)) {
					System.out.println("Left the building");
					cur.setState(State.OUTSIDE);
				}
			}
		}
	}
	
	private void generateProfiles() {
		profiles.add(new UserProfile(Type.PROFESSOR));
		profiles.add(new UserProfile(Type.PAS));
		profiles.add(new UserProfile(Type.STUDENT));
	}
	
	private void fillPeople() {
		for (int i = 0; i < 20; ++i) {
			people.add(new Person(getRandomName(), getRandomName(), State.OUTSIDE, Type.PROFESSOR));
		}
		for (int i = 0; i < 20; ++i) {
			people.add(new Person(getRandomName(), getRandomName(), State.OUTSIDE, Type.PAS));
		}
		for (int i = 0; i < 20; ++i) {
			people.add(new Person(getRandomName(), getRandomName(), State.OUTSIDE, Type.STUDENT));
		}
	}
	
	private String getRandomName() {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
	}
	
	public void printPeople() {
		for (int i = 0; i < people.size(); ++i) {
			Person p = people.get(i);
			System.out.println("Name " + p.getName());
			System.out.println("State " + p.getState());
		}
	}
}
