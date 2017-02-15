package rule;

import building.Room;
import domain.Debugger;
import entity.Computer;
import entity.Computer.State;
import iot.Manager;
import iot.Sensor;

class SmartComputer extends ComputerRule {


    SmartComputer(Room r, Computer c, Sensor s) {
        super(r, c, s);
    }

	
	@Override
	public boolean evaluate() {
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
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    " switched ON in room " + room.getLocation());
            comp.setCurrentState(State.ON);
        }
		else if (st.equals(State.ON)) {
            if (comp.getUsedBy() == null) {
                if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                        " switched OFF in room " + room.getLocation());
                comp.setCurrentState(State.OFF);
            }
            else {
                if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                        " SUSPENDED in room " + room.getLocation());
                comp.setCurrentState(State.SUSPEND);
            }
        }
        else if (st.equals(State.SUSPEND)) {
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    " awakened in room " + room.getLocation());
            comp.setCurrentState(State.ON);
        }
        if (Manager.MODE == 0) saveAction();
	}

}
