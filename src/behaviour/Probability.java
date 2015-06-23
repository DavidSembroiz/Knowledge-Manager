package behaviour;

import java.util.Map;
import java.util.Random;

public class Probability {
	
	private final int HALF_HOUR = 30;
	private final int PROBABILITY_DIVISIONS = 48;
	
	private Map<Integer, Double> probabilityPerHour;
	private Random rand;
	
	public Probability(Map<Integer, Double> p) {
		this.probabilityPerHour = p;
		this.rand = new Random();
	}
	
	private double getProbability(int currentTime) {
		int t1 = (currentTime/HALF_HOUR) % PROBABILITY_DIVISIONS;
		double p1 = this.probabilityPerHour.get(t1);
		if (currentTime%HALF_HOUR == 0) return p1;
		
		int t2 = ((currentTime/HALF_HOUR) + 1) % PROBABILITY_DIVISIONS;
		double p2 = this.probabilityPerHour.get(t2);
		double min = Math.min(p1, p2);
		double max = Math.max(p1, p2);
		return min + (max - min) * ((currentTime%HALF_HOUR)/30.0);
	}
	
	public boolean triggerStatus(int currentTime) {
		return rand.nextDouble() <= getProbability(currentTime);
	}
	
}
