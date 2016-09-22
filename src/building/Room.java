package building;

import java.util.ArrayList;

import behaviour.Person;
import domain.Database;
import iot.Actuator;
import iot.Sensor;
import rules.RuleManager;

public class Room {
	
	/**
	 * Current value used to know when all room sensors have been filled
	 */
	
	
	private String location;
	private String size;
	private ArrayList<Sensor> sensors;
	private ArrayList<Actuator> actuators;
	private RuleManager ruleManager;
	private ArrayList<Person> people;
	
	public Room(String location, String size) {
		this.location = location;
		this.size = size;
		sensors = new ArrayList<Sensor>();
		actuators = new ArrayList<Actuator>();
		this.ruleManager = new RuleManager(this);
	}
	
	public RuleManager getRuleManager() {
		return ruleManager;
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
	
	public ArrayList<Person> getPeople() {
		return people;
	}

	public void setPeople(ArrayList<Person> people) {
		this.people = people;
	}

	public boolean sensorExists(String soID, String type) {
		for (Sensor s : sensors) {
			if (s.getSoID().equals(soID) && s.getType().equals(type)) return true;
		}
		return false;
	}
	
	private Sensor assignCloudId(String soID, String type, String location) {
		Sensor s = new Sensor(soID, type);
		sensors.add(s);
		return s;
	}
	

	public Sensor getSensor(String soID, String type) {
		for (Sensor s : sensors) {
			if (s.getSoID().equals(soID) && s.getType().equals(type)) return s;
		}
		return null;
	}

	public void fireRules() {
		ruleManager.fireRules();
	}
	
	public void addSensor(String id, String type, String val) {
		this.sensors.add(new Sensor(id, type, val));
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public ArrayList<Actuator> getActuators() {
		return actuators;
	}

	public void setActuators(ArrayList<Actuator> actuators) {
		this.actuators = actuators;
	}

	public void setRuleManager(RuleManager ruleManager) {
		this.ruleManager = ruleManager;
	}

	public Sensor getSensor(String model) {
		for (Sensor s : sensors) {
			/*
			 * Currently comparing with type, but model needs to be compared
			 * with id so multiple sensors of same type are compatible
			 */
			
			if (s.getType().equals(model)) return s;
		}
		return null;
	}
}
