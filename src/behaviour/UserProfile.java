package behaviour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import behaviour.Person.Type;

public class UserProfile {
	
	private Probability entrance;
	private Probability randomWalks;
	//private Probability randomWalksDuration;
	private Probability lunch;
	//private Probability lunchDuration;
	private Probability exit;
	
	private int[] lunchDurationRange;
	private int[] randomWalksDurationRange;
	
	private Type type;
	private Random rand;
	
	public UserProfile(Type t) {
		this.type = t;
		loadProfileFromFile();
		rand = new Random();
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
	
	/*public Probability getRandomWalksDuration() {
		return randomWalksDuration;
	}
	public void setRandomWalksDuration(Probability randomWalksDuration) {
		this.randomWalksDuration = randomWalksDuration;
	}
	public Probability getLunchDuration() {
		return lunchDuration;
	}
	public void setLunchDuration(Probability lunchDuration) {
		this.lunchDuration = lunchDuration;
	}*/
	public Probability getLunch() {
		return lunch;
	}
	public void setLunch(Probability lunch) {
		this.lunch = lunch;
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

	public void setType(Type t) {
		this.type = t;
	}
	
	/**
	 * Returns a value within min <= value <= max
	 */
	public int getLunchDuration() {
		return rand.nextInt((lunchDurationRange[1] - lunchDurationRange[0]) + 1) + lunchDurationRange[0];
	}
	
	public int getRandomWalksDuration() {
		return rand.nextInt((randomWalksDurationRange[1] - randomWalksDurationRange[0]) + 1) + randomWalksDurationRange[0];
	}
	
	
	/**
	 * Profile file is divided in blocks of two lines with the following format:
	 * 
	 * probabilityName
	 * value,value,value,value...
	 * 
	 * For the actions that requiere a range instead of a probability, the format is the following:
	 * 
	 * actionName
	 * minValue,maxValue
	 */
	
	private void loadProfileFromFile() {
		String path = "res/profile/" + this.type.toString().toLowerCase() + ".txt";
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] values = br.readLine().split(",");
	        	/**
	        	 * Reading duration action
	        	 */
	        	if (values.length == 2) {
	        		assignDuration(line, values);
	        	}
	        	/**
	        	 * Reading probability distribution of an action
	        	 */
	        	else {
	        		Probability p = new Probability(values);
	        		assignProbability(line, p);
	        	}
	        	
	        }
	        br.close();
	    } catch (IOException e) {
	    	System.err.println("ERROR: Unable to read probability from file.");
	    	e.printStackTrace();
	    }
	}
	
	private int[] arrayStringToInt(String[] values) {
		int[] res = new int[2];
		for (int i = 0; i < values.length; ++i) {
			res[i] = Integer.parseInt(values[i]);
		}
		return res;
	}
	
	private void assignDuration(String name, String[] values) {
		int[] vals = arrayStringToInt(values);
		switch(name) {
		case "lunchDuration":
			lunchDurationRange = vals;
			break;
		case "randomWalksDuration":
			randomWalksDurationRange = vals;
			break;
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
		/*case "lunchDuration":
			lunchDuration = p;
			break;*/
		case "randomWalks":
			randomWalks = p;
			break;
		/*case "randomWalksDuration":
			randomWalksDuration = p;
			break;*/
		default:
			System.err.println("ERROR: profile file wrongly formatted");
			break;
		}
	}
}
