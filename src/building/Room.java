package building;

import behaviour.Person;
import data.Schedule;
import data.SchedulesDB;
import domain.Debugger;
import entity.*;
import iot.Actuator;
import iot.Sensor;
import rule_headers.RuleManager;

import java.util.ArrayList;
import java.util.HashSet;

public class Room {


	private String location;
	private String size;
    private Building.ROOM_TYPE type;
	private ArrayList<Sensor> sensors;
	private ArrayList<Actuator> actuators;
	private RuleManager ruleManager;
	private ArrayList<Person> peopleActing;
    private ArrayList<Person> peopleComing;
	private HashSet<Object> entities;
	private Schedule schedule;

	public Room(String location, String size, String type) {
		this.location = location;
		this.size = size;
        this.type = Building.ROOM_TYPE.valueOf(type.toUpperCase());
		sensors = new ArrayList<>();
		actuators = new ArrayList<>();
		this.entities = new HashSet<>();
		this.ruleManager = new RuleManager(this);
		this.peopleActing = new ArrayList<>();
        this.peopleComing = new ArrayList<>();
        this.schedule = new Schedule(location, new ArrayList<>());
	}

	public void addTimeToSchedule(String elementId, int time, String st) {
	    schedule.addTimeToSchedule(elementId, time, st);
    }


	public String getLocation() {
		return location;
	}

	public ArrayList<Sensor> getSensors() {
		return sensors;
	}

    public Building.ROOM_TYPE getType() {
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


    public void turnHVACon() {
        for (Object e : entities) {
            if (e instanceof HVAC) {
                if (((HVAC) e).getCurrentState().equals(HVAC.State.OFF))
                ((HVAC) e).setCurrentState(HVAC.State.ON);
            }
        }
    }

    public void turnHVACoff() {
        for (Object e : entities) {
            if (e instanceof HVAC) {
                if (!((HVAC) e).getCurrentState().equals(HVAC.State.OFF))
                    ((HVAC) e).setCurrentState(HVAC.State.OFF);
            }
        }
    }



	void fireRules() {
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

        switch(type) {
            case "computer":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) {
                    Computer c = new Computer(i);
                    entities.add(c);
                    ruleManager.addComputerRule(c);
                }
                break;
            case "lamp":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) {
                    Lamp l = new Lamp(i);
                    entities.add(l);
                    ruleManager.addLampRule(l);
                }
                break;
            case "hvac":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) {
                    HVAC hvac = new HVAC(i);
                    Window w = findUnassignedWindow();
                    entities.add(hvac);
                    if (w != null) ruleManager.addHVACRule(hvac, w);
                }
                break;
            case "door":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) {
                    Door door = new Door(i);
                    entities.add(door);
                    ruleManager.addDoorRule(door);
                }
                break;
            case "window":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) {
                    Window window = new Window(i);
                    entities.add(window);
                    ruleManager.addWindowRule(window);
                }
                break;
            default: break;
		}
	}

    private Window findUnassignedWindow() {
        return (Window) entities.stream().filter(o ->
                o instanceof Window).filter(o ->
                !((Window) o).isAssigned()).findFirst().orElse(null);
    }

    HashSet<Object> getEntities() {
		return entities;
	}

    public ArrayList<Person> getPeopleActing() {
        return peopleActing;
    }

    void removePerson(Person p) {
		if (peopleActing.contains(p)) peopleActing.remove(p);
        else if (peopleComing.contains(p)) peopleComing.remove(p);
	}

    void addPerson(Person p) {
		peopleComing.add(p);
	}

    public boolean arePeopleComing(int threshold) {
        return peopleComing.stream().anyMatch(p -> p.getNextActionSteps() < threshold);
    }

    public boolean arePeopleInside() {
        return peopleActing.size() > 0;
    }

    public boolean isEmpty() {
        return peopleActing.size() == 0 && peopleComing.size() == 0;
    }

    public void shiftPerson(Person p) {
        if (peopleComing.contains(p)) {
            if (Debugger.isEnabled()) Debugger.log("Person " + p.getName() + " shifted to ACTING " + this.getLocation());
            peopleComing.remove(p);
            peopleActing.add(p);
        }
    }

    public boolean isAvailable() {
        return peopleActing.size() + peopleComing.size() < type.getLimit();
    }

    public void saveSchedule() {
        SchedulesDB.getInstance().save(schedule);
    }

    public void insertSchedule(Schedule s) {
	    this.schedule = s;
    }
}
