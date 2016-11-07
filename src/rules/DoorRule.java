package rules;

import building.Room;
import domain.Debugger;
import entity.Door;
import entity.Door.State;
import iot.Sensor;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;

@Rule(name = "Door Management Rule")
public class DoorRule {
	
	private Room room;

    private Sensor electro;
	private Door door;
	
	//TODO change to an actual actuator
	//private String actuator;
	
	public DoorRule(Room r, Door d, Sensor s) {
		this.room = r;
        this.door = d;
        this.electro = s;
	}

	
	@Condition
	public boolean checkDoor() {
		State st = door.getCurrentState();
        if (st.equals(State.CLOSE)) {
            if (room.isPeopleComing()) return true;
        }
        if (st.equals(State.OPEN)) {
            if (!room.isPeopleComing() && !room.isPeopleInside()) return true;
        }
        return false;
	}
	
	@Action(order = 1)
	public void switchDoor() throws Exception {
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
