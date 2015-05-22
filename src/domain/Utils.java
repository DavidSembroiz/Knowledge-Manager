package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Utils {

	
	public void parseJSON(String soID, String message) {
		
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(message);
			JSONObject channels = (JSONObject) obj.get("channels");
			Set<String> s = channels.keySet();
			for (Iterator<String> it = s.iterator(); it.hasNext();) {
				String t = it.next();
				System.out.println(t);
			}
		} catch (ParseException e) {
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
			Set<String> s = channels.keySet();
			for (Iterator<String> it = s.iterator(); it.hasNext();) {
				ret.add(it.next());
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
}
