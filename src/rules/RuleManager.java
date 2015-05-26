package rules;

import iot.Sensor;

import java.util.ArrayList;
import java.util.Iterator;

import org.easyrules.api.RulesEngine;

import static org.easyrules.core.RulesEngineBuilder.aNewRulesEngine;

public class RuleManager {
	
	private RulesEngine rulesEngine;
	private ArrayList<Object> unregistered;
	
	public RuleManager() {
		rulesEngine = aNewRulesEngine().build();
		unregistered = new ArrayList<Object>();
		
		unregistered.add(new SwitchOffLight());
		unregistered.add(new AirConditioning());
	}
	
	public void registerRules(Sensor s) {
		Iterator<Object> ito = unregistered.iterator();
		while (ito.hasNext()) {
			Object o = ito.next();
			
			if (o instanceof SwitchOffLight) {
				registerSwitchOffLight(ito, (SwitchOffLight) o, s);
			}
			
			if (o instanceof AirConditioning) {
				registerAirConditioning(ito, (AirConditioning) o, s);
			}
		}
	}
	
	private void registerSwitchOffLight(Iterator<Object> ito, SwitchOffLight sol, Sensor s) {
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
	}
	
	private void registerAirConditioning(Iterator<Object> ito, AirConditioning sol, Sensor s) {
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
	}
	
	public void fireRules() {
		rulesEngine.fireRules();
	}

}
