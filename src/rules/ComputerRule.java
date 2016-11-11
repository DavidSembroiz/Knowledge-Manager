package rules;

import building.Room;
import domain.Debugger;
import entity.Computer;
import entity.Computer.State;
import iot.Sensor;
import org.easyrules.core.BasicRule;

public class ComputerRule extends BasicRule {

    private int PREDICTION_THRESHOLD = 30;
	
	private Room room;
	
	private Sensor power;
	private Computer comp;



    public ComputerRule(Room r, Computer c, Sensor s) {
        super("Computer rule #" + c.getId(), "Rule to manage computer", c.getId());
        this.room = r;
        this.comp = c;
        this.power = s;
    }

    // TODO change return false

    private boolean isPersonComing() {
        if (comp.getUsedBy() == null) return false;
        return !(comp.getUsedBy() == null) || (comp.getUsedBy().getNextActionSteps() < PREDICTION_THRESHOLD);
    }

	
	@Override
	public boolean evaluate() {
		State st = comp.getCurrentState();
		if (st.equals(State.OFF)) {
			if (isPersonComing()) return true;
		}
		if (st.equals(State.ON)) {
			if (!isPersonComing() || room.isEmpty()) return true;
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
	}
		
}
