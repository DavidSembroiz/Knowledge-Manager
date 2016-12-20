package rule;

import building.Room;
import domain.Debugger;
import entity.HVAC;
import entity.Window;
import iot.Manager;
import iot.Sensor;

class NormalHVAC extends HVACRule {

	NormalHVAC(Room r, HVAC h, Window w, Sensor temp, Sensor hum) {
        super(r, h, w, temp, hum);
	}


    private boolean workingHours() {
        return Manager.CURRENT_STEP >= 2520 && Manager.CURRENT_STEP <= 6840;
    }
	
	@Override
	public boolean evaluate() {
        HVAC.State st = hvac.getCurrentState();

        if (st.equals(HVAC.State.OFF)) {
            moderateTemperature();
            if (workingHours()) return true;
        }

        if (st.equals(HVAC.State.ON)) {
            adjustTemperature();
            if (window.isOpen()) return true;
            if (workingHours() && currentTemperatureOK()) return true;
            if (!workingHours()) return true;
        }

        if (st.equals(HVAC.State.SUSPEND)) {
            suspendTemperature();
            if (window.isOpen()) return true;
            if (workingHours() && reactivateFromSuspend()) return true;
            if (!workingHours()) return true;
        }
		return false;
	}


    @Override
	public void execute() throws Exception {
        HVAC.State st = hvac.getCurrentState();
        if (workingHours()) {
            if (st.equals(HVAC.State.OFF)) {
                if (Debugger.isEnabled()) Debugger.log("HVAC switched ON in room " + room.getLocation());
                hvac.setCurrentState(HVAC.State.ON);
            }
            else if (st.equals(HVAC.State.ON)) {

                if (window.isOpen()) {
                    if (Debugger.isEnabled())
                        Debugger.log("HVAC switched from ON to OFF in room " + room.getLocation());
                    hvac.setCurrentState(HVAC.State.OFF);
                }
                else {
                    if (Debugger.isEnabled()) Debugger.log("HVAC SUSPENDED in room " + room.getLocation());
                    hvac.setCurrentState(HVAC.State.SUSPEND);
                }
            }
            else if (st.equals(HVAC.State.SUSPEND)) {

                if (window.isOpen()) {
                    if (Debugger.isEnabled())
                        Debugger.log("HVAC switched from SUSPENDED to OFF in room " + room.getLocation());
                    hvac.setCurrentState(HVAC.State.OFF);
                }
                else {
                    if (Debugger.isEnabled())
                        Debugger.log("HVAC switched from SUSPENDED to ON in room " + room.getLocation());
                    hvac.setCurrentState(HVAC.State.ON);
                }
            }
        }
        else {
            if (Debugger.isEnabled()) Debugger.log("HVAC switched OFF in room " + room.getLocation());
            hvac.setCurrentState(HVAC.State.OFF);
        }
	}
}
