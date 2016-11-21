package rule;

import building.Room;
import domain.Debugger;
import entity.HVAC;
import entity.HVAC.State;
import iot.Manager;
import iot.Sensor;

class NormalHVAC extends HVACRule {

	NormalHVAC(Room r, HVAC h, Sensor temp, Sensor hum) {
        super(r, h, temp, hum);
	}


    private boolean workingHours() {
        return Manager.CURRENT_STEP >= 2520 && Manager.CURRENT_STEP <= 6840;
    }
	
	@Override
	public boolean evaluate() {
		State st = hvac.getCurrentState();

        if (st.equals(State.OFF)) {
            moderateTemperature();
            if (workingHours()) return true;
        }

        if (st.equals(State.ON)) {
            adjustTemperature();
            if (workingHours() && currentTemperatureOK()) return true;
            if (!workingHours()) return true;
        }

        if (st.equals(State.SUSPEND)) {
            suspendTemperature();
            if (workingHours() && reactivateFromSuspend()) return true;
            if (!workingHours()) return true;
        }
		return false;
	}


    @Override
	public void execute() throws Exception {
		State st = hvac.getCurrentState();
        if (workingHours()) {
            if (st.equals(State.OFF)) {
                if (Debugger.isEnabled()) Debugger.log("HVAC switched ON in room " + room.getLocation());
                hvac.setCurrentState(State.ON);
            }
            else if (st.equals(State.ON)) {
                if (Debugger.isEnabled()) Debugger.log("HVAC SUSPENDED in room " + room.getLocation());
                hvac.setCurrentState(State.SUSPEND);
            }
            else if (st.equals(State.SUSPEND)) {
                if (Debugger.isEnabled())
                    Debugger.log("HVAC switched from SUSPENDED to ON in room " + room.getLocation());
                hvac.setCurrentState(State.ON);
            }
        }
        else {
            if (Debugger.isEnabled()) Debugger.log("HVAC switched OFF in room " + room.getLocation());
            hvac.setCurrentState(State.OFF);
        }
	}
}
