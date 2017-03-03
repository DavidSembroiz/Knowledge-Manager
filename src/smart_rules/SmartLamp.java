package smart_rules;

import building.Room;
import domain.Debugger;
import entity.Lamp;
import iot.Manager;
import iot.Sensor;
import rule_headers.LampRule;

public class SmartLamp extends LampRule {

    public SmartLamp(Room r, Lamp l, Sensor light) {
        super(r, l, light);
    }

    @Override
    public boolean evaluate() {

        int PREDICTION_THRESHOLD = 5;

		/*
		 * If light is ON:
		 *  - OFF: room is empty or environmental light is OK
		 *
		 * If light is OFF:
		 *  - ON: people inside or coming to the room and environmental light is bad
		 */
        Lamp lamp = getLamp();
        Room room = getRoom();
        Lamp.State st = lamp.getCurrentState();
        if (st.equals(Lamp.State.OFF)) {
            if ((room.arePeopleInside() || room.arePeopleComing(PREDICTION_THRESHOLD))
                    && !environmentalLightOK()) return true;
        }
        if (st.equals(Lamp.State.ON)) {
            if (room.isEmpty() || environmentalLightOK()) return true;
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
        if (Manager.MODE == 0) saveAction();
    }
}
