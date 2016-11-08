package rules;

import domain.Debugger;
import org.easyrules.annotation.*;

import entity.Lamp;
import entity.Lamp.State;
import building.Room;
import iot.Sensor;
import models.Weather;

@Rule(name = "Lights Management Rule")
public class LampRule {

    private int PREDICTION_THRESHOLD = 5;
    private int ENVIRONMENTAL_LIGHT_THRESHOLD = 500;
	
	private Room room;
	private Weather models;
	
	private Sensor luminosity;
	private Lamp lamp;

	
	public LampRule(Room r, Lamp l, Sensor light) {
		models = Weather.getInstance();
		this.room = r;
		this.lamp = l;
		this.luminosity = light;
	}
	
	
	private boolean environmentalLightOK() {
		double modelValue = models.getCurrentEnvironmentalLight();
		return modelValue > ENVIRONMENTAL_LIGHT_THRESHOLD;
	}
	
	@Condition
	public boolean checkLuminosity() {

		/*
		 * If light is ON:
		 *  - OFF: room is empty or environmental light is OK
		 * 
		 * If light is OFF:
		 *  - ON: people inside or coming to the room and environmental light is bad
		 */

		State st = lamp.getCurrentState();
        if (st.equals(State.OFF)) {
            if ((room.arePeopleInside() || room.arePeopleComing(PREDICTION_THRESHOLD))
                    && !environmentalLightOK()) return true;
        }
        if (st.equals(State.ON)) {
            if (room.isEmpty() || environmentalLightOK()) return true;
        }
		return false;
	}
	
	@Action(order = 1)
	public void changeState() throws Exception {
        State st = lamp.getCurrentState();
        if (st.equals(State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("Lamp switched ON in room " + room.getLocation());
            lamp.setCurrentState(State.ON);
        }
        else if (st.equals(State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("Lamp switched OFF in room " + room.getLocation());
            lamp.setCurrentState(State.ON);
        }
	}
}
