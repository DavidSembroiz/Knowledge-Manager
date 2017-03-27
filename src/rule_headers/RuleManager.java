package rule_headers;

import building.Room;
import entity.*;
import iot.Manager;
import iot.Sensor;
import learn_rules.LearnComputer;
import learn_rules.LearnHVAC;
import learn_rules.LearnLamp;
import normal_rules.NormalComputer;
import normal_rules.NormalHVAC;
import normal_rules.NormalLamp;
import org.easyrules.api.RulesEngine;
import smart_rules.SmartComputer;
import smart_rules.SmartHVAC;
import smart_rules.SmartLamp;

import java.util.ArrayList;

import static org.easyrules.core.RulesEngineBuilder.aNewRulesEngine;

public class RuleManager {

    private final String DEFAULT_TEMPERATURE = "20";
    private final String DEFAULT_LIGHT = "200";
    private final String DEFAULT_HUMIDITY = "30";
    private final String DEFAULT_AIR_QUALITY = "4";

	
	private RulesEngine rulesEngine;
	private Room r;
	
	public RuleManager(Room r) {
		this.r = r;
		rulesEngine = aNewRulesEngine().build();
	}
	
	public void fireRules() {
		rulesEngine.fireRules();
	}

	public void addComputerRule(Computer c) {
        ArrayList<Sensor> sens = r.getSensors();
		for (Sensor s : sens) {
			if (!s.isAssigned() && s.getType().toLowerCase().equals("power")) {
                s.setAssigned();
                switch (Manager.MODE) {
                    case 0:
                    case 1:
                        rulesEngine.registerRule(new SmartComputer(r, c, s));
                        break;
                    case 2:
                        rulesEngine.registerRule(new NormalComputer(r, c, s));
                        break;
                    case 3:
                        rulesEngine.registerRule(new LearnComputer(r, c, s));
                        break;
                }
                return;
			}
		}
	}
	
	public void addHVACRule(HVAC h, Window w) {
		ArrayList<Sensor> sens = r.getSensors();
		Sensor temp = null, hum = null;
		for (Sensor s : sens) {
			if (!s.isAssigned()) {
				if (s.getType().equals("temperature")) temp = s;
				else if (s.getType().equals("humidity")) hum = s;

                if (temp != null && hum != null) {
                    temp.setValue(DEFAULT_TEMPERATURE);
                    temp.setAssigned();
                    hum.setValue(DEFAULT_HUMIDITY);
                    hum.setAssigned();
                    w.setAssigned();
                    switch (Manager.MODE) {
                        case 0:
                        case 1:
                            rulesEngine.registerRule(new SmartHVAC(r, h, w, temp, hum));
                            break;
                        case 2:
                            rulesEngine.registerRule(new NormalHVAC(r, h, w, temp, hum));
                            break;
                        case 3:
                            rulesEngine.registerRule(new LearnHVAC(r, h, w, temp, hum));
                            break;
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
                s.setValue(DEFAULT_LIGHT);
                s.setAssigned();
                switch (Manager.MODE) {
                    case 0:
                    case 1:
                        rulesEngine.registerRule(new SmartLamp(r, l, s));
                        break;
                    case 2:
                        rulesEngine.registerRule(new NormalLamp(r, l, s));
                        break;
                    case 3:
                        rulesEngine.registerRule(new LearnLamp(r, l, s));
                        break;
                }
                return;
            }
        }
    }

    public void addDoorRule(Door d) {
        ArrayList<Sensor> sens = r.getSensors();
        for (Sensor s : sens) {
            if (!s.isAssigned() && s.getType().toLowerCase().equals("electromagnetic")) {
                DoorRule dr = new DoorRule(r, d, s);
                s.setAssigned();
                rulesEngine.registerRule(dr);
                return;
            }
        }
    }

    public void addWindowRule(Window w) {
        ArrayList<Sensor> sens = r.getSensors();
        for (Sensor s : sens) {
            if (!s.isAssigned() && s.getType().toLowerCase().equals("airquality")) {
                s.setValue(DEFAULT_AIR_QUALITY);
                s.setAssigned();
                WindowRule wr = new WindowRule(r, w, s);
                rulesEngine.registerRule(wr);
                return;
            }
        }
    }
}
