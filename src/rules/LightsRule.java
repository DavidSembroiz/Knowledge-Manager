package rules;

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
	
	public LightsRule(ArrayList<Person> people) {
		reg = Register.getInstance();
		models = Weather.getInstance();
		this.people = people;
		this.light = "off";
		this.hasChanged = false;
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
			luminosity.setValue(Integer.toString(1000));
			hasChanged = true;
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
