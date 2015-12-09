package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import behaviour.Person;
import iot.Sensor;

@Rule(name = "Door Management Rule")
public class DoorRule {
	
	private ArrayList<Person> people;
	
	private Sensor door;
	
	//TODO change to an actual actuator
	//private String actuator;
	
	public DoorRule(ArrayList<Person> people) {
		this.people = people;
	}
	
	public ArrayList<String> getNecessarySensors() {
		ArrayList<String> ret = new ArrayList<String>();
		if (door == null) ret.add("door");
		return ret;
	}
	
	public void setSensor(String ruleSens, Sensor s) {
		if (ruleSens.equals("door")) {
			door = s;
		}
	}
	
	@Condition
	public boolean checkDoor() {
		int val = Integer.parseInt(door.getValue());
		return val > 0;
	}
	
	@Action(order = 1)
	public void switchOffLight() throws Exception {
		System.out.println("Door triggered");
	}
}
