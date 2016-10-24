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
	private String[] locations;
	
	public Building(String id, ArrayList<Room> rooms){
		this.rooms = rooms;
		addSpecialRooms();
		this.numRooms = rooms.size();
		this.id = id;
		parseLocations();
	}
	
	private void addSpecialRooms() {
		rooms.add(new Room("outside", "0"));
		rooms.add(new Room("inside", "0"));
		rooms.add(new Room("salon", "0"));
	}
	
	private void parseLocations() {
		HashSet<String> locs = new HashSet<String>();
		for (Room r : rooms) {
			if (Integer.parseInt(r.getSize()) > 0) locs.add(r.getLocation());
		}
		locations = locs.toArray(new String[locs.size()]);
	}

	public String[] getLocations() {
		return locations;
	}

	public void setLocations(String[] l) {
		this.locations = l;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}

	public int getNumRooms() {
		return numRooms;
	}

	public void setNumRooms(int numRooms) {
		this.numRooms = numRooms;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
	
	public double calculateFinalConsumption() {
        double fcons = 0;
        for (Room r : rooms) {
            HashSet<Object> ents = r.getEntities();
            for (Object e : ents) {
                if (e instanceof Computer) fcons += ((Computer) e).getCons();
                else if (e instanceof HVAC) fcons += ((HVAC) e).getCons();
                else if (e instanceof Lamp) fcons += ((Lamp) e).getCons();
            }
        }
        /**
         * Every step equivals to 10 seconds, and consumption is added every step.
         * Thus, (fcons/(STEPS))*(STEPS/360) gives kWh
         */

        return ((fcons/Manager.STEPS)*(Manager.STEPS/360))/1000;
	}

    public void fireRules() {
        for (Room r : rooms) r.fireRules();
    }
}
