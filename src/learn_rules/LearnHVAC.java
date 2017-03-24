package learn_rules;

import building.Room;
import entity.HVAC;
import entity.HVAC.State;
import entity.Window;
import iot.Sensor;
import rule_headers.HVACRule;

public class LearnHVAC extends HVACRule {

	public LearnHVAC(Room r, HVAC h, Window w, Sensor temp, Sensor hum) {
        super(r, h, w, temp, hum);
	}
	
	
	@Override
	public boolean evaluate() {
        sampleTemperature();
	    HVAC hvac = super.hvac;
	    Room room = super.room;
	    Window window = super.window;
        int PREDICTION_THRESHOLD = getPredictionThreshold();
		State st = hvac.getCurrentState();

        if (st.equals(State.OFF)) {
            moderateTemperature();
			if ((room.arePeopleInside() || room.arePeopleComing(PREDICTION_THRESHOLD))
                    && !temperatureOK() && !window.isOpen()) return true;
		}
		
		if (st.equals(State.ON)) {
            adjustTemperature();
			if (currentTemperatureOK() || window.isOpen()) return true;
			else if (room.isEmpty() && !room.arePeopleComing(PREDICTION_THRESHOLD)) return true;
		}

		if (st.equals(State.SUSPEND)) {
            suspendTemperature();
            if (window.isOpen()) return true;
            if (!room.isEmpty() && reactivateFromSuspend()) return true;
            if (room.isEmpty() && !room.arePeopleComing(PREDICTION_THRESHOLD)) return true;
        }
		return false;
	}


    @Override
	public void execute() throws Exception {
	}
}
