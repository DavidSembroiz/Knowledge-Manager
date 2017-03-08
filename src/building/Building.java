package building;

import behaviour.Person;
import data.Schedule;
import data.Schedule.Element;
import data.SchedulesDB;
import domain.Debugger;
import entity.Computer;
import entity.HVAC;
import entity.Lamp;
import iot.Manager;
import javafx.util.Pair;

import java.util.*;

public class Building {

    public void saveSchedules() {
        for (Room r : rooms) {
            r.saveSchedule();
        }
    }

    private Room getRoomById(String roomId) {
        for (Room r : rooms) {
            if (r.getLocation().equals(roomId)) return r;
        }
        return null;
    }

    public void fillSchedules() {
        List<Schedule> schedules = SchedulesDB.getInstance().fetchData();
        for (Schedule s : schedules) {
            Room r = getRoomById(s.get_id());
            if (r != null) r.insertSchedule(s);
        }
    }

    public void performActuations() {
        for (Room r : rooms) {
            ArrayList<Element> elements = r.getSchedule().getElements();
            for (Element e : elements) {
                ArrayList<Pair<Integer, String>> times = e.getTimes();
                for (Pair<Integer, String> time : times) {
                    if (time.getKey().equals(Manager.CURRENT_STEP)) {
                        r.performActuation(e.getElementType(), e.getElementIndex(), time.getValue());
                    }
                }
            }

        }
    }

    public enum ROOM_TYPE {
        OFFICE(3),
        MEETING_ROOM(20),
        CLASSROOM(20),
        UNDEFINED(9999);

        private final int limit;
        public int getLimit() {return limit;}

        ROOM_TYPE(int limit) {
            this.limit = limit;
        }
    }

	private ArrayList<Room> rooms;

	private String[] officeLocations;
    private String[] meetingLocations;
    private String[] classLocations;


	public Building(ArrayList<Room> rooms){
		this.rooms = rooms;
		addSpecialRooms();
		parseLocations();
	}
	
	private void addSpecialRooms() {
		rooms.add(new Room("outside", "0", "undefined"));
		rooms.add(new Room("inside", "0", "undefined"));
		rooms.add(new Room("salon", "0", "undefined"));
	}
	
	private void parseLocations() {
		HashSet<String> officeLocs = new HashSet<>();
        HashSet<String> meetingLocs = new HashSet<>();
        HashSet<String> classLocs = new HashSet<>();
		for (Room r : rooms) {
			if (Integer.parseInt(r.getSize()) > 0) {
                if (r.getType().equals(ROOM_TYPE.MEETING_ROOM)) meetingLocs.add(r.getLocation());
                else if (r.getType().equals(ROOM_TYPE.OFFICE)) officeLocs.add(r.getLocation());
                else if (r.getType().equals(ROOM_TYPE.CLASSROOM)) classLocs.add(r.getLocation());
            }
		}
		officeLocations = officeLocs.toArray(new String[officeLocs.size()]);
        meetingLocations = meetingLocs.toArray(new String[meetingLocs.size()]);
        classLocations = classLocs.toArray(new String[classLocs.size()]);
	}


	public String[] getOfficeLocations() {
		return officeLocations;
	}

    public String[] getMeetingLocations() {
        return meetingLocations;
    }

    public String[] getClassromLocations() {
        return classLocations;
    }


	public ArrayList<Room> getRooms() {
		return this.rooms;
	}

	public Room getRoom(String location) {
		for (Room r : rooms) {
			if (r.getLocation().equals(location)) return r;
		}
		return null;
	}

	public void updateConsumption() {
		for (Room r : rooms) {
			HashSet<Object> ents = r.getEntities();
			for (Object e : ents) {
				if (e instanceof Computer) {
					int cons = ((Computer) e).getCurrentState().getConsumption();
					((Computer) e).addConsumption(cons);
				}
				else if (e instanceof HVAC) {
                    int cons = ((HVAC) e).getCurrentState().getConsumption();
                    ((HVAC) e).addConsumption(cons);
                }
                else if (e instanceof Lamp) {
                    int cons = ((Lamp) e).getCurrentState().getConsumption();
                    ((Lamp) e).addConsumption(cons);
                }
			}
		}
	}

	public void movePerson(Person p, String currentLoc) {
		for (Room r : rooms) {
			String roomLoc = r.getLocation();
			if (currentLoc.equals(roomLoc)) {
                if (Debugger.isEnabled()) Debugger.log("Person " + p.getName() + " removed from " + roomLoc);
				r.removePerson(p);
			}
            if (p.getLocation().equals(roomLoc)) {
                if (Debugger.isEnabled()) Debugger.log("Person " + p.getName() + " added to " + roomLoc);
				r.addPerson(p);
			}
		}
	}

    public void assignRoomElements(Person p, String roomLoc) {
        Room r = getRoom(roomLoc);
        if (r != null) {
            HashSet<Object> ents = r.getEntities();
            for (Object e : ents) {
                if (e instanceof Computer) {
                    if (((Computer) e).getUsedBy() == null &&
                            ((Computer) e).getCurrentState().equals(Computer.State.OFF)) {
                        ((Computer) e).setUsedBy(p);
                        return;
                    }
                }
            }
        }
    }

    public void unassignRoomElements(Person p, String roomLoc) {
        Room r = getRoom(roomLoc);
        if (r != null) {
            HashSet<Object> ents = r.getEntities();
            for (Object e : ents) {
                if (e instanceof Computer) {
                    Person c = ((Computer) e).getUsedBy();
                    if (c != null && c.getName().equals(p.getName())) {
                        ((Computer) e).setUsedBy(null);
                    }
                }
            }
        }
    }

    public double calculateAccumulatedConsumption() {
        double fcons[] = getHourlyConsumption();
        double res = 0;
        for (double fcon : fcons) res += fcon;

        /*
         * Every step equals to 10 seconds, and consumption is added every step.
         * Thus, (fcons/(STEPS))*(STEPS/360) gives kWh
         *
         * Equals to (fcons/360)/1000 for 1 day
         *
         */
        return res;
	}

	public double[] getHourlyConsumption() {
        double fcons[] = new double[Manager.CONSUMPTION_RESOLUTION];
        for (int i = 0; i < fcons.length; ++i) {
            for (Room r : rooms) {
                HashSet<Object> ents = r.getEntities();
                for (Object e : ents) {
                    if (e instanceof Computer) fcons[i] += ((Computer) e).getHourlyConsumption(i);
                    else if (e instanceof HVAC) fcons[i] += ((HVAC) e).getHourlyConsumption(i);
                    else if (e instanceof Lamp) fcons[i] += ((Lamp) e).getHourlyConsumption(i);
                }
            }
            fcons[i] /= (360*1000);
        }
        return fcons;
    }


    public void fireRules() {
        rooms.forEach(Room::fireRules);
    }


    public ROOM_TYPE getLocationType(String location) {
        for (Room r : rooms) {
            if (r.getLocation().equals(location)) return r.getType();
        }
        return null;
    }
}
