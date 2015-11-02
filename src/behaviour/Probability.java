package behaviour;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import domain.Utils;

public class Probability {
	
	private Map<Integer, Double> probabilityPerHour;
	private Random rand;
	
	public Probability(Map<Integer, Double> p) {
		this.probabilityPerHour = p;
		this.rand = new Random();
	}
	
	public Probability(String[] values) {
		probabilityPerHour = new HashMap<Integer, Double>();
		for (int i = 0; i < values.length; ++i) {
			probabilityPerHour.put(i, Double.parseDouble(values[i]));
		}
		this.rand = new Random();
	}
	
	private double getProbability(int currentTime) {
		int t1 = currentTime/Utils.HALF_HOUR;
		double p1 = this.probabilityPerHour.get(t1);
		if (currentTime%Utils.HALF_HOUR == 0) return p1;
		
		int t2 = (t1 + 1)%Utils.DIVISIONS;
		double p2 = this.probabilityPerHour.get(t2);
		
		double min = Math.min(p1, p2);
		double max = Math.max(p1, p2);
		return min + (max - min) * ((currentTime%Utils.HALF_HOUR)/Utils.HALF_HOUR);
	}
	
	public boolean triggerStatus(int currentTime) {
		return rand.nextDouble() < getProbability(currentTime)/10;
	}
	
	public boolean triggerStatusWithPrint(int currentTime) {
		double next = rand.nextDouble();
		double real = getProbability(currentTime)/10;
		//System.out.println("Next: " + next + "     |     " + "Real: " + real + "(" + currentTime + ")");
		return next < real;
	}
	
}
