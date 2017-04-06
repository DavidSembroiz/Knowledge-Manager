package building;

import behaviour.Person;
import data.Schedule;
import data.SchedulesDB;
import domain.Debugger;
import entity.*;
import iot.Manager;
import iot.Sensor;
import rule_headers.RuleManager;

import java.util.ArrayList;
import java.util.HashSet;

public class Room {


	private String location;
	private String size;
    private Building.ROOM_TYPE type;
	private ArrayList<Sensor> sensors;
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
		this.entities = new HashSet<>();
		this.ruleManager = new RuleManager(this);
		this.peopleActing = new ArrayList<>();
        this.peopleComing = new ArrayList<>();
        this.schedule = new Schedule(location, new ArrayList<>());
	}

	public void addTimeToSchedule(String elementId, int time, String st) {
	    schedule.addTimeToSchedule(elementId, time, st);
    }

    Schedule getSchedule() {
        return schedule;
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
                for (int i = 0; i < Integer.parseInt(qtt); ++i) entities.add(new Computer(i));
                break;
            case "lamp":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) entities.add(new Lamp(i));
                break;
            case "hvac":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) entities.add(new HVAC(i));
                break;
            case "door":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) ruleManager.addDoorRule(new Door(i));
                break;
            case "window":
                for (int i = 0; i < Integer.parseInt(qtt); ++i) entities.add(new Window(i));
                break;
            default: break;
		}
	}

	public void addRules() {
	    for (Object e : entities) {
	        if (e instanceof HVAC) {
                Window w = findUnassignedWindow();
                if (w != null) ruleManager.addHVACRule((HVAC) e, w);
            }
            else if (e instanceof Computer) ruleManager.addComputerRule((Computer) e);
	        else if (e instanceof Lamp) ruleManager.addLampRule((Lamp) e);
	        else if (e instanceof Window) ruleManager.addWindowRule((Window) e);
	        else if (e instanceof Door) ruleManager.addDoorRule((Door) e);
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

    void saveSchedule() {
        SchedulesDB.getInstance().save(schedule);
    }

    void insertSchedule(Schedule s) {
	    this.schedule = s;
    }

    void performActuation(String element, int index, String value) {
	    if (element.toLowerCase().equals("computer")) {
            for (Object e : entities) {
                if (e instanceof Computer && ((Computer) e).getId() == index) {
                    ((Computer) e).setCurrentState(Computer.State.valueOf(value));
                    ((Computer) e).setTimeChanged(Manager.CURRENT_STEP);
                    return;
                }
            }
        }
        else if (element.toLowerCase().equals("hvac")) {
            for (Object e : entities) {
                if (e instanceof HVAC && ((HVAC) e).getId() == index) {
                    ((HVAC) e).setCurrentState(HVAC.State.valueOf(value));
                    ((HVAC) e).setTimeChanged(Manager.CURRENT_STEP);
                    return;
                }
            }
        }
        else if (element.toLowerCase().equals("lamp")) {
            for (Object e : entities) {
                if (e instanceof Lamp && ((Lamp) e).getId() == index) {
                    ((Lamp) e).setCurrentState(Lamp.State.valueOf(value));
                    ((Lamp) e).setTimeChanged(Manager.CURRENT_STEP);
                    return;
                }
            }
        }
    }

    public double getTemperature() {
	    for (Sensor s : sensors) {
	        if (s.getType().equals("temperature")) {
	            double res = Double.parseDouble(s.getValue());
	            if (res > 0) return res;
            }
        }
        return 0;
    }

    public double getLuminosity() {
        for (Sensor s : sensors) {
            if (s.getType().equals("luminosity")) {
                double res = Double.parseDouble(s.getValue());
                if (res > 0) return res;
            }
        }
        return 0;
    }

    public Computer.State getUsedComputer(String name) {
	    for (Object o : entities) {
	        if (o instanceof Computer) {
	            Person p = ((Computer) o).getUsedBy();
	            if (p != null && p.getName().equals(name)) {
	                return ((Computer) o).getCurrentState();
                }
            }
        }
        return null;
    }

    public void adjustSchedule(int jump, Person p) {
        schedule.adjustSchedule(jump, p);
    }
}
