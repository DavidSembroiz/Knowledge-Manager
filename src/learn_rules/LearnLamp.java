package learn_rules;

import building.Room;
import entity.Lamp;
import iot.Sensor;
import rule_headers.LampRule;

public class LearnLamp extends LampRule {

    public LearnLamp(Room r, Lamp l, Sensor light) {
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
        Lamp lamp = super.lamp;
        Room room = super.room;
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
    }
}
