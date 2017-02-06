package domain;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import iot.Manager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(message).getAsJsonObject();
        JsonObject channels = obj.get("channels").getAsJsonObject();
        for (Object value : channels.entrySet()) {
            ret.add((String) value);
        }
		return ret;
	}


	public String getValueFromType(String message, String type) {
		JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(message);
        JsonObject channels = obj.getAsJsonObject("channels");
        JsonObject valueObj = channels.get(type).getAsJsonObject();
        return valueObj.get("current-value").toString();
	}


	
	@SuppressWarnings("unchecked")
	public void generatePeople() {

		try (FileWriter writer = new FileWriter("./res/people.json")) {
			JsonObject root = new JsonObject();
			JsonArray people = new JsonArray();
			for (int i = 0; i < Manager.NUM_PROFESSORS; ++i) {
                JsonObject person = new JsonObject();
				person.addProperty("name", this.getRandomName());
				person.addProperty("profile", "professor");
				people.add(person);
			}
            for (int i = 0; i < Manager.NUM_STUDENTS; ++i) {
                JsonObject person = new JsonObject();
                person.addProperty("name", this.getRandomName());
                person.addProperty("profile", "student");
                people.add(person);
            }
            for (int i = 0; i < Manager.NUM_PAS; ++i) {
                JsonObject person = new JsonObject();
                person.addProperty("name", this.getRandomName());
                person.addProperty("profile", "pas");
                people.add(person);
            }
			root.add("people", people);
			writer.write(root.toString());
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

}
