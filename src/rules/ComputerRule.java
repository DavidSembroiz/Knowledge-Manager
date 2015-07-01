package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import behaviour.Person;
import iot.Sensor;
import domain.Utils;
import domain.Register;

@Rule(name = "Computer Management Rule")
public class ComputerRule {
	
	private ArrayList<Person> people;
	private Register reg;
	
	private Sensor computer;
	
	//TODO change to an actual actuator
	private String comp;
	private String old_comp;
	
	
	public ComputerRule(ArrayList<Person> people) {
		this.people = people;
		this.reg = Register.getInstance();
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
		
		/**
		 * If computer is ON:
		 *  - Suspend: everyone in the room is walking
		 *  - OFF: room is empty
		 *  
		 * If computer is SUSPENDED:
		 *  - ON: someone has returned to the room
		 * 
		 * If computer is OFF:
		 *  - ON: someone has entered the room
		 */
		
		if (comp.equals("on")) {
			old_comp = comp;
			if (Utils.emptyRoom(people) && Utils.justWalking(people)) {
				comp = "suspended";
				return true;
			}
			else if (Utils.emptyRoom(people)) {
				comp = "off";
				return true;
			}
		}
		else if ((comp.equals("suspended") || comp.equals("off")) && !Utils.emptyRoom(people)) {
			old_comp = comp;
			comp = "on";
			return true;
		}
		return false;
	}
	
	@Action(order = 1)
	public void switchOffLight() throws Exception {
		if (comp.equals("on")) {
			if (old_comp.equals("off")) {
				reg.switchComputerOn();
				System.out.println("Computer switched on");
			}
			else if (old_comp.equals("suspended")) {
				reg.enableSuspendedComputer();
				System.out.println("Computer enabled from suspension");
			}
		}
		else if (comp.equals("suspended")) {
			if (old_comp.equals("on")) {
				reg.putComputerInSuspension();
				System.out.println("Computer turned into suspension mode");
			}
		}
		else if (comp.equals("off")) {
			if (old_comp.equals("on")) {
				reg.switchComputerOff();
				System.out.println("Computer switched off");
			}
		}
	}
}
