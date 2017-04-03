package rule_headers;

import building.Room;
import entity.Lamp;
import iot.Manager;
import iot.Sensor;
import model.ModelManager;
import org.easyrules.core.BasicRule;

public class LampRule extends BasicRule {

    protected int PREDICTION_THRESHOLD = 0;

    protected Room room;
	private ModelManager models;
	
	protected Sensor luminosity;
    protected Lamp lamp;

	
	public LampRule(Room r, Lamp l, Sensor light) {
        super("Lamp rule_headers #" + l.getId(), "Rule to manage Lamps", l.getId());
        models = ModelManager.getInstance();
		this.room = r;
		this.lamp = l;
		this.luminosity = light;
	}

    protected boolean environmentalLightOK() {
        int ENVIRONMENTAL_LIGHT_THRESHOLD = 500;
		double modelValue = models.getCurrentEnvironmentalLight();
		return modelValue > ENVIRONMENTAL_LIGHT_THRESHOLD;
	}

	protected void adjustRoomLight() {
	    if (lamp.getCurrentState().equals(Lamp.State.ON)) this.luminosity.setValue(Integer.toString(2000));
        else this.luminosity.setValue(Double.toString(models.getCurrentEnvironmentalLight()));
    }

    protected void saveAction() {
        room.addTimeToSchedule("lamp_" + lamp.getId(), Manager.CURRENT_STEP, lamp.getCurrentState().toString());
    }
}
