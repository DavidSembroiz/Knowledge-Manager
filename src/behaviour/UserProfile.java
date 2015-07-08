package behaviour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import behaviour.Person.Type;

public class UserProfile {
	
	private Probability entrance;
	private Probability randomWalks;
	private Probability randomWalksDuration;
	private Probability lunch;
	private Probability lunchDuration;
	private Probability exit;
	
	private Type type;
	
	public UserProfile(Type t) {
		this.type = t;
		loadProfileFromFile();
	}
	
	public Probability getEntrance() {
		return entrance;
	}
	public void setEntrance(Probability entrance) {
		this.entrance = entrance;
	}
	public Probability getRandomWalks() {
		return randomWalks;
	}
	public void setRandomWalks(Probability randomWalks) {
		this.randomWalks = randomWalks;
	}
	
	public Probability getRandomWalksDuration() {
		return randomWalksDuration;
	}
	public void setRandomWalksDuration(Probability randomWalksDuration) {
		this.randomWalksDuration = randomWalksDuration;
	}
	public Probability getLunch() {
		return lunch;
	}
	public void setLunch(Probability lunch) {
		this.lunch = lunch;
	}
	public Probability getLunchDuration() {
		return lunchDuration;
	}
	public void setLunchDuration(Probability lunchDuration) {
		this.lunchDuration = lunchDuration;
	}
	public Probability getExit() {
		return exit;
	}
	public void setExit(Probability exit) {
		this.exit = exit;
	}
	public Type getType() {
		return type;
	}

	public void setT(Type t) {
		this.type = t;
	}
	
	/**
	 * Profile file is divided in blocks of two lines with the following format:
	 * 
	 * probabilityName
	 * value,value,value,value...
	 */
	
	private void loadProfileFromFile() {
		String path = "res/profile/" + this.type.toString().toLowerCase() + ".txt";
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] values = br.readLine().split(",");
	        	Probability p = new Probability(values);
	        	assignProbability(line, p);
	        }
	        br.close();
	    } catch (IOException e) {
	    	System.out.println("ERROR: Unable to read probability from file.");
	    	e.printStackTrace();
	    }
	}

	private void assignProbability(String name, Probability p) {
		switch (name) {
		case "entrance":
			entrance = p;
			break;
		case "exit":
			exit = p;
			break;
		case "lunch":
			lunch = p;
			break;
		case "lunchDuration":
			lunchDuration = p;
			break;
		case "randomWalks":
			randomWalks = p;
			break;
		case "randomWalksDuration":
			randomWalksDuration = p;
			break;
		default:
			System.out.println("ERROR: profile file wrongly formatted");
			break;
		}
	}
}
