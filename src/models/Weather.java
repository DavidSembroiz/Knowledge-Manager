package models;


public class Weather {
	
	private static Weather instance = new Weather();
	
	private Weather() {
	}
	
	public static Weather getInstance() {
		return instance;
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
		return environmentalTemperature[0]; // change to fit the current step
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
		return environmentalHumidity[0]; // change to fit the current step
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
		return environmentalLight[0]; // change to fit the current step
	}

}
