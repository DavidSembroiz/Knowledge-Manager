package models;

import iot.Manager;


public class Weather {
	
	private static Weather instance = new Weather();
	
	private Weather() {
	}
	
	public static Weather getInstance() {
		return instance;
	}
	
	/**
	 * Returns the weighted value between two, according to the current time (step)
	 */
	
	private double getWeightedValue(double val1, double val2) {
		if (val1 > val2) {
			return val1 - (val1 - val2) * (Manager.CURRENT_STEP%360/360.0);
		}
		return val1 + (val2 - val1) * (Manager.CURRENT_STEP%360/360.0);
	}
	
	/**
	 * Daily temperature every hour, starts at 0h
	 */
	
	/*private static final double[] environmentalTemperature = {
		21, 20, 19, 18, 18, 19,
		20, 22, 24, 26, 28, 30,
		33, 34, 35, 36, 36, 35,
		34, 31, 29, 27, 25, 22
	};*/
	
	private static final double[] environmentalTemperature = {
			9, 9, 8, 8, 7, 7,
			6, 6, 7, 8, 9, 12,
			14, 16, 16, 16, 16, 15,
			14, 13, 12, 11, 10, 10
		};
	
	/**
	 * Returns the current environmental temperature. If the current time is between two values,
	 * it calculates the weighted value in between both of them.
	 */
	
	public double getCurrentEnvironmentalTemperature() {
		int p1 = Manager.CURRENT_STEP/360;
		double val1 = environmentalTemperature[p1];
		if (Manager.CURRENT_STEP%360 == 0) return val1;
			
		int p2 = (p1 + 1)%24;
		double val2 = environmentalTemperature[p2];
		return getWeightedValue(val1, val2);
	}
	
	/**
	 * Daily humidity every hour, starts at 0h
	 */
	
	private static final double[] environmentalHumidity = {
		48, 48, 50, 54, 56, 56,
		55, 50, 47, 42, 38, 35,
		33, 31, 29, 29, 29, 32,
		35, 37, 38, 41, 44, 48
	};
	
	public double getCurrentEnvironmentalHumidity() {
		int p1 = Manager.CURRENT_STEP/360;
		double val1 = environmentalHumidity[p1];
		if (Manager.CURRENT_STEP%360 != 0) {
			int p2 = (p1 + 1)%24;
			double val2 = environmentalHumidity[p2];
			return getWeightedValue(val1, val2);
		}
		return val1;
	}
	
	/**
	 * Daily luminosity every hour, starts at 0h
	 */
	
	private static final double[] environmentalLight = {
		20, 20, 20, 40, 60, 60,
		100, 120, 140, 200, 400, 600,
		800, 900, 1000, 1200, 1400, 1500,
		1400, 1200, 600, 200, 100, 20
	};
	
	public double getCurrentEnvironmentalLight() {
		int p1 = Manager.CURRENT_STEP/360;
		double val1 = environmentalLight[p1];
		if (Manager.CURRENT_STEP%360 != 0) {
			int p2 = (p1 + 1)%24;
			double val2 = environmentalLight[p2];
			return getWeightedValue(val1, val2);
		}
		return val1;
	}
}
