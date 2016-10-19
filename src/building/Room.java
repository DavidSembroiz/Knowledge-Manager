package building;

import java.util.ArrayList;
import java.util.HashSet;

import behaviour.Person;
import entity.Computer;
import entity.HVAC;
import entity.Lamp;
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
	private HashSet<Object> entities;
	
	public Room(String location, String size) {
		this.location = location;
		this.size = size;
		sensors = new ArrayList<Sensor>();
		actuators = new ArrayList<Actuator>();
		this.entities = new HashSet<>();
		this.ruleManager = new RuleManager(this);
		this.people = new ArrayList<Person>();
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
	

	public Sensor getSensor(String soID, String type) {
		for (Sensor s : sensors) {
			if (s.getSoID().equals(soID) && s.getType().toLowerCase().equals(type)) return s;
		}
		return null;
	}

	public void fireRules() {
		ruleManager.fireRules();
	}
	
	public void addSensor(ArrayList<Sensor> s) {
		for (Sensor sen : s) sensors.add(sen);
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

	public Sensor fetchSensor(String id, String type) {
		for (Sensor s : sensors) {
			/*
			 * Currently comparing with type, but model needs to be compared
			 * with id so multiple sensors of same type are compatible
			 */
			
			if (s.getId().toLowerCase().equals(id) && s.getType().toLowerCase().equals(type)) return s;
		}
		return null;
	}

	public void addEntity(String type, String qtt) {
		for (int i = 0; i < Integer.parseInt(qtt); ++i) {
			if (type.equals("computer")) {
				Computer c = new Computer();
				entities.add(c);
				ruleManager.addComputerRule(c);
				
			}
			else if (type.equals("lamp")) {
				Lamp l = new Lamp();
				entities.add(l);
				ruleManager.addLampRule(l);
			}
			else if (type.equals("hvac")) {
				HVAC hvac = new HVAC();
				entities.add(hvac);
				ruleManager.addHVACRule(hvac);
			}
		}
	}

	public HashSet<Object> getEntities() {
		return entities;
	}

	public void setEntities(HashSet<Object> entities) {
		this.entities = entities;
	}

	public void removePerson(Person p) {
		if (people.contains(p)) people.remove(p);
	}

	public void addPerson(Person p) {
		people.add(p);
	}
	
	
}
