package rule;

import building.Room;
import domain.Debugger;
import entity.HVAC;
import entity.HVAC.State;
import entity.Window;
import iot.Sensor;

class SmartHVAC extends HVACRule {
	
	SmartHVAC(Room r, HVAC h, Window w, Sensor temp, Sensor hum) {
        super(r, h, w, temp, hum);
	}
	
	
	@Override
	public boolean evaluate() {
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
		State st = hvac.getCurrentState();
		if (st.equals(State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("HVAC switched ON in room " + room.getLocation());
            hvac.setCurrentState(State.ON);
        }
		else if (st.equals(State.ON)) {
			if (room.isEmpty() || window.isOpen()) {
                if (Debugger.isEnabled()) Debugger.log("HVAC switched OFF in room " + room.getLocation());
                hvac.setCurrentState(State.OFF);
            }
			else {
                if (Debugger.isEnabled()) Debugger.log("HVAC SUSPENDED in room " + room.getLocation());
                hvac.setCurrentState(State.SUSPEND);
            }
		}
		else if (st.equals(State.SUSPEND)) {
            if (room.isEmpty() || window.isOpen()) {
                if (Debugger.isEnabled())
                    Debugger.log("HVAC switched from SUSPENDED to OFF in room " + room.getLocation());
                hvac.setCurrentState(State.OFF);
            }
            else {
                if (Debugger.isEnabled())
                    Debugger.log("HVAC switched from SUSPENDED to ON in room " + room.getLocation());
                hvac.setCurrentState(State.ON);
            }
        }
	}
}
