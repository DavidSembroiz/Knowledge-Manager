package normal_rules;

import building.Room;
import domain.Debugger;
import entity.Lamp;
import iot.Manager;
import iot.Sensor;
import rule_headers.LampRule;

public class NormalLamp extends LampRule {

    public NormalLamp(Room r, Lamp l, Sensor light) {
        super(r, l, light);
    }

    @Override
    public boolean evaluate() {
        Lamp lamp = getLamp();
        Room room = getRoom();

        Lamp.State st = lamp.getCurrentState();
        if (st.equals(Lamp.State.OFF)) {
            if (room.arePeopleInside()) return true;
        }
        if (st.equals(Lamp.State.ON)) {
            if (room.isEmpty()) return true;
        }
        return false;
    }

    @Override
    public void execute() throws Exception {
        Lamp lamp = getLamp();
        Room room = getRoom();
        Lamp.State st = lamp.getCurrentState();
        if (st.equals(Lamp.State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("Lamp switched ON in room " + room.getLocation());
            lamp.setCurrentState(Lamp.State.ON);
        }
        else if (st.equals(Lamp.State.ON)) {
            if (Debugger.isEnabled()) Debugger.log("Lamp switched OFF in room " + room.getLocation());
            lamp.setCurrentState(Lamp.State.OFF);
        }
        lamp.setTimeChanged(Manager.CURRENT_STEP);
        saveAction();
    }
}
