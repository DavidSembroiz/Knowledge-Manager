package learn_rules;

import building.Room;
import domain.Debugger;
import entity.Computer;
import entity.Computer.State;
import iot.Sensor;
import rule_headers.ComputerRule;

public class LearnComputer extends ComputerRule {


    public LearnComputer(Room r, Computer c, Sensor s) {
        super(r, c, s);
    }

	
	@Override
	public boolean evaluate() {
        Computer comp = getComputer();
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
			if (isGuestComing()) return true;
		}
		if (st.equals(State.ON)) {
			if (guestLeft()) return true;
		}
		if (st.equals(State.SUSPEND)) {
            if (guestReturned()) return true;
        }
		return false;
	}


    @Override
	public void execute() throws Exception {
        Computer comp = getComputer();
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    " switched ON in room " + room.getLocation());
        }
		else if (st.equals(State.ON)) {
            if (comp.getUsedBy() == null) {
                if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                        " switched OFF in room " + room.getLocation());
            }
            else {
                if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                        " SUSPENDED in room " + room.getLocation());
            }
        }
        else if (st.equals(State.SUSPEND)) {
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    " awakened in room " + room.getLocation());
        }
	}

}
