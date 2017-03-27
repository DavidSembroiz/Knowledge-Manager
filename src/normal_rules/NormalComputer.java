package normal_rules;

import building.Room;
import domain.Debugger;
import entity.Computer;
import entity.Computer.State;
import iot.Manager;
import iot.Sensor;
import rule_headers.ComputerRule;

public class NormalComputer extends ComputerRule {


    public NormalComputer(Room r, Computer c, Sensor s) {
        super(r, c, s);
    }

	
	@Override
	public boolean evaluate() {
		State st = super.comp.getCurrentState();
		int threshold = 0;
		if (st.equals(State.OFF)) {
			if (isGuestComing(threshold)) return true;
		}
		if (st.equals(State.ON)) {
			if (!isGuestComing(threshold) || room.isEmpty()) return true;
		}
		return false;
	}
	
	@Override
	public void execute() throws Exception {
        Computer comp = super.comp;
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    " switched ON in room " + room.getLocation());
            comp.setCurrentState(State.ON);
        }
		else if (st.equals(State.ON)) {
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    " switched OFF in room " + room.getLocation());
            comp.setCurrentState(State.OFF);
        }
        comp.setTimeChanged(Manager.CURRENT_STEP);
        saveAction();
	}

}
