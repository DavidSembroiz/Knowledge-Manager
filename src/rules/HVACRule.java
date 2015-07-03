package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import iot.Sensor;
import models.Weather;
import behaviour.Person;
import domain.Utils;
import domain.Register;

@Rule(name = "HVAC Management Rule")
public class HVACRule {
	
	private ArrayList<Person> people;
	private Register reg;
	private Weather models;
	
	private Sensor temperature;
	private Sensor humidity;
	
	private String ac;
	private String old_ac;
	
	private String action;
	private boolean hasChanged;
	
	public HVACRule(ArrayList<Person> people) {
		reg = Register.getInstance();
		models = Weather.getInstance();
		this.people = people;
		action = "";
		ac = "off";
		old_ac = "";
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
	
	private Double getDesiredTemperature() {
		Double hum = Double.parseDouble(humidity.getValue());
		return hum < 45 ? 25.0 : 24.0;
	}
	
	private boolean environmentalTemperatureOK() {
		
		Double temp = models.getCurrentEnvironmentalTemperature();
		Double hum = models.getCurrentEnvironmentalHumidity();
		
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
	
	/**
	 * It usually takes up to 3 hours to modify 10 degrees
	 */
	
	private void moderateTemperature() {
		double roomTemp = Double.parseDouble(temperature.getValue());
		double environTemp = models.getCurrentEnvironmentalTemperature();
		double newTemp = 0;
		if (roomTemp == environTemp) return;
		if (roomTemp > environTemp) {
			newTemp = roomTemp - (roomTemp - environTemp) * 0.05;
		}
		else newTemp = roomTemp + (environTemp - roomTemp) * 0.05;
		temperature.setValue(Double.toString(newTemp));
	}
	
	
	@Condition
	public boolean checkConditions() {
		
		/**
		 * If HVAC is ON:
		 *  - OFF: room is empty
		 *  - HEAT or COLD: temperature adjustment is not instant, it must vary over time
		 *                until desired state is achieved
		 *  - MAINTAIN: temperature is OK, do nothing
		 * If HVAC is OFF:
		 *  - ON: someone has entered the room or environmental temperature is BAD
		 */
		
		if ((ac.equals("on") || ac.equals("maintain")) && 
		   ((Utils.emptyRoom(people) && !Utils.justWalking(people) && !Utils.eating(people)) || 
		     environmentalTemperatureOK())) {
			hasChanged = true;
			old_ac = ac;
			ac = "off";
		}
		else if (ac.equals("on") ||
				 ac.equals("off") && !Utils.emptyRoom(people) && !environmentalTemperatureOK()) {
			hasChanged = true;
			double diff = getDesiredTemperature() - Double.parseDouble(temperature.getValue());
			old_ac = ac;
			ac = "on";
			if (diff < -0.5) action = "cold";
			else if (diff > 0.5) action = "heat";
			else ac = "maintain";
		}
		else if (ac.equals("off")) {
			moderateTemperature();
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
			if (old_ac.equals("on")) reg.switchHvacOff();
			else if (old_ac.equals("maintain")) reg.switchOffMaintHvac();
			System.out.println("HVAC switched off");
		}
		else if (ac.equals("on")) {
			if (old_ac.equals("off")) reg.switchHvacOn();
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
		else if (ac.equals("maintain")) {
			System.out.println("Temperature being maintained");
			
			if (old_ac.equals("on")) reg.setMaintainHvacFromOn();
			else if (old_ac.equals("off")) reg.setMaintainHvacFromOff();
		}
	}
}
