package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import behaviour.Person;
import iot.Sensor;

@Rule(name = "Computer Management Rule")
public class Computer {
	
	private ArrayList<Person> people;
	
	private Sensor computer;
	
	//TODO change to an actual actuator
	private String actuator;
	
	public Computer(ArrayList<Person> people) {
		this.people = people;
	}
	
	public ArrayList<String> getNecessarySensors() {
		ArrayList<String> ret = new ArrayList<String>();
		if (computer == null) ret.add("computer");
		return ret;
	}
	
	public void setSensor(String ruleSens, Sensor s) {
		if (ruleSens.equals("computer")) {
			computer = s;
		}
	}
	
	@Condition
	public boolean checkComputer() {
		int val = Integer.parseInt(computer.getValue());
		return val > 0;
	}
	
	@Action(order = 1)
	public void switchOffLight() throws Exception {
		System.out.println("Computer triggered");
	}
}
