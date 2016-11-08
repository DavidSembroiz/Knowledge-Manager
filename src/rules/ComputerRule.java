package rules;

import building.Room;
import domain.Debugger;
import entity.Computer;
import entity.Computer.State;
import iot.Sensor;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;

@Rule(name = "Computer Management Rule")
public class ComputerRule {

    private int PREDICTION_THRESHOLD = 30;
	
	private Room room;
	
	private Sensor power;
	private Computer comp;

	
	public ComputerRule(Room r, Computer c, Sensor s) {
		this.comp = c;
		this.power = s;
		this.room = r;
	}

	
	@Condition
	public boolean checkComputer() {
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
			if (room.arePeopleComing(PREDICTION_THRESHOLD)) return true;
		}
		if (st.equals(State.ON)) {
			if (!room.arePeopleComing(PREDICTION_THRESHOLD) && room.isEmpty()) return true;
		}
		return false;
	}
	
	@Action(order = 1)
	public void switchStatus() throws Exception {
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("Computer switched ON in room " + room.getLocation());
            comp.setCurrentState(State.ON);
        }
		else if (st.equals(State.ON)) {
            if (Debugger.isEnabled()) Debugger.log("Computer switched OFF in room " + room.getLocation());
            comp.setCurrentState(State.OFF);
        }
	}
		
}
