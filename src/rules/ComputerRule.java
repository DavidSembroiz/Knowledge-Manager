package rules;

import building.Room;
import domain.Debugger;
import entity.Computer;
import entity.Computer.State;
import iot.Sensor;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.core.BasicRule;

public class ComputerRule extends BasicRule {

    private int PREDICTION_THRESHOLD = 30;
	
	private Room room;
	
	private Sensor power;
	private Computer comp;


    public ComputerRule(Room r, Computer c, Sensor s) {
        super("Computer rule #" + Integer.toString(c.getId()), "Rule to manage computer", c.getId());
        this.room = r;
        this.comp = c;
        this.power = s;
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
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    "switched ON in room " + room.getLocation());
            comp.setCurrentState(State.ON);
        }
		else if (st.equals(State.ON)) {
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    "switched OFF in room " + room.getLocation());
            comp.setCurrentState(State.OFF);
        }
	}
		
}
