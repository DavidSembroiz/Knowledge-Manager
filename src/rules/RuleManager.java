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
	
	public RulesEngine getRulesEngine() {
		return rulesEngine;
	}
	
	
	public void registerRules(ArrayList<RuleDAO> rules) {
		for (RuleDAO rule : rules) {
			if (rule.getRuleName().equals("HVAC")) {
				createHVACRule(rule.getActuator(), rule.getSensors());
			}
			else if (rule.getRuleName().equals("Lights")) {
				createLightsRule(rule.getActuator(), rule.getSensors());
			}
			else if (rule.getRuleName().equals("Door")) {
				createDoorRule(rule.getActuator(), rule.getSensors());
			}
			else if (rule.getRuleName().equals("Window")) {
				createWindowRule(rule.getActuator(), rule.getSensors());
			}
			else if (rule.getRuleName().equals("Computer")) {
				createComputerRule(rule.getActuator(), rule.getSensors());
			}
		}
	}
	
	private void createHVACRule(String actuator, String sensors) {
		HVACRule ac = new HVACRule(r.getPeople());
		ArrayList<String> necessary = ac.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			ac.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(ac);
		System.out.println("Rule " + ac.getClass().getName() + " registered");
	}
	
	private void createLightsRule(String actuator, String sensors) {
		LightsRule sol = new LightsRule(r.getPeople());
		ArrayList<String> necessary = sol.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			sol.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(sol);
		System.out.println("Rule " + sol.getClass().getName() + " registered");
	}
	
	private void createDoorRule(String actuator, String sensors) {
		DoorRule d = new DoorRule(r.getPeople());
		ArrayList<String> necessary = d.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			d.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(d);
		System.out.println("Rule " + d.getClass().getName() + " registered");
	}
	
	private void createWindowRule(String actuator, String sensors) {
		WindowRule w = new WindowRule(r.getPeople());
		ArrayList<String> necessary = w.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			w.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(w);
		System.out.println("Rule " + w.getClass().getName() + " registered");
	}
	
	private void createComputerRule(String actuator, String sensors) {
		ComputerRule c = new ComputerRule(r.getPeople());
		ArrayList<String> necessary = c.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			c.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(c);
		System.out.println("Rule " + c.getClass().getName() + " registered");
	}
	
	public void fireRules() {
		rulesEngine.fireRules();
	}

}
