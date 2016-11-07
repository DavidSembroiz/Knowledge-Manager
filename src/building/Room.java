package building;

import behaviour.Person;
import domain.Debugger;
import entity.Computer;
import entity.Door;
import entity.HVAC;
import entity.Lamp;
import iot.Actuator;
import iot.Sensor;
import rules.RuleManager;

import java.util.ArrayList;
import java.util.HashSet;

public class Room {
	
	/**
	 * Current value used to know when all room sensors have been filled
	 */
	
	
	private String location;
	private String size;
	private ArrayList<Sensor> sensors;
	private ArrayList<Actuator> actuators;
	private RuleManager ruleManager;
	private ArrayList<Person> peopleActing;
    private ArrayList<Person> peopleComing;
	private HashSet<Object> entities;
	
	public Room(String location, String size) {
		this.location = location;
		this.size = size;
		sensors = new ArrayList<Sensor>();
		actuators = new ArrayList<Actuator>();
		this.entities = new HashSet<>();
		this.ruleManager = new RuleManager(this);
		this.peopleActing = new ArrayList<Person>();
        this.peopleComing = new ArrayList<Person>();
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
            else if (type.equals("door")) {
                Door door = new Door();
                entities.add(door);
                ruleManager.addDoorRule(door);
            }
		}
	}

	public HashSet<Object> getEntities() {
		return entities;
	}

	public void setEntities(HashSet<Object> entities) {
		this.entities = entities;
	}

    public ArrayList<Person> getPeopleActing() {
        return peopleActing;
    }

    public void setPeopleActing(ArrayList<Person> peopleActing) {
        this.peopleActing = peopleActing;
    }

    public ArrayList<Person> getPeopleComing() {
        return peopleComing;
    }

    public void setPeopleComing(ArrayList<Person> peopleComing) {
        this.peopleComing = peopleComing;
    }

    public void removePerson(Person p) {
		if (peopleActing.contains(p)) peopleActing.remove(p);
	}

	public void addPerson(Person p) {
		peopleComing.add(p);
	}

    public boolean isPeopleComing() {

        /**
         * If someone comes in the next 5 minutes, some actions might be anticipated
         */

        for (Person p : peopleComing) {
            if (p.getNextActionSteps() < 30) return true;
        }
        return false;
    }

    public boolean isPeopleInside() {
        return peopleActing.size() > 0;
    }

    public boolean isEmpty() {
        return peopleActing.size() == 0;
    }

    public void shiftPerson(Person p) {
        if (peopleComing.contains(p)) {
            if (Debugger.isEnabled()) Debugger.log("Person " + p.getName() + " shifted to ACTING " + this.getLocation());
            peopleComing.remove(p);
            peopleActing.add(p);
        }
    }
}
