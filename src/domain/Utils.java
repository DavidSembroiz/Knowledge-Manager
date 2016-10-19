package domain;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import building.Building;
import building.Room;
import iot.Sensor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Utils {
	
	private static Utils instance = new Utils();
	
	private Utils() {
		loadProperties();
	}
	
	public static Utils getInstance() {
		return instance;
	}
	
	private Properties prop;
	
	
	public static int MAX_RANDOM_WALKS;
	public static boolean RANDOM_WALKS;


	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("manager.properties");
			prop.load(is);
			
			
			MAX_RANDOM_WALKS = Integer.parseInt(prop.getProperty("max_random_walks"));
			RANDOM_WALKS = Boolean.parseBoolean(prop.getProperty("random_walks"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Gets the soID from a topic string
	 * 
	 * @param topic
	 * @return
	 */
	
	public String extractIdFromTopic(String topic) {
		return topic.split("/")[1];
	}
	
	
	public ArrayList<String> getTypesFromMessage(String message) {
		ArrayList<String> ret = new ArrayList<String>();
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(message);
			JSONObject channels = (JSONObject) obj.get("channels");
			Set<?> s = channels.keySet();
			for (Iterator<?> it = s.iterator(); it.hasNext();) {
				ret.add((String)it.next());
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
	public String addToJSON(String soID, String type, String json) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(json);
			obj.put(type, soID);
			return obj.toString();
		} catch(ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String getIdFromType(String s, String sensors) {
		String res = null;
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(sensors);
			res = obj.get(s).toString();
			
		} catch(ParseException e) {
			e.printStackTrace();
		}
		return res;
	}


	public String getRuleByActuator(Map<String, Integer> ruleAssociations, String actuator) {
		for (Map.Entry<String, Integer> entry : ruleAssociations.entrySet()) {
			String[] n = entry.getKey().split("/");
			if (actuator.equals(n[0])) {
				return n[1];
			}
		}
		return null;
	}


	public int getRegsByActuator(Map<String, Integer> ruleAssociations, String actuator) {
		for (Map.Entry<String, Integer> entry : ruleAssociations.entrySet()) {
			String[] n = entry.getKey().split("/");
			if (actuator.equals(n[0])) {
				return entry.getValue();
			}
		}
		return -1;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public void generatePeople() {
		try (FileWriter writer = new FileWriter("./res/people.json")) {
			JSONObject root = new JSONObject();
			JSONArray people = new JSONArray();
			for (int i = 0; i < 1; ++i) {
				JSONObject person = new JSONObject();
				person.put("name", this.getRandomName());
				person.put("profile", "professor");
				people.add(person);
			}
			root.put("people", people);
			writer.write(root.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getTemplatePersonName() {
		try(BufferedReader br = new BufferedReader(new FileReader("res/people.txt"))) {
	        String line;
	        if ((line = br.readLine()) != null) {
	        	return line.split(",")[0];
	        }
	        return null;
	    } catch (IOException e) {
	    	System.out.println("ERROR: Unable to read events from file.");
	    	e.printStackTrace();
	    }
		return null;
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
		ArrayList<Room> rooms = new ArrayList<Room>();
		try {
			FileReader reader = new FileReader("./res/building.json");
			JSONObject root = (JSONObject) parser.parse(reader);
			String id = (String) root.get("id");
			JSONArray rms = (JSONArray) root.get("rooms");
			for (int i = 0; i < rms.size(); ++i) {
				JSONObject rm = (JSONObject) rms.get(i);
				Room r = new Room((String) rm.get("id"), (String) rm.get("size"));
				JSONArray sens = (JSONArray) rm.get("sensors");
				for (int j = 0; j < sens.size(); ++j) {
					JSONObject sen = (JSONObject) sens.get(j);
					String mode = (String) sen.get("mode");
					if (mode.equals("single")) r.addSensor(loadSingleSensor(sen));
					else if (mode.equals("multiple")) r.addSensor(loadMultipleSensor(sen));
					
				}
				JSONArray ents = (JSONArray) rm.get("entities");
				for (int j = 0; j < ents.size(); ++j) {
					JSONObject ent = (JSONObject) ents.get(j);
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
		ArrayList<Sensor> ret = new ArrayList<Sensor>();
		String mainType = (String) sen.get("type");
		String qt = (String) sen.get("quantity");
		JSONArray motes = (JSONArray) sen.get("motes");
		
		for (int i = 0; i < motes.size(); ++i) {
			JSONObject mote = (JSONObject) motes.get(i);
			String type = (String) mote.get("type");
			String qtt = (String) mote.get("quantity");
			for (int k = 0; k < Integer.parseInt(qt); ++k) {
				if (Integer.parseInt(qtt) > 1) {
					for (int j = 0; j < Integer.parseInt(qtt); ++j) {
						ret.add(new Sensor(mainType + "_" + k, type + "_" + j, "-1"));
					}
				}
				else ret.add(new Sensor(mainType + "_" + k, type, "-1"));
			}
		}
		return ret;
	}

	private ArrayList<Sensor> loadSingleSensor(JSONObject sen) {
		ArrayList<Sensor> ret = new ArrayList<Sensor>();
		String qtt = (String) sen.get("quantity");
		String type = (String) sen.get("type");
		for (int z = 0; z < Integer.parseInt(qtt); ++z) {
			ret.add(new Sensor(type + "_" + z, type, "-1"));
		}
		return ret;
	}
}
