package iot;

import java.util.*;
import rules.*;

import org.easyrules.api.Rule;
import org.easyrules.api.RulesEngine;

import static org.easyrules.core.RulesEngineBuilder.aNewRulesEngine;

public class Room {
	
	private static final int NOT_FOUND = -1;
	
	private String location;
	private ArrayList<Sensor> sensors;
	private RulesEngine rulesEngine;
	private ArrayList<Object> unregistered;
	
	public Room(String location) {
		this.location = location;
		sensors = new ArrayList<Sensor>();
		rulesEngine = aNewRulesEngine().build();
		unregistered = new ArrayList<Object>();
		unregistered.add(new SwitchOffLight(null));
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ArrayList<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(ArrayList<Sensor> sensors) {
		this.sensors = sensors;
	}
	
	private Sensor sensorExists(String soID, String type) {
		for (Sensor s : sensors) {
			if (s.getSoID().equals(soID) && s.getType().equals(type)) return s;
		}
		return null;
	}
	
	private Sensor registerSensor(String soID, String type) {
		Sensor s = new Sensor(soID, type);
		sensors.add(s);
		checkRules();
		return s;
	}
	
	private void checkRules() {
		for (Object o : unregistered) {
			
		}
	}

	public Sensor getSensor(String soID, String type) {
		Sensor s = sensorExists(soID, type); 
		if (s == null) s = registerSensor(soID, type);
		return s;
	}
	
}
