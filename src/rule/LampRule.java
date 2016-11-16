package rule;

import domain.Debugger;
import org.easyrules.annotation.*;

import entity.Lamp;
import entity.Lamp.State;
import building.Room;
import iot.Sensor;
import model.Weather;
import org.easyrules.core.BasicRule;

@Rule(name = "Lights Management Rule")
public class LampRule extends BasicRule {

    private int PREDICTION_THRESHOLD = 5;
    private int ENVIRONMENTAL_LIGHT_THRESHOLD = 500;
	
	private Room room;
	private Weather models;
	
	private Sensor luminosity;
	private Lamp lamp;

	
	public LampRule(Room r, Lamp l, Sensor light) {
        super("Lamp rule #" + l.getId(), "Rule to manage Lamps", l.getId());
        models = Weather.getInstance();
		this.room = r;
		this.lamp = l;
		this.luminosity = light;
	}
	
	
	private boolean environmentalLightOK() {
		double modelValue = models.getCurrentEnvironmentalLight();
		return modelValue > ENVIRONMENTAL_LIGHT_THRESHOLD;
	}
	
	@Override
	public boolean evaluate() {

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
