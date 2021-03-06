package rule_headers;

import building.Room;
import domain.Debugger;
import entity.Door;
import entity.Door.State;
import iot.Sensor;
import org.easyrules.core.BasicRule;

class DoorRule extends BasicRule {
	
	private Room room;

    private Sensor electro;
	private Door door;

	
	DoorRule(Room r, Door d, Sensor s) {
        super("Door rule_headers #" + d.getId(), "Rule to manage doors", d.getId());

        this.room = r;
        this.door = d;
        this.electro = s;
	}

	
	@Override
	public boolean evaluate() {
        int PREDICTION_THRESHOLD = 10;
		State st = door.getCurrentState();
        if (st.equals(State.CLOSE)) {
            if (room.arePeopleComing(PREDICTION_THRESHOLD)) return true;
        }
        if (st.equals(State.OPEN)) {
            if (!room.arePeopleComing(PREDICTION_THRESHOLD) && room.isEmpty()) return true;
        }
        return false;
	}
	
	@Override
	public void execute() throws Exception {
        State st = door.getCurrentState();
        if (st.equals(State.CLOSE)) {
            if (Debugger.isEnabled()) Debugger.log("Door OPENED in room " + room.getLocation());
            door.setCurrentState(State.OPEN);
        }
        else if (st.equals(State.OPEN)) {
            if (Debugger.isEnabled()) Debugger.log("Door CLOSED in room " + room.getLocation());
            door.setCurrentState(State.CLOSE);
        }
	}
}
