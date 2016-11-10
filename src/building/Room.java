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

    public ArrayList<Person> getPeopleComing() {
        return peopleComing;
    }

    public enum ROOM_TYPE {
        OFFICE, MEETING_ROOM, CLASSROOM, UNDEFINED
    }

	private String location;
	private String size;
    private ROOM_TYPE type;
	private ArrayList<Sensor> sensors;
	private ArrayList<Actuator> actuators;
	private RuleManager ruleManager;
	private ArrayList<Person> peopleActing;
    private ArrayList<Person> peopleComing;
	private HashSet<Object> entities;
	
	public Room(String location, String size, String type) {
		this.location = location;
		this.size = size;
        this.type = ROOM_TYPE.valueOf(type.toUpperCase());
		sensors = new ArrayList<>();
		actuators = new ArrayList<>();
		this.entities = new HashSet<>();
		this.ruleManager = new RuleManager(this);
		this.peopleActing = new ArrayList<>();
        this.peopleComing = new ArrayList<>();
	}


	public String getLocation() {
		return location;
	}

	public ArrayList<Sensor> getSensors() {
		return sensors;
	}

    public ROOM_TYPE getType() {
        return type;
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

	public String getSize() {
		return size;
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
        ArrayList<Computer> comps = new ArrayList();
		for (int i = 0; i < Integer.parseInt(qtt); ++i) {
			if (type.equals("computer")) {
                Computer c = new Computer(i);
				comps.add(c);
				entities.add(c);
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
		ruleManager.addComputerRule(comps);
	}

	public HashSet<Object> getEntities() {
		return entities;
	}

    public ArrayList<Person> getPeopleActing() {
        return peopleActing;
    }

    public void removePerson(Person p) {
		if (peopleActing.contains(p)) peopleActing.remove(p);
	}

	public void addPerson(Person p) {
		peopleComing.add(p);
	}

    public boolean arePeopleComing(int threshold) {

        /*
         * If someone comes in the next threshold steps, some actions might be anticipated
         */

        for (Person p : peopleComing) {
            if (p.getNextActionSteps() < threshold) return true;
        }
        return false;
    }

    public boolean arePeopleInside() {
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
