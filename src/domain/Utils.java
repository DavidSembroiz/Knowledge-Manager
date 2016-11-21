package domain;

import behaviour.Event;
import behaviour.PeopleManager.Action;
import building.Building;
import building.Room;
import iot.Manager;
import iot.Sensor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Utils {
	
	private static Utils instance = new Utils();
	
	private Utils() {
	}
	
	public static Utils getInstance() {
		return instance;
	}
	
	
	/*
	 * Gets the soID from a topic string
	 */
	
	public String extractIdFromTopic(String topic) {
		return topic.split("/")[1];
	}
	
	
	public ArrayList<String> getTypesFromMessage(String message) {
		ArrayList<String> ret = new ArrayList<>();
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(message);
			JSONObject channels = (JSONObject) obj.get("channels");
            for (Object value : channels.keySet()) {
                ret.add((String) value);
            }
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ret;
	}


	public String getValueFromType(String message, String type) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(message);
			JSONObject channels = (JSONObject) obj.get("channels");
			JSONObject valueObj = (JSONObject) channels.get(type);
			return valueObj.get("current-value").toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	
	@SuppressWarnings("unchecked")
	public void generatePeople() {

		try (FileWriter writer = new FileWriter("./res/people.json")) {
			JSONObject root = new JSONObject();
			JSONArray people = new JSONArray();
			for (int i = 0; i < Manager.NUM_PROFESSORS; ++i) {
				JSONObject person = new JSONObject();
				person.put("name", this.getRandomName());
				person.put("profile", "professor");
				people.add(person);
			}
            for (int i = 0; i < Manager.NUM_STUDENTS; ++i) {
                JSONObject person = new JSONObject();
                person.put("name", this.getRandomName());
                person.put("profile", "student");
                people.add(person);
            }
            for (int i = 0; i < Manager.NUM_PAS; ++i) {
                JSONObject person = new JSONObject();
                person.put("name", this.getRandomName());
                person.put("profile", "pas");
                people.add(person);
            }
			root.put("people", people);
			writer.write(root.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private String getRandomName() {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		return sb.toString();
	}

	
	public Building loadBuilding() {
		JSONParser parser = new JSONParser();
		ArrayList<Room> rooms = new ArrayList<>();
		try {
			FileReader reader = new FileReader("./res/building_testbed.json");
			JSONObject root = (JSONObject) parser.parse(reader);
			String id = (String) root.get("id");
			JSONArray rms = (JSONArray) root.get("rooms");
            for (Object rm1 : rms) {
                JSONObject rm = (JSONObject) rm1;
                Room r = new Room((String) rm.get("id"), (String) rm.get("size"), (String) rm.get("type"));
                JSONArray sens = (JSONArray) rm.get("sensors");
                for (Object sen1 : sens) {
                    JSONObject sen = (JSONObject) sen1;
                    String mode = (String) sen.get("mode");
                    if (mode.equals("single")) r.addSensor(loadSingleSensor(sen));
                    else if (mode.equals("multiple")) r.addSensor(loadMultipleSensor(sen));

                }
                JSONArray ents = (JSONArray) rm.get("entities");
                for (Object ent1 : ents) {
                    JSONObject ent = (JSONObject) ent1;
                    r.addEntity((String) ent.get("type"), (String) ent.get("quantity"));
                }
                rooms.add(r);
            }
			return new Building(id, rooms);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ArrayList<Sensor> loadMultipleSensor(JSONObject sen) {
		ArrayList<Sensor> ret = new ArrayList<>();
		String mainType = (String) sen.get("type");
		String qt = (String) sen.get("quantity");
		JSONArray motes = (JSONArray) sen.get("motes");

        for (Object mote1 : motes) {
            JSONObject mote = (JSONObject) mote1;
            String type = (String) mote.get("type");
            String qtt = (String) mote.get("quantity");
            for (int k = 0; k < Integer.parseInt(qt); ++k) {
                if (Integer.parseInt(qtt) > 1) {
                    for (int j = 0; j < Integer.parseInt(qtt); ++j) {
                        ret.add(new Sensor(mainType + "_" + k, type + "_" + j, "-1"));
                    }
                } else ret.add(new Sensor(mainType + "_" + k, type, "-1"));
            }
        }
		return ret;
	}

	private ArrayList<Sensor> loadSingleSensor(JSONObject sen) {
		ArrayList<Sensor> ret = new ArrayList<>();
		String qtt = (String) sen.get("quantity");
		String type = (String) sen.get("type");
		for (int z = 0; z < Integer.parseInt(qtt); ++z) {
			ret.add(new Sensor(type + "_" + z, type, "-1"));
		}
		return ret;
	}

    public ArrayList<Event> fetchEventsFromFile() {
        ArrayList<Event> events = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader("res/events.log"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int step = Integer.parseInt(values[0]);
                String name = values[1];
                Action a = Action.valueOf(values[2]);
                int next = Integer.parseInt(values[3]);
                int duration = Integer.parseInt(values[4]);
                String dest = values[5];
                events.add(new Event(step, name, a, dest, next, duration));
            }
            return events;
        } catch (IOException e) {
            System.out.println("ERROR: Unable to read events from file.");
            e.printStackTrace();
        }
        return events;
    }

}
