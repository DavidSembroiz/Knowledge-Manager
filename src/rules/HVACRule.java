package rules;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	
	private PrintWriter writer;
	
	public HVACRule(ArrayList<Person> people) {
		reg = Register.getInstance();
		models = Weather.getInstance();
		this.people = people;
		action = "";
		ac = "off";
		old_ac = "";
		hasChanged = false;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter("res/results/hvac.txt")));
		} catch(IOException e) {
		}
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
		return hum < 45 ? 23.0 : 22.0;
	}
	
	private boolean environmentalTemperatureOK() {
		
		Double temp = models.getCurrentEnvironmentalTemperature();
		Double hum = models.getCurrentEnvironmentalHumidity();
		
		/**
		 * Summer
		 */
		
		if (hum < 45 && temp < 23 && temp > 22) return true;
		else if (hum > 60 && temp < 22 && temp > 21) return true;
		else if (temp < 22.5 && temp > 21.5) return true;
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
			newTemp = roomTemp - (roomTemp - environTemp) * 0.001;
		}
		else newTemp = roomTemp + (environTemp - roomTemp) * 0.001;
		temperature.setValue(Double.toString(newTemp));
	}
	
	
	@Condition
	public boolean checkConditions() {
		
		printStatus();
		
		/**
		 * If HVAC is ON:
		 *  - OFF: room is empty
		 *  - HEAT or COLD: temperature adjustment is not instant, it must vary over time
		 *                  until desired state is achieved
		 *  - MAINTAIN: temperature is OK, do nothing
		 * If HVAC is OFF:
		 *  - ON: someone has entered the room and environmental temperature is BAD
		 */
		
		if ((ac.equals("on") || ac.equals("maintain")) && 
		   ((Utils.emptyRoom(people) && !Utils.justWalking(people)) || 
		     environmentalTemperatureOK())) {
			hasChanged = true;
			old_ac = ac;
			ac = "off";
		}
		else if (ac.equals("on") ||
				 ac.equals("off") && !Utils.emptyRoom(people) && !environmentalTemperatureOK()) {
			hasChanged = true;
			//System.out.println("Desired " + getDesiredTemperature());
			double diff = getDesiredTemperature() - Double.parseDouble(temperature.getValue());
			old_ac = ac;
			ac = "on";
			if (diff < -0.2) action = "cold";
			else if (diff > 0.2) action = "heat";
			else ac = "maintain";
		}
		else if (ac.equals("off")) {
			moderateTemperature();
		}
		return hasChanged;
	}
	
	private void printStatus() {
		String temp = Utils.getTemplatePersonName();
		for (Person p : people) {
			if (p.getName().equals(temp)) {
				if (ac.equals("on")) writer.println("2");
				else if (ac.equals("maintain")) writer.println("1");
				else if (ac.equals("off")) writer.println("0");
			}
		}
		if (Utils.CURRENT_STEP == 8600) {
			writer.close();
		}
	}
	
	@Action(order = 1)
	public void apply() throws Exception {
		
		hasChanged = false;
		
		/**
		 * Register the new state and compute its new consumption
		 */
		
		if (ac.equals("off")) {
			if (old_ac.equals("on")) {
				System.out.println(Utils.CURRENT_STEP + " HVAC switched off from on");
				reg.switchHvacOff();
			}
			else if (old_ac.equals("maintain")) {
				System.out.println(Utils.CURRENT_STEP + " HVAC switched to off from maintain");
				reg.switchOffMaintHvac();
			}
		}
		else if (ac.equals("on")) {
			if (old_ac.equals("off")) {
				System.out.println(Utils.CURRENT_STEP + " HVAC switched on");
				reg.switchHvacOn();
			}
			else if (old_ac.equals("maintain")) {
				System.out.println(Utils.CURRENT_STEP + " HVAC switched to on from maintain");
				reg.switchOffMaintHvac();
				reg.switchHvacOn();
			}
			Double temp = Double.parseDouble(temperature.getValue());
			if (action.equals("heat")) {
				temp += 0.015;
				temperature.setValue(Double.toString(temp));
				System.out.println(Utils.CURRENT_STEP + " HVAC heating the room " + people.get(0).getLocation() + " " + temperature.getValue());
			}
			else if (action.equals("cold")) {
				temp -= 0.015;
				temperature.setValue(Double.toString(temp));
				System.out.println(Utils.CURRENT_STEP + " HVAC cooling the room " + people.get(0).getLocation() + " " + temperature.getValue());
			}
		}
		else if (ac.equals("maintain")) {
			/*double cur = Double.parseDouble(temperature.getValue());
			if (models.getCurrentEnvironmentalTemperature() > cur) {
				temperature.setValue(Double.toString(cur + 0.05));
			}
			else temperature.setValue(Double.toString(cur - 0.05));*/
			System.out.println(Utils.CURRENT_STEP + " Temperature being maintained");
			
			
			if (old_ac.equals("on")) reg.setMaintainHvacFromOn();
			else if (old_ac.equals("off")) reg.setMaintainHvacFromOff();
		}
	}
}
