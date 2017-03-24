package learn_rules;

import building.Room;
import entity.Computer;
import entity.Computer.State;
import iot.Manager;
import iot.Sensor;
import rule_headers.ComputerRule;

public class LearnComputer extends ComputerRule {


    public LearnComputer(Room r, Computer c, Sensor s) {
        super(r, c, s);
    }

    /*
        Checks if the computer was changed too soon, too late or at the necessary time:
            * 1 for too soon
            * 0 for correctly
            * -1 for too late
     */

    private int checkWhen(Computer comp) {
        int res = Manager.CURRENT_STEP - super.comp.getTimeChanged();
        if (res == 60) return 0;
        return res > 60 ? 1 : -1;

    }

	
	@Override
	public boolean evaluate() {
        Computer comp = super.comp;
		State st = comp.getCurrentState();
		/*if (st.equals(State.OFF)) {
			if (isGuestComing(60)) return true;
		}
		if (st.equals(State.ON)) {
			if (guestLeft()) return true;
		}
		if (st.equals(State.SUSPEND)) {
            if (guestReturned()) return true;
        }
		return false;*/

		if (st.equals(State.ON)) {
            int r = checkWhen(comp);
            if (r != 0) adjustSchedule(r);
        }
        return false;
	}

    private void adjustSchedule(int r) {
        if (r == 1) {
        }
    }


    @Override
	public void execute() throws Exception {
        /*Computer comp = getComputer();
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
        }*/
	}

}
