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
	private int size;
	private ArrayList<Sensor> sensors;
	private ArrayList<Actuator> actuators;
	private RuleManager ruleManager;
	private ArrayList<Person> people;
	
	public Room(String location, int size) {
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

	private Sensor sensorExists(String soID, String type) {
		for (Sensor s : sensors) {
			if (s.getSoID().equals(soID) && s.getType().equals(type)) return s;
		}
		return null;
	}
	
	private Sensor registerSensor(String soID, String type, String location) {
		Sensor s = new Sensor(soID, type);
		sensors.add(s);
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
