package behaviour;

import behaviour.PeopleManager.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

class UserProfile implements Cloneable {
	
	private Probability entrance;
	private Probability randomWalks;
    private Probability meeting;
	private Probability lunch;
	private Probability exit;
	
	private int[] lunchDurationRange;
	private int[] randomWalksDurationRange;
	
	private Type type;
	private Random rand;
	
	UserProfile(Type t) {
		this.type = t;
		rand = new Random();
		loadProfileFromFile(t);
	}
	
	
	Probability getEntrance() {
		return entrance;
	}
	public void setEntrance(Probability entrance) {
		this.entrance = entrance;
	}
    Probability getRandomWalks() {
		return randomWalks;
	}
	public void setRandomWalks(Probability randomWalks) {
		this.randomWalks = randomWalks;
	}
	
    Probability getLunch() {
		return lunch;
	}
	
	public void setLunch(Probability lunch) {
		this.lunch = lunch;
	}
	
    Probability getExit() {
		return exit;
	}
	public void setExit(Probability exit) {
		this.exit = exit;
	}
	public Type getType() {
		return type;
	}

	public void setType(Type t) {
		this.type = t;
	}

    Probability getMeeting() {
        return meeting;
    }

    public void setMeeting(Probability meeting) {
        this.meeting = meeting;
    }

    /**
	 * Returns a value within min <= value <= max
	 */
    int getLunchDuration() {
		return rand.nextInt((lunchDurationRange[1] - lunchDurationRange[0]) + 1) + lunchDurationRange[0];
	}
	
    int getRandomWalksDuration() {
		return rand.nextInt((randomWalksDurationRange[1] - randomWalksDurationRange[0]) + 1) + randomWalksDurationRange[0];
	}
	
	
	/**
	 * Profile file is divided in blocks of two lines with the following format:
	 * 
	 * "profileName": {
     *     "action": ["prob0", "prob1", ... ],
     *     "action": ["prob0", "prob1", ... ],
     * }
     *
	 * 
	 * For the actions that require a range (duration) instead of a probability, the format is the following:
	 * 
	 * "profileName": {
     *     "actionDuration": ["min", "max"]
     * }
     *
	 */
	
	private void loadProfileFromFile(Type t) {
		JSONParser parser = new JSONParser();
		try {
			FileReader reader = new FileReader("./res/profiles.json");
			JSONObject root = (JSONObject) parser.parse(reader);
			JSONObject prof = (JSONObject) root.get(t.toString().toLowerCase());
			if (prof == null) return;
            for (String k : (Iterable<String>) prof.keySet()) {
                JSONArray values = (JSONArray) prof.get(k);
                String[] vals = new String[values.size()];
                for (int i = 0; i < values.size(); ++i) {
                    vals[i] = (String) values.get(i);
                }
                if (vals.length == 2) {
                    assignDuration(k, vals);
                } else {
                    Probability p = new Probability(vals);
                    assignProbability(k, p);
                }
            }
		} catch(IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	private int[] arrayStringToInt(String[] values) {
		int[] res = new int[2];
		for (int i = 0; i < values.length; ++i) {
			res[i] = Integer.parseInt(values[i]);
		}
		return res;
	}
	
	private void assignDuration(String name, String[] values) {
		int[] vals = arrayStringToInt(values);
		switch(name) {
		case "lunchDuration":
			lunchDurationRange = vals;
			break;
		case "randomWalksDuration":
			randomWalksDurationRange = vals;
			break;
		}
	}

	private void assignProbability(String name, Probability p) {
		switch (name) {
		case "entrance":
			entrance = p;
			break;
		case "exit":
			exit = p;
			break;
		case "lunch":
			lunch = p;
			break;
		case "randomWalks":
			randomWalks = p;
			break;
        case "meeting":
            meeting = p;
            break;
		default:
			System.err.println("ERROR: profile file wrongly formatted");
			break;
		}
	}
	
	protected Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
