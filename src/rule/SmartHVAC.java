package rule;

import building.Room;
import domain.Debugger;
import entity.HVAC;
import entity.HVAC.State;
import iot.Sensor;

public class SmartHVAC extends HVACRule {
	
	public SmartHVAC(Room r, HVAC h, Sensor temp, Sensor hum) {
        super(r, h, temp, hum);
	}
	
	
	@Override
	public boolean evaluate() {
		State st = hvac.getCurrentState();
		
		if (st.equals(State.OFF)) {
            moderateTemperature();
			if ((room.arePeopleInside() || room.arePeopleComing(PREDICTION_THRESHOLD))
                    && !temperatureOK()) return true;
		}
		
		if (st.equals(State.ON)) {
            adjustTemperature();
			if (currentTemperatureOK()) return true;
			else if (room.isEmpty() && !room.arePeopleComing(PREDICTION_THRESHOLD)) return true;
		}

		if (st.equals(State.SUSPEND)) {
            suspendTemperature();
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
			if (room.isEmpty()) {
                if (Debugger.isEnabled()) Debugger.log("HVAC switched OFF in room " + room.getLocation());
                hvac.setCurrentState(State.OFF);
            }
			else {
                if (Debugger.isEnabled()) Debugger.log("HVAC SUSPENDED in room " + room.getLocation());
                hvac.setCurrentState(State.SUSPEND);
            }
		}
		else if (st.equals(State.SUSPEND)) {
            if (room.isEmpty()) {
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
