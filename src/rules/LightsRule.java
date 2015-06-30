package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import domain.Utils;
import domain.Register;
import behaviour.Person;
import iot.Sensor;

@Rule(name = "Lights Management Rule")
public class LightsRule {
	
	private ArrayList<Person> people;
	private Register reg;
	
	private Sensor luminosity;
	private boolean hasChanged;
	
	private String light;
	
	public LightsRule(ArrayList<Person> people) {
		reg = Register.getInstance();
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
	
	private int getEnvironmentalLight() {
		return 200; // get it from a model
	}
	
	private boolean environmentalLightOK() {
		int threshold = 500;
		int modelValue = 400; // get it from a model
		return modelValue > threshold;
	}
	
	@Condition
	public boolean checkLuminosity() {
		if (light.equals("on") && (Utils.emptyRoom(people) || environmentalLightOK())) {
			light = "off";
			luminosity.setValue(Integer.toString(getEnvironmentalLight()));
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
			System.out.println("Light switched on");
		}
		else if (light.equals("off")) {
			reg.switchLightOff();
			System.out.println("Light switched off");
		}
	}
}
