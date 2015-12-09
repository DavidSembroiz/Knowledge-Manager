package rules;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.easyrules.annotation.*;

import domain.Utils;
import domain.Register;
import behaviour.Person;
import iot.Sensor;
import models.Weather;

@Rule(name = "Lights Management Rule")
public class LightsRule {
	
	private ArrayList<Person> people;
	private Register reg;
	private Weather models;
	
	private Sensor luminosity;
	private boolean hasChanged;
	
	private String light;
	
	private PrintWriter writer;
	
	public LightsRule(ArrayList<Person> people) {
		reg = Register.getInstance();
		models = Weather.getInstance();
		this.people = people;
		this.light = "off";
		this.hasChanged = false;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter("res/results/light.txt")));
		} catch(IOException e) {
		}
	}
	
	private void printStatus() {
		String temp = Utils.getTemplatePersonName();
		for (Person p : people) {
			if (p.getName().equals(temp)) {
				if (light.equals("on")) writer.println("1");
				else if (light.equals("off")) writer.println("0");
			}
		}
		
		if (Utils.CURRENT_STEP == 8600) {
			writer.close();
		}
	}
	
	public ArrayList<String> getNecessarySensors() {
		ArrayList<String> ret = new ArrayList<String>();
		if (luminosity == null) ret.add("luminosity");
		return ret;
	}
	
	public void setSensor(String ruleSens, Sensor s) {
		if (ruleSens.equals("luminosity")) {
			luminosity = s;
		}
	}
	
	private boolean environmentalLightOK() {
		int threshold = 500;
		double modelValue = models.getCurrentEnvironmentalLight();
		return modelValue > threshold;
	}
	
	@Condition
	public boolean checkLuminosity() {
		
		printStatus();
		
		/**
		 * If light is ON:
		 *  - OFF: room is empty or environmental light is OK
		 * 
		 * If light is OFF:
		 *  - ON: someone has entered the room or the environmental light is BAD
		 */
		
		if (light.equals("on") && (Utils.emptyRoom(people) || environmentalLightOK())) {
			light = "off";
			luminosity.setValue(Double.toString(models.getCurrentEnvironmentalLight()));
			hasChanged = true;
		}
		else if (light.equals("off") && !Utils.emptyRoom(people) && !environmentalLightOK()) {
			light = "on";
			luminosity.setValue(Integer.toString(500));
			hasChanged = true;
		}
		else if (light.equals("off")) {
			luminosity.setValue(Double.toString(models.getCurrentEnvironmentalLight()));
		}
		return hasChanged;
	}
	
	@Action(order = 1)
	public void changeState() throws Exception {
		
		hasChanged = false;
		
		/**
		 * Register the new state and compute its new consumption
		 */
		
		if (light.equals("on")) {
			reg.switchLightOn();
			System.out.println(Utils.CURRENT_STEP + " Light switched on");
		}
		else if (light.equals("off")) {
			reg.switchLightOff();
			System.out.println(Utils.CURRENT_STEP + " Light switched off");
		}
	}
}
