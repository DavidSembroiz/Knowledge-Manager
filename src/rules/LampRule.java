package rules;

import org.easyrules.annotation.*;

import entity.Lamp;
import building.Room;
import iot.Sensor;
import models.Weather;

@Rule(name = "Lights Management Rule")
public class LampRule {
	
	private Room room;
	private Weather models;
	
	private Sensor luminosity;
	private Lamp lamp;
	
	private String light;
	
	
	public LampRule(Room r, Lamp l, Sensor light) {
		models = Weather.getInstance();
		this.room = r;
		this.lamp = l;
		this.luminosity = light;
	}
	
	
	private boolean environmentalLightOK() {
		int threshold = 500;
		double modelValue = models.getCurrentEnvironmentalLight();
		return modelValue > threshold;
	}
	
	@Condition
	public boolean checkLuminosity() {
		
		/**
		 * If light is ON:
		 *  - OFF: room is empty or environmental light is OK
		 * 
		 * If light is OFF:
		 *  - ON: someone has entered the room or the environmental light is BAD
		 */
		
		return true;
	}
	
	@Action(order = 1)
	public void changeState() throws Exception {
		
	}
}
