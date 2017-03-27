package rule_headers;

import building.Room;
import entity.Lamp;
import iot.Manager;
import iot.Sensor;
import model.ModelManager;
import org.easyrules.core.BasicRule;

public class LampRule extends BasicRule {

    protected Room room;
	private ModelManager models;
	
	private Sensor luminosity;
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

    protected void saveAction() {
        room.addTimeToSchedule("lamp_" + lamp.getId(), Manager.CURRENT_STEP, lamp.getCurrentState().toString());
    }
}
