package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import iot.Sensor;
import behaviour.Person;
import domain.Utils;

@Rule(name = "HVAC Management Rule")
public class HVACRule {
	
	private ArrayList<Person> people;
	
	private Sensor temperature;
	private Sensor humidity;
	
	private String ac;
	
	private String action;
	private boolean hasChanged;
	
	public HVACRule(ArrayList<Person> people) {
		this.people = people;
		action = "";
		ac = "off";
		hasChanged = false;
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
	
	private Double getEnvironmentalTemperature() {
		return 23.0; // get from the model
	}
	
	private Double getEnvironmentalHumidity() {
		return 35.0; // get from the model
	}
	
	private Double getDesiredTemperature() {
		Double hum = Double.parseDouble(humidity.getValue());
		return hum < 45 ? 25.0 : 24.0;
	}
	
	private boolean environmentalTemperatureOK() {
		
		Double temp = getEnvironmentalTemperature();
		Double hum = getEnvironmentalHumidity();
		/**
		 * Summer
		 */
		if (hum < 45 && temp < 28 && temp > 24.5) return true;
		else if (hum > 60 && temp < 25.5 && temp > 23) return true;
		else if (temp < 27 && temp > 23.8) return true;
		return false;
		
		/**
		 * Winter
		
		if (hum < 45 && temp < 25.5 && temp > 20.5) return true;
		else if (hum > 60 && temp < 24 && temp > 20) return true;
		else if (temp < 24.7 && temp > 20.2) return true;
		return false;
		
		*/
	}
	
	
	@Condition
	public boolean checkConditions() {
		
		if (ac.equals("on") && (Utils.emptyRoom(people) || environmentalTemperatureOK())) {
			hasChanged = true;
			temperature.setValue(Double.toString(getEnvironmentalTemperature()));
			ac = "off";
		}
		else if (ac.equals("on") || 
				(ac.equals("off") && !Utils.emptyRoom(people) && !environmentalTemperatureOK())) {
			
			hasChanged = true;
			double diff = getDesiredTemperature() - Double.parseDouble(temperature.getValue());
			if (diff <= 0.2) action = "cold";
			else action = "heat";
			ac = "on";
		}
		return hasChanged;
	}
	
	@Action(order = 1)
	public void apply() throws Exception {
		
		hasChanged = false;
		
		/**
		 * Register the new state and compute its new consumption
		 */
		
		if (ac.equals("off")) {
			System.out.println("HVAC switched off");
		}
		else if (ac.equals("on")) {
			Double temp = Double.parseDouble(temperature.getValue());
			if (action.equals("heat")) {
				temp += 0.2;
				temperature.setValue(Double.toString(temp));
				System.out.println("HVAC heating the room... " + temperature.getValue());
				
			}
			else if (action.equals("cold")) {
				temp -= 0.2;
				temperature.setValue(Double.toString(temp));
				System.out.println("HVAC cooling the room... " + temperature.getValue());
				
			}
		}
	}
}
