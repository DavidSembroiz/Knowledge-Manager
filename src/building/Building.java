package building;

import behaviour.Person;
import domain.Debugger;
import entity.Computer;
import entity.HVAC;
import entity.Lamp;
import iot.Manager;

import java.util.ArrayList;
import java.util.HashSet;

public class Building {
	
	private int numRooms;
	private String id;
	private ArrayList<Room> rooms;


    public int NUM_PLACES = 3;
	private String[] officeLocations;
    private String[] meetingLocations;
    private String[] classLocations;
	
	public Building(String id, ArrayList<Room> rooms){
		this.rooms = rooms;
		addSpecialRooms();
		this.numRooms = rooms.size();
		this.id = id;
		parseLocations();
	}
	
	private void addSpecialRooms() {
		rooms.add(new Room("outside", "0", "undefined"));
		rooms.add(new Room("inside", "0", "undefined"));
		rooms.add(new Room("salon", "0", "undefined"));
	}
	
	private void parseLocations() {
		HashSet<String> officeLocs = new HashSet<String>();
        HashSet<String> meetingLocs = new HashSet<String>();
        HashSet<String> classLocs = new HashSet<String>();
		for (Room r : rooms) {
			if (Integer.parseInt(r.getSize()) > 0) {
                if (r.getType().equals(Room.ROOM_TYPE.MEETING_ROOM)) meetingLocs.add(r.getLocation());
                else if (r.getType().equals(Room.ROOM_TYPE.OFFICE)) officeLocs.add(r.getLocation());
                else if (r.getType().equals(Room.ROOM_TYPE.CLASSROOM)) classLocs.add(r.getLocation());
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
			else if (p.getLocation().equals(roomLoc)) {
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
                    if (((Computer) e).getUsedBy() == null) {
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
        double fcons = 0;
        for (Room r : rooms) {
            HashSet<Object> ents = r.getEntities();
            for (Object e : ents) {
                if (e instanceof Computer) fcons += ((Computer) e).getCons();
                else if (e instanceof HVAC) fcons += ((HVAC) e).getCons();
                else if (e instanceof Lamp) fcons += ((Lamp) e).getCons();
            }
        }

        /*
         * Every step equals to 10 seconds, and consumption is added every step.
         * Thus, (fcons/(STEPS))*(STEPS/360) gives kWh
         *
         * Equals to (fcons/360)/1000 for 1 day
         *
         */

        return ((fcons/Manager.STEPS)*(Manager.STEPS/360))/1000;
	}

	public double[] getHourlyConsumption() {
        double fcons[] = new double[24];
        for (int i = 0; i < 24; ++i) {
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
        for (Room r : rooms) r.fireRules();
    }


}
