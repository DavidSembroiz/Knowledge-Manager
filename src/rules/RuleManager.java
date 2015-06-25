package rules;

import domain.Utils;
import iot.Room;

import java.util.ArrayList;

import org.easyrules.api.RulesEngine;

import static org.easyrules.core.RulesEngineBuilder.aNewRulesEngine;

public class RuleManager {
	
	private RulesEngine rulesEngine;
	private Utils uts;
	private Room r;
	
	public RuleManager(Room r) {
		this.r = r;
		this.uts = Utils.getInstance();
		rulesEngine = aNewRulesEngine().build();
	}
	
	
	public void registerRules(ArrayList<RuleDAO> rules) {
		for (RuleDAO rule : rules) {
			if (rule.getRuleName().equals("AirConditioning")) {
				createAirConditioning(rule.getActuator(), rule.getSensors());
			}
			else if (rule.getRuleName().equals("SwitchOffLight")) {
				createSwitchOffLight(rule.getActuator(), rule.getSensors());
			}
		}
	}
	
	private void createAirConditioning(String actuator, String sensors) {
		AirConditioning ac = new AirConditioning(r.getPeople());
		ArrayList<String> necessary = ac.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			ac.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(ac);
		System.out.println("Rule " + ac.getClass().getName() + " registered");
	}
	
	private void createSwitchOffLight(String actuator, String sensors) {
		SwitchOffLight sol = new SwitchOffLight(r.getPeople());
		ArrayList<String> necessary = sol.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			sol.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(sol);
		System.out.println("Rule " + sol.getClass().getName() + " registered");
	}
	
	public void fireRules() {
		rulesEngine.fireRules();
	}

}
