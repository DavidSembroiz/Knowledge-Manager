package rules;

import building.Room;
import domain.Utils;
import entity.Computer;
import entity.HVAC;
import entity.Lamp;
import iot.Sensor;
import org.easyrules.api.RulesEngine;

import java.util.ArrayList;

import static org.easyrules.core.RulesEngineBuilder.aNewRulesEngine;

public class RuleManager {

    private final String DEFAULT_TEMPERATURE = "20";
    private final String DEFAULT_LIGHT = "200";
    private final String DEFAULT_HUMIDITY = "30";

	
	private RulesEngine rulesEngine;
	private Utils uts;
	private Room r;
	
	public RuleManager(Room r) {
		this.r = r;
		this.uts = Utils.getInstance();
		rulesEngine = aNewRulesEngine().build();
	}
	
	public RulesEngine getRulesEngine() {
		return rulesEngine;
	}
	
	
	public void fireRules() {
		rulesEngine.fireRules();
	}

	public void addComputerRule(Computer c) {
		ArrayList<Sensor> sens = r.getSensors();
		for (Sensor s : sens) {
			if (!s.isAssigned() && s.getType().toLowerCase().equals("power")) {
				ComputerRule cr = new ComputerRule(r, c, s);
				s.setAssigned(true);
				rulesEngine.registerRule(cr);
			}
		}
	}
	
	public void addHVACRule(HVAC h) {
		ArrayList<Sensor> sens = r.getSensors();
		Sensor temp = null, hum = null;
		for (Sensor s : sens) {
			if (!s.isAssigned()) {
				if (s.getType().equals("temperature")) temp = s;
				else if (s.getType().equals("humidity")) hum = s;
			}
		}
		if (temp != null && hum != null) {
			HVACRule hr = new HVACRule(r, h, temp, hum);
            temp.setValue(DEFAULT_TEMPERATURE);
			temp.setAssigned(true);
            hum.setValue(DEFAULT_HUMIDITY);
			hum.setAssigned(true);
			rulesEngine.registerRule(hr);
		}
	}

	public void addLampRule(Lamp l) {
		ArrayList<Sensor> sens = r.getSensors();
		for (Sensor s : sens) {
			if (!s.isAssigned() && s.getType().toLowerCase().equals("luminosity")) {
				LampRule lr = new LampRule(r, l, s);
                s.setValue(DEFAULT_LIGHT);
				s.setAssigned(true);
				rulesEngine.registerRule(lr);
			}
		}
	}

}
