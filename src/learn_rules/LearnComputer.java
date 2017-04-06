package learn_rules;

import building.Room;
import domain.Debugger;
import entity.Computer;
import entity.Computer.State;
import iot.Manager;
import iot.Sensor;
import rule_headers.ComputerRule;

public class LearnComputer extends ComputerRule {


    public LearnComputer(Room r, Computer c, Sensor s) {
        super(r, c, s);
    }

	@Override
	public boolean evaluate() {
        Computer comp = super.comp;
        State st = comp.getCurrentState();
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
        Computer comp = super.comp;
        State st = comp.getCurrentState();
        if (st.equals(State.ON)) {
            if (comp.getUsedBy() == null) {
                if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                        " switched OFF in room " + room.getLocation());
                System.out.println(Manager.CURRENT_STEP + " Computer " + comp.getId() + " OFF " + room.getLocation());
                comp.setCurrentState(State.OFF);
            }
            else {
                if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                        " SUSPENDED in room " + room.getLocation());
                System.out.println(Manager.CURRENT_STEP + " Computer " + comp.getId() + " SUSP " + room.getLocation());
                comp.setCurrentState(State.SUSPEND);
            }
        }
        else if (st.equals(State.SUSPEND)) {
            if (Debugger.isEnabled()) Debugger.log("Computer " + comp.getId() +
                    " awakened in room " + room.getLocation());
            System.out.println(Manager.CURRENT_STEP + " Computer " + comp.getId() + " ON from SUSP " + room.getLocation());
            comp.setCurrentState(State.ON);
        }
        comp.setTimeChanged(Manager.CURRENT_STEP);
	}

}
