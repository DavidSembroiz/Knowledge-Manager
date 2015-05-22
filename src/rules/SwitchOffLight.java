package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import iot.Sensor;

@Rule(name = "Switch off light")
public class SwitchOffLight {

	private Sensor luminosity;
	
	public SwitchOffLight(Sensor s) {
		this.luminosity = s;
	}
	
	public ArrayList<String> getNecessaryTypes() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("luminosity");
		return ret;
	}
	
	@Condition
	public boolean checkLuminosity() {
		int val = Integer.parseInt(luminosity.getValue());
		return val > 500;
	}
	
	@Action(order = 1)
	public void switchOffLight() throws Exception {
		//Trigger actuator
	}
}
