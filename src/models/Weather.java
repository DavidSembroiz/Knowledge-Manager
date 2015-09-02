package models;

import domain.Utils;


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
		double min = Math.min(val1, val2);
		double max = Math.max(val1, val2);
		return min + (max - min) * (Utils.CURRENT_STEP%360/360.0);
	}
	
	/**
	 * Daily temperature every hour, starts at 0h
	 */
	
	private static final double[] environmentalTemperature = {
		25, 24, 23, 22, 21, 21,
		20, 22, 24, 26, 28, 30,
		33, 34, 35, 36, 36, 35,
		34, 31, 29, 27, 26, 25
	};
	
	public double getCurrentEnvironmentalTemperature() {
		int p1 = Utils.CURRENT_STEP/360;
		double val1 = environmentalTemperature[p1];
		if (Utils.CURRENT_STEP%360 != 0) {
			int p2 = (p1 + 1)%24;
			double val2 = environmentalTemperature[p2];
			return getWeightedValue(val1, val2);
		}
		return val1;
	}
	
	/**
	 * Daily humidity every hour, starts at 0h
	 */
	
	private static final double[] environmentalHumidity = {
		51, 48, 50, 54, 56, 56,
		55, 50, 47, 42, 38, 35,
		33, 31, 29, 29, 29, 32,
		37, 35, 38, 41, 44, 48
	};
	
	public double getCurrentEnvironmentalHumidity() {
		int p1 = Utils.CURRENT_STEP/360;
		double val1 = environmentalHumidity[p1];
		if (Utils.CURRENT_STEP%360 != 0) {
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
		100, 120, 140, 180, 160, 200,
		300, 400, 500, 1000, 1400, 1500,
		1400, 1200, 800, 500, 300, 100
	};
	
	public double getCurrentEnvironmentalLight() {
		int p1 = Utils.CURRENT_STEP/360;
		double val1 = environmentalLight[p1];
		if (Utils.CURRENT_STEP%360 != 0) {
			int p2 = (p1 + 1)%24;
			double val2 = environmentalLight[p2];
			return getWeightedValue(val1, val2);
		}
		return val1;
	}
}
