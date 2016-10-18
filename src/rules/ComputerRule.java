package rules;

import java.io.PrintWriter;
import java.util.ArrayList;

import org.easyrules.annotation.*;

import behaviour.Person;
import building.Room;
import iot.Sensor;
import entity.Computer;
import entity.Computer.State;

@Rule(name = "Computer Management Rule")
public class ComputerRule {
	
	private Room room;
	
	private Sensor power;
	private Computer comp;
	
	private PrintWriter writer;
	
	public ComputerRule(Room r, Computer c, Sensor s) {
		this.comp = c;
		this.power = s;
		this.room = r;
	}
	
	private boolean isPeopleComing() {
		
		/**
		 * If someone comes in the next 5 minutes, computer must be switched ON
		 */
		ArrayList<Person> people = room.getPeople();
		for (Person p : people) {
			if (p.getNextActionSteps() < 30) return true;
		}
		return false;
	}
	
	private boolean isPeopleInside() {
		
		/**
		 * If room is empty, computer must be switched OFF
		 */
		
		return room.getPeople().size() > 0;
	}
	
	@Condition
	public boolean checkComputer() {
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
			if (isPeopleComing()) return true;
		}
		if (st.equals(State.ON)) {
			if (!isPeopleComing() && !isPeopleInside()) return true;
		}
		return false;
	}
	
	@Action(order = 1)
	public void switchStatus() throws Exception {
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) comp.setCurrentState(State.ON);
		else if (st.equals(State.ON)) comp.setCurrentState(State.OFF);
	}
		
}
