package data;

import building.Building;
import building.Room;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iot.Manager;
import iot.Sensor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;


public class BuildingsDB extends NoSQLDB<JsonObject, Building> {

    private static BuildingsDB instance = new BuildingsDB();

    private BuildingsDB() {
        initComponents();
    }

    public static BuildingsDB getInstance() {
        return instance;
    }


    @Override
    public void loadProperties() {
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream("buildingsdb.properties");
            prop.load(is);
            DB = prop.getProperty("couchdb.name");
            CREATE_IF_NOT_EXIST = Boolean.parseBoolean(prop.getProperty("couchdb.createdb.if-not-exist"));
            PROTOCOL = prop.getProperty("couchdb.protocol");
            HOST = prop.getProperty("couchdb.host");
            PORT = Integer.parseInt(prop.getProperty("couchdb.port"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(JsonObject b) {
        System.out.println(b);
        dbClient.save(b);
    }

    @Override
    public Building fetchData() {
        ArrayList<Room> rooms = new ArrayList<>();
        JsonObject root = dbClient.find(JsonObject.class, Manager.getBuildingName());
        String id = root.get("_id").getAsString();
        JsonArray rms = root.getAsJsonArray("rooms");
        for (Object rm1 : rms) {
            JsonObject rm = (JsonObject) rm1;
            Room r = new Room(rm.get("id").getAsString(), rm.get("size").getAsString(), rm.get("type").getAsString());
            JsonArray sens = rm.getAsJsonArray("sensors");
            for (Object sen1 : sens) {
                JsonObject sen = (JsonObject) sen1;
                String mode = sen.get("mode").getAsString();
                if (mode.equals("single")) r.addSensor(loadSingleSensor(sen));
                else if (mode.equals("multiple")) r.addSensor(loadMultipleSensor(sen));
            }
            JsonArray ents = rm.getAsJsonArray("entities");
            for (Object ent1 : ents) {
                JsonObject ent = (JsonObject) ent1;
                r.addEntity(ent.get("type").getAsString(), ent.get("quantity").getAsString());
            }
            rooms.add(r);
        }
        return new Building(id, rooms);
    }

    private ArrayList<Sensor> loadMultipleSensor(JsonObject sen) {
        ArrayList<Sensor> ret = new ArrayList<>();
        String mainType = sen.get("type").getAsString();
        String qt = sen.get("quantity").getAsString();
        JsonArray motes = sen.getAsJsonArray("motes");

        for (Object mote1 : motes) {
            JsonObject mote = (JsonObject) mote1;
            String type = mote.get("type").getAsString();
            String qtt = mote.get("quantity").getAsString();
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

    private ArrayList<Sensor> loadSingleSensor(JsonObject sen) {
        ArrayList<Sensor> ret = new ArrayList<>();
        String qtt =  sen.get("quantity").getAsString();
        String type =  sen.get("type").getAsString();
        for (int z = 0; z < Integer.parseInt(qtt); ++z) {
            ret.add(new Sensor(type + "_" + z, type, "-1"));
        }
        return ret;
    }
}
