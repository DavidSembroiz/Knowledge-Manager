package domain;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import behaviour.Person;
import behaviour.Person.State;

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
	
	/**
	 * 10 second steps
	 */
	
	public static int STEPS;
	
	public static int MAX_RANDOM_WALKS;
	public static boolean RANDOM_WALKS;
	
	public static int CURRENT_STEP = 0;
	private static int CURRENT_ROOM_NO = 1;

	
	

	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("manager.properties");
			prop.load(is);
			
			STEPS = Integer.parseInt(prop.getProperty("steps"));
			
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
	
	public static boolean emptyRoom(ArrayList<Person> people) {
		for (Person p : people) {
			if (p.getState().equals(State.INSIDE)) return false;
		}
		return true;
	}

	public static boolean justWalking(ArrayList<Person> people) {
		for (Person p : people) {
			if (!p.getState().equals(State.RANDOM_WALKS)) return false;
		}
		return true;
	}
	
	public static boolean eating(ArrayList<Person> people) {
		for (Person p : people) {
			if (!p.getState().equals(State.LUNCH)) return false;
		}
		return true;
	}
	
	public void generatePeople(int numProfRooms, int numStudRooms, int numPasRooms,
							   int professorsPerRoom, int studentsPerRoom, int pasPerRoom) {
		try(PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter("res/people.txt")))) {
			for (int i = CURRENT_ROOM_NO; i < CURRENT_ROOM_NO + numProfRooms; ++i) {
				String room = getProperRoomName(i);
				for (int j = 0; j < professorsPerRoom; ++j) {
					String name = getRandomName();
		        	String type = "professor";
		        	wr.println(name + "," + room + "," + type);
				}
	        }
			CURRENT_ROOM_NO += numProfRooms;
			
			for (int i = CURRENT_ROOM_NO; i < CURRENT_ROOM_NO + numStudRooms; ++i) {
				String room = getProperRoomName(i);
				for (int j = 0; j < studentsPerRoom; ++j) {
					String name = getRandomName();
		        	String type = "student";
		        	wr.println(name + "," + room + "," + type);
				}
			}
			CURRENT_ROOM_NO += numStudRooms;
			
			for (int i = CURRENT_ROOM_NO; i < CURRENT_ROOM_NO + numPasRooms; ++i) {
				String room = getProperRoomName(i);
				for (int j = 0; j < pasPerRoom; ++j) {
					String name = getRandomName();
		        	String type = "pas";
		        	wr.println(name + "," + room + "," + type);
				}
			}
			CURRENT_ROOM_NO += numPasRooms;
			
	    } catch (IOException e) {
	    	System.err.println("ERROR: Unable to read people from file.");
	    	e.printStackTrace();
	    } catch(IllegalArgumentException e) {
	    	System.err.println("ERROR: Person does not contain a valid type.");
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
	
	private String getProperRoomName(int id) {
        String res = "upc/campusnord/";
        if (id < 10) res += "d600" + id;
        else if (id < 100) res += "d60" + id;
        else if (id < 1000) res += "d6" + id;
        return res;
    }
}
