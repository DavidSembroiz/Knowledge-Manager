package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Utils {

	
	/**
	 * Gets the soID from a topic string
	 * 
	 * @param topic
	 * @return
	 */
	public String extractIdFromTopic(String topic) {
		return topic.split("/")[1];
	}


	/**
	 * Provisional function to read SO IDS from a database file
	 * 
	 * @return
	 */
	public ArrayList<String> readSOIdsFromFile() {
		String path = ".\\src\\database\\database_ids.txt";
		ArrayList<String> ids = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				ids.add(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ids;
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
	
	
}
