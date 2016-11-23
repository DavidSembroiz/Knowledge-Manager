package rule;

import building.Room;
import domain.Debugger;
import entity.Lamp;
import entity.Lamp.State;
import iot.Sensor;
import model.Weather;
import org.easyrules.core.BasicRule;

class LampRule extends BasicRule {

	private Room room;
	private Weather models;
	
	private Sensor luminosity;
	private Lamp lamp;

	
	LampRule(Room r, Lamp l, Sensor light) {
        super("Lamp rule #" + l.getId(), "Rule to manage Lamps", l.getId());
        models = Weather.getInstance();
		this.room = r;
		this.lamp = l;
		this.luminosity = light;
	}
	
	
	private boolean environmentalLightOK() {
        int ENVIRONMENTAL_LIGHT_THRESHOLD = 500;
		double modelValue = models.getCurrentEnvironmentalLight();
		return modelValue > ENVIRONMENTAL_LIGHT_THRESHOLD;
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
	
	@Override
	public void execute() throws Exception {
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
