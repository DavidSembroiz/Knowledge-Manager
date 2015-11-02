package behaviour;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import behaviour.Person.State;
import behaviour.Person.Type;
import domain.Utils;

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
	
	private boolean writeToFile = false;
	private PrintWriter writer;
	
	private void initComponents() {
		unassigned = new ArrayList<Person>();
		peopleOutside = new ArrayList<Person>();
		peopleInside = new ArrayList<Person>();
		peopleRandomWalks = new ArrayList<Person>();
		peopleLunch = new ArrayList<Person>();
		profiles = new ArrayList<UserProfile>();
		readPeople();
		generateProfiles();
	}
	
	public void makeStep() {
		int t = Utils.CURRENT_STEP;
		resetChanged();
		
		enterBuilding(t);
		
		goForRandomWalk(t);
		returnFromWalk(t);
		
		goForLunch(t);
		finishLunch(t);
		
		leaveBuilding(t);
		
		//printPeople();
	}
	
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
			if (!cur.hasChanged() && !cur.hasEntered() && getProfile(cur.getType()).getEntrance().triggerStatus(t)) {
				cur.setState(State.INSIDE);
				cur.setChanged(true);
				cur.setEntered(true);
				peopleInside.add(peopleOutside.remove(i));
				if (writeToFile) writer.println(cur.getName() + ",enter," + Utils.CURRENT_STEP);
			}
		}
		//System.out.println("People Outside: " + peopleOutside.size());
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
				cur.setLunchReturn(Utils.CURRENT_STEP, getProfile(cur.getType()).getLunchDuration());
				peopleLunch.add(peopleInside.remove(i));
				if (writeToFile) writer.println(cur.getName() + ",lunch," + Utils.CURRENT_STEP);
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
			//if (!cur.hasChanged() && getProfile(cur.getType()).getLunchDuration().triggerStatus(t)) {
			if (!cur.hasChanged() && cur.getLunchReturn() <= Utils.CURRENT_STEP) {
				cur.setState(State.INSIDE);
				cur.setChanged(true);
				peopleInside.add(peopleLunch.remove(i));
				if (writeToFile) writer.println(cur.getName() + ",returnLunch," + Utils.CURRENT_STEP);
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
			if (!cur.hasChanged() && getProfile(cur.getType()).getExit().triggerStatusWithPrint(t)) {
				cur.setState(State.OUTSIDE);
				cur.setChanged(true);
				peopleOutside.add(peopleInside.remove(i));
				if (writeToFile) writer.println(cur.getName() + ",leave," + Utils.CURRENT_STEP);
			}
		}
	}
	
	public void goForRandomWalk(int t) {
		for (int i = peopleInside.size() - 1; i >= 0; --i) {
			Person cur = peopleInside.get(i);
			if (!cur.hasChanged() && cur.canRandomWalk() && getProfile(cur.getType()).getRandomWalks().triggerStatus(t)) {
				cur.setState(State.RANDOM_WALKS);
				cur.setChanged(true);
				cur.setRandomWalksReturn(Utils.CURRENT_STEP, getProfile(cur.getType()).getRandomWalksDuration());
				cur.addRandomWalk();
				peopleRandomWalks.add(peopleInside.remove(i));
				if (writeToFile) writer.println(cur.getName() + ",randomWalk," + Utils.CURRENT_STEP);
			}
		}
	}
	
	public void returnFromWalk(int t) {
		for (int i = peopleRandomWalks.size() - 1; i >= 0; --i) {
			Person cur = peopleRandomWalks.get(i);
			//if (!cur.hasChanged() && getProfile(cur.getType()).getRandomWalksDuration().triggerStatus(t)) {
			if (!cur.hasChanged() && cur.getRandomWalksReturn() <= Utils.CURRENT_STEP) {
				cur.setState(State.INSIDE);
				cur.setChanged(true);
				peopleInside.add(peopleRandomWalks.remove(i));
				if (writeToFile) writer.println(cur.getName() + ",returnRandomWalk," + Utils.CURRENT_STEP);
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
	
	public HashMap<String, ArrayList<Map.Entry<String, String>>> getPeopleFromFile() {
		HashMap<String, ArrayList<Map.Entry<String,String>>> ppl = new HashMap<String, ArrayList<Map.Entry<String, String>>>();
		try(BufferedReader br = new BufferedReader(new FileReader("res/people.txt"))) {
	        String line = br.readLine();
	        String[] values = line.split(",");
	        String currentRoom = values[1];
	        ArrayList<Map.Entry<String, String>> names = new ArrayList<Map.Entry<String, String>>();
	        Map.Entry<String, String> entry =  new AbstractMap.SimpleEntry<String, String>(values[0], values[2]);
	        names.add(entry);
	        while ((line = br.readLine()) != null) {
	        	values = line.split(",");
	        	if (values[1].equals(currentRoom)) {
	        		names.add(new AbstractMap.SimpleEntry<String, String>(values[0], values[2]));
	        	}
	        	else {
	        		ppl.put(currentRoom, names);
	        		currentRoom = values[1];
	        		names = new ArrayList<Map.Entry<String, String>>();
	        		names.add(new AbstractMap.SimpleEntry<String, String>(values[0], values[2]));
	        	}
	        }
	        return ppl;
	    } catch (IOException e) {
	    	System.err.println("ERROR: Unable to read people from file.");
	    	e.printStackTrace();
	    } catch(IllegalArgumentException e) {
	    	System.err.println("ERROR: Person does not contain a valid type.");
	    	e.printStackTrace();
	    }
		return ppl;
	}
	
	/*public void printHash() {
		HashMap<String, ArrayList<Entry<String, String>>> ppl = getPeopleFromFile();
		Iterator it = ppl.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			it.remove();
		}
	}*/
	
	private void readPeople() {
		HashMap<String, ArrayList<Entry<String, String>>> ppl = getPeopleFromFile();
		Iterator<?> it = ppl.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<String, ArrayList<Entry<String, String>>> pair = (Entry<String, ArrayList<Entry<String, String>>>) it.next();
			for (int i = 0; i < pair.getValue().size(); ++i) {
				Map.Entry<String, String> vals = pair.getValue().get(i);
				Person p = new Person(vals.getKey(), pair.getKey(), State.UNASSIGNED, Type.valueOf(vals.getValue().toUpperCase()));
				this.unassigned.add(p);
			}
		}
	}

	
	/*private void readPeopleFromFile() {
		try(BufferedReader br = new BufferedReader(new FileReader("res/people.txt"))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] values = line.split(",");
	        	System.out.println(values[1] + " " + values[0] + " " + values[2]);
	        	Person p = new Person(values[0], values[1], State.UNASSIGNED, Type.valueOf(values[2].toUpperCase()));
	        	this.unassigned.add(p);
	        }
	    } catch (IOException e) {
	    	System.err.println("ERROR: Unable to read people from file.");
	    	e.printStackTrace();
	    } catch(IllegalArgumentException e) {
	    	System.err.println("ERROR: Person does not contain a valid type.");
	    	e.printStackTrace();
	    }
	}*/
	 
	
	public boolean isAllPeopleAssigned() {
		return unassigned.isEmpty();
	}
	
	public void executeAction(String person, String action) {
		int i = 0;
		if (action.equals("enter")) {
			while (!peopleOutside.get(i).getName().equals(person)) ++i;
			Person p = peopleOutside.get(i);
			p.setState(State.INSIDE);
			peopleOutside.remove(p);
			peopleInside.add(p);
			System.out.println(p.getName() + " has entered");
		}
		else if (action.equals("leave")) {
			while (!peopleInside.get(i).getName().equals(person)) ++i;
			Person p = peopleInside.get(i);
			p.setState(State.OUTSIDE);
			peopleInside.remove(p);
			peopleOutside.add(p);
			System.out.println(p.getName() + " has left");
		}
		else if (action.equals("lunch")) {
			while (!peopleInside.get(i).getName().equals(person)) ++i;
			Person p = peopleInside.get(i);
			p.setState(State.LUNCH);
			peopleInside.remove(p);
			peopleLunch.add(p);
			System.out.println(p.getName() + " is going for lunch");
		}
		else if (action.equals("returnLunch")) {
			while (!peopleLunch.get(i).getName().equals(person)) ++i;
			Person p = peopleLunch.get(i);
			p.setState(State.INSIDE);
			peopleLunch.remove(p);
			peopleInside.add(p);
			System.out.println(p.getName() + " has come back");
		}
		else if (Utils.RANDOM_WALS && action.equals("randomWalk")) {
			while (!peopleInside.get(i).getName().equals(person)) ++i;
			Person p = peopleInside.get(i);
			p.setState(State.RANDOM_WALKS);
			peopleInside.remove(p);
			peopleRandomWalks.add(p);
			System.out.println(p.getName() + " is walking");
		}
		else if (Utils.RANDOM_WALS && action.equals("returnRandomWalk")) {
			while (!peopleRandomWalks.get(i).getName().equals(person)) ++i;
			Person p = peopleRandomWalks.get(i);
			p.setState(State.INSIDE);
			peopleRandomWalks.remove(p);
			peopleInside.add(p);
			System.out.println(p.getName() + " has returned from walking");
		}
		else {
			System.out.println("Action is not correct");
		}
	}

	public void closeFile() {
		if (writer != null) writer.close();
	}
}
