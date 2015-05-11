package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class Utils {

	
	public void parseJSON(String soID, MqttMessage message) {
		
		/*
		 * TODO Use soID to get the structure of the message
		 * 
		 */
		
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(message.toString());
			JSONObject channels = (JSONObject) obj.get("channels");
			JSONObject temp = (JSONObject) channels.get("temperature");
			
			System.out.println("Temperature " + Double.toString((Double) temp.get("current-value")));
			
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
}
