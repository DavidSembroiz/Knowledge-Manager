package behaviour;

import iot.Manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class Probability {
	
	private Map<Integer, Double> probabilityPerHour;
	private Random rand;
	
	
	Probability(String[] values) {
		probabilityPerHour = new HashMap<>();
		for (int i = 0; i < values.length; ++i) {
			probabilityPerHour.put(i, Double.parseDouble(values[i]));
		}
		this.rand = new Random();
	}
	
	private double getProbability(int currentTime) {
        int HALF_HOUR = 180;
        int DIVISIONS = 48;
		int t1 = currentTime/HALF_HOUR;
		double p1 = this.probabilityPerHour.get(t1);
		if (currentTime%HALF_HOUR == 0) return p1;
		
		int t2 = (t1 + 1)%DIVISIONS;
		double p2 = this.probabilityPerHour.get(t2);
		
		double min = Math.min(p1, p2);
		double max = Math.max(p1, p2);
		return min + (max - min) * ((currentTime%HALF_HOUR)/HALF_HOUR);
	}
	
	boolean triggerStatus(int currentTime) {
		return rand.nextDouble() < getProbability(currentTime)/Math.max(10 - Manager.CURRENT_STEP / 756, 0);
	}
}
