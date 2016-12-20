package rule;

import building.Room;
import entity.Lamp;
import iot.Sensor;
import model.ModelManager;
import org.easyrules.core.BasicRule;

class LampRule extends BasicRule {

    Room room;
	private ModelManager models;
	
	private Sensor luminosity;
    Lamp lamp;

	
	LampRule(Room r, Lamp l, Sensor light) {
        super("Lamp rule #" + l.getId(), "Rule to manage Lamps", l.getId());
        models = ModelManager.getInstance();
		this.room = r;
		this.lamp = l;
		this.luminosity = light;
	}
	
	
    boolean environmentalLightOK() {
        int ENVIRONMENTAL_LIGHT_THRESHOLD = 500;
		double modelValue = models.getCurrentEnvironmentalLight();
		return modelValue > ENVIRONMENTAL_LIGHT_THRESHOLD;
	}
}
