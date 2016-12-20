package model;

import iot.Manager;


public class ModelManager {
	
	private static ModelManager instance = new ModelManager();
	
	private ModelManager() {}
	
	public static ModelManager getInstance() {
		return instance;
	}

	
	public double getCurrentEnvironmentalTemperature() {
		return getCurrentEnvironmentalValue(Temperature.getEnvironmentalTemperature());
	}
	
	public double getCurrentEnvironmentalHumidity() {
        return getCurrentEnvironmentalValue(Humidity.getEnvironmentalHumidity());
	}
	
	public double getCurrentEnvironmentalLight() {
        return getCurrentEnvironmentalValue(Light.getEnvironmentalLight());
	}

    public double getCurrentAirQuality() {
        return getCurrentEnvironmentalValue(AirQuality.getAirQuality());
    }

    private double getCurrentEnvironmentalValue(double[] array) {
        int p1 = Manager.CURRENT_STEP/360;
        double val1 = array[p1];
        if (Manager.CURRENT_STEP%360 == 0) return val1;

        double val2 = array[(p1 + 1)%24];
        return getWeightedValue(val1, val2);
    }

    /**
     * Returns the weighted value between two, according to the current time (step)
     */

    private double getWeightedValue(double val1, double val2) {
        val1 = val1 > val2 ?  val1 - (val1 - val2) : val1 + (val2 - val1);
        return val1 * (Manager.CURRENT_STEP%360/360.0);
    }

}
