package rule;

import building.Room;
import entity.*;
import iot.Manager;
import iot.Sensor;
import org.easyrules.api.RulesEngine;

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
                s.setAssigned(true);
                SmartComputer cr;
                NormalComputer crn;
                if (Manager.MODE == 2) {
                    crn = new NormalComputer(r, c, s);
                    rulesEngine.registerRule(crn);
                }
                else {
                    cr = new SmartComputer(r, c, s);
                    rulesEngine.registerRule(cr);
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
                    temp.setAssigned(true);
                    hum.setValue(DEFAULT_HUMIDITY);
                    hum.setAssigned(true);
                    w.setAssigned(true);

                    SmartHVAC hr;
                    NormalHVAC hrn;
                    if (Manager.MODE == 2) {
                        hrn = new NormalHVAC(r, h, w, temp, hum);
                        rulesEngine.registerRule(hrn);
                    }
                    else {
                        hr = new SmartHVAC(r, h, w, temp, hum);
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
                s.setValue(DEFAULT_LIGHT);
                s.setAssigned(true);
                NormalLamp ln;
                SmartLamp sln;
                if (Manager.MODE == 2) {
                    ln = new NormalLamp(r, l, s);
                    rulesEngine.registerRule(ln);
                }
                else {
                    sln = new SmartLamp(r, l, s);
                    rulesEngine.registerRule(sln);
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
                s.setAssigned(true);
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
                s.setAssigned(true);
                WindowRule wr = new WindowRule(r, w, s);
                rulesEngine.registerRule(wr);
                return;
            }
        }
    }
}
