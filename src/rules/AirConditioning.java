package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import iot.Sensor;

@Rule(name = "Manages air condition extreme cases")
public class AirConditioning {
	
	private Sensor temperature;
	private Sensor humidity;
	
	//TODO change to an actual actuator
	//private String actuator;
	
	private String action;
	
	public AirConditioning() {
	}
	
	public ArrayList<String> getNecessarySensors() {
		ArrayList<String> ret = new ArrayList<String>();
		if (temperature == null) ret.add("temperature");
		if (humidity == null) ret.add("humidity");
		return ret;
	}
	
	public void setSensor(String ruleSens, Sensor s) {
		if (ruleSens.equals("temperature")) temperature = s;
		else if (ruleSens.equals("humidity")) humidity = s;
	}
	
	@Condition
	public boolean checkConditions() {
		double temp = Double.parseDouble(temperature.getValue());
		double hum = Double.parseDouble(humidity.getValue());
		if (temp < 18 && hum < 20) {
			action = "heat";
			return true;
		}
		if (temp > 25 && hum > 30) {
			action = "cool";
			return true;
		}
		return true;
	}
	
	@Action(order = 1)
	public void apply() throws Exception {
		action = "heat";
		if (action.equals("heat")) {
			System.out.println("AC System activated to heat the room");
		}
		else if (action.equals("cool")) {
			System.out.println("AC System activated to cool the room");
		}
	}
}
