package iot;

import java.util.ArrayList;

import rules.RuleManager;

public class Room {
	
	
	private String location;
	private ArrayList<Sensor> sensors;
	private RuleManager ruleManager;
	
	public Room(String location) {
		this.location = location;
		sensors = new ArrayList<Sensor>();
		this.ruleManager = new RuleManager();
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
		ruleManager.registerRules(s);
		return s;
	}

	public Sensor getSensor(String soID, String type) {
		Sensor s = sensorExists(soID, type); 
		if (s == null) s = registerSensor(soID, type);
		return s;
	}

	public void fireRules() {
		ruleManager.fireRules();
	}
	
}
