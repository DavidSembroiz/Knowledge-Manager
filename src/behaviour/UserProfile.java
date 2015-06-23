package behaviour;

import java.util.HashMap;
import java.util.Map;

import behaviour.Person.Type;

public class UserProfile {
	
	
	private Probability entrance;
	private Probability randomWalks;
	private Probability lunch;
	private Probability lunchDuration;
	private Probability exit;
	
	private Type type;
	
	public UserProfile(Type t) {
		this.type = t;
		/**
		 * Distinguish between different Types to create different probabilities
		 */
			entrance = new Probability(getDummyProbability());
			randomWalks = new Probability(getDummyProbability());
			lunch = new Probability(getDummyProbability());
			lunchDuration = new Probability(getDummyProbability());
			exit = new Probability(getDummyProbability());
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

	private Map<Integer, Double> getDummyProbability() {
		Map<Integer, Double> m = new HashMap<Integer, Double>();
		double s = 1.0/48;
		for (int i = 0; i < 48; ++i) {
			m.put(i, s*i);
		}
		m.put(47, 1.0);
		return m;
	}
	
	
}
