package rules;

import java.util.ArrayList;

import org.easyrules.annotation.*;

import iot.Sensor;

@Rule(name = "Window Management Rule")
public class WindowRule {
	
	
	private Sensor window;
	
	//TODO change to an actual actuator
	private String actuator;
	
	public WindowRule() {
	}
	
	public ArrayList<String> getNecessarySensors() {
		ArrayList<String> ret = new ArrayList<String>();
		if (window == null) ret.add("window");
		return ret;
	}
	
	public void setSensor(String ruleSens, Sensor s) {
		if (ruleSens.equals("window")) {
			window = s;
		}
	}
	
	@Condition
	public boolean checkWindow() {
		int val = Integer.parseInt(window.getValue());
		return val > 0;
	}
	
	@Action(order = 1)
	public void switchOffLight() throws Exception {
		System.out.println("Window triggered");
	}
}
