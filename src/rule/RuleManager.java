package rule;

import building.Room;
import domain.Utils;
import entity.Computer;
import entity.Door;
import entity.HVAC;
import entity.Lamp;
import iot.Manager;
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
                return;
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

                if (temp != null && hum != null) {
                    temp.setValue(DEFAULT_TEMPERATURE);
                    temp.setAssigned(true);
                    hum.setValue(DEFAULT_HUMIDITY);
                    hum.setAssigned(true);

                    SmartHVAC hr;
                    NormalHVAC hrn;
                    if (Manager.MODE == 2) {
                        hrn = new NormalHVAC(r, h, temp, hum);
                        rulesEngine.registerRule(hrn);
                    }
                    else {
                        hr = new SmartHVAC(r, h, temp, hum);
                        rulesEngine.registerRule(hr);
                    }
                    return;
                }
			}
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
                return;
            }
        }
    }

    public void addDoorRule(Door d) {
        ArrayList<Sensor> sens = r.getSensors();
        for (Sensor s : sens) {
            if (!s.isAssigned() && s.getType().toLowerCase().equals("electromagnetic")) {
                DoorRule dr = new DoorRule(r, d, s);
                s.setAssigned(true);
                rulesEngine.registerRule(dr);
                return;
            }
        }
    }

}
