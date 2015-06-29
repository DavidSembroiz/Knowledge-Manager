package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import iot.Sensor;
import behaviour.Person;

@Rule(name = "HVAC Management Rule")
public class HVACRule {
	
	private ArrayList<Person> people;
	
	private Sensor temperature;
	private Sensor humidity;
	
	private String actuator;
	
	private String action;
	
	public HVACRule(ArrayList<Person> people) {
		this.people = people;
		action = "";
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
		return false;
	}
	
	@Action(order = 1)
	public void apply() throws Exception {
		if (action.equals("heat")) {
			System.out.println("AC System activated to heat the room");
		}
		else if (action.equals("cool")) {
			System.out.println("AC System activated to cool the room");
		}
		else {
			System.out.println("Temperature inside the thresholds");
		}
	}
}
