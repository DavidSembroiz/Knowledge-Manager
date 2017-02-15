package rule;

import building.Room;
import domain.Debugger;
import entity.Computer;
import entity.Computer.State;
import iot.Sensor;

class NormalComputer extends ComputerRule {


    NormalComputer(Room r, Computer c, Sensor s) {
        super(r, c, s);
    }

	
	@Override
	public boolean evaluate() {
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
			if (isGuestComing()) return true;
		}
		if (st.equals(State.ON)) {
			if (!isGuestComing() || room.isEmpty()) return true;
		}
		return false;
	}
	
	@Override
	public void execute() throws Exception {
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
        saveAction();
	}

}
