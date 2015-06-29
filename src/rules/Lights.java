package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import behaviour.Person;
import iot.Sensor;

@Rule(name = "Lights Management Rule")
public class Lights {
	
	private ArrayList<Person> people;
	
	private Sensor luminosity;
	
	//TODO change to an actual actuator
	private String actuator;
	
	public Lights(ArrayList<Person> people) {
		this.people = people;
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
	
	@Condition
	public boolean checkLuminosity() {
		int val = Integer.parseInt(luminosity.getValue());
		return val > 300;
	}
	
	@Action(order = 1)
	public void switchOffLight() throws Exception {
		System.out.println("Switch off light triggered");
	}
}
