package rules;

import domain.Utils;
import iot.Room;

import java.util.ArrayList;

import org.easyrules.api.RulesEngine;

import static org.easyrules.core.RulesEngineBuilder.aNewRulesEngine;

public class RuleManager {
	
	private RulesEngine rulesEngine;
	private ArrayList<Object> unregistered;
	private Utils uts;
	private Room r;
	
	public RuleManager(Room r, Utils uts) {
		this.r = r;
		this.uts = uts;
		rulesEngine = aNewRulesEngine().build();
		unregistered = new ArrayList<Object>();
		
		/*unregistered.add(new SwitchOffLight());
		unregistered.add(new AirConditioning());*/
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
		/*Iterator<Object> ito = unregistered.iterator();
		while (ito.hasNext()) {
			Object o = ito.next();
			
			if (o instanceof SwitchOffLight) {
				registerSwitchOffLight(ito, (SwitchOffLight) o, s);
			}
			
			if (o instanceof AirConditioning) {
				registerAirConditioning(ito, (AirConditioning) o, s);
			}
		}*/
	}
	
	private void createAirConditioning(String actuator, String sensors) {
		AirConditioning ac = new AirConditioning();
		ArrayList<String> necessary = ac.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			ac.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(ac);
		System.out.println("Rule " + ac.getClass().getName() + " registered");
	}
	
	private void createSwitchOffLight(String actuator, String sensors) {
		SwitchOffLight sol = new SwitchOffLight();
		ArrayList<String> necessary = sol.getNecessarySensors();
		for (String n : necessary) {
			String soID = uts.getIdFromType(n, sensors);
			sol.setSensor(n, r.getSensor(soID, n));
		}
		rulesEngine.registerRule(sol);
		System.out.println("Rule " + sol.getClass().getName() + " registered");
	}
	
	/*private void registerSwitchOffLight(Iterator<Object> ito, SwitchOffLight sol, Sensor s) {
		ArrayList<String> sens = sol.getNecessarySensors();
		
		Iterator<String> it = sens.iterator();
		while (it.hasNext()) {
			String ruleSens = it.next();
			if (s.getType().equals(ruleSens)) {
				sol.setSensor(ruleSens, s);
				it.remove();
			}
		}
		
		if (sens.isEmpty()) {
			rulesEngine.registerRule(sol);
			ito.remove();
			System.out.println("Rule " + sol.getClass().getName() + " registered");
		}
	}*/
	
	public void fireRules() {
		rulesEngine.fireRules();
	}

}
