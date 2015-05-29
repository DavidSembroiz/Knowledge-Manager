package iot;

import java.util.ArrayList;

import domain.Database;
import domain.Utils;
import rules.RuleManager;

public class Room {
	
	
	private String location;
	private ArrayList<Sensor> sensors;
	private RuleManager ruleManager;
	private Database awsdb;
	
	public Room(String location, Database awsdb, Utils uts) {
		this.location = location;
		this.awsdb = awsdb;
		sensors = new ArrayList<Sensor>();
		this.ruleManager = new RuleManager(this, uts);
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
	
	private Sensor registerSensor(String soID, String type, String location) {
		Sensor s = new Sensor(soID, type);
		sensors.add(s);
		awsdb.updateAssociations(soID, type, location);
		ruleManager.registerRules(awsdb.getCompletedRules(location));
		return s;
	}

	public Sensor getSensor(String soID, String type) {
		Sensor s = sensorExists(soID, type); 
		if (s == null) s = registerSensor(soID, type, this.location);
		return s;
	}

	public void fireRules() {
		ruleManager.fireRules();
	}
	
}
