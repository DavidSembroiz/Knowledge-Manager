package iot;

import java.util.*;

public class Room {
	
	private static final int NOT_FOUND = -1;
	
	private String location;
	private ArrayList<Sensor> sensors;
	
	public Room(String location) {
		this.location = location;
		sensors = new ArrayList<Sensor>();
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
	
	private int sensorExists(String soID, String type) {
		for (int i = 0; i < sensors.size(); ++i) {
			Sensor s = sensors.get(i);
			if (s.getSoID().equals(soID) && s.getType().equals(type)) return i;
		}
		return NOT_FOUND;
	}
	
	private Sensor registerSensor(String soID, String type) {
		Sensor s = new Sensor(soID, type);
		sensors.add(s);
		return s;
	}
	
	public Sensor getSensor(String soID, String type) {
		int pos = sensorExists(soID, type);
		if (pos >= 0) return sensors.get(pos);
		return registerSensor(soID, type);
	}
	
}
