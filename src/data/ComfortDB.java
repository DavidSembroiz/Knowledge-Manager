package data;

import behaviour.Event;
import behaviour.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import iot.Manager;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;


public class ComfortDB extends NoSQLDB<Person, ArrayList<Event>> {


    private static ComfortDB instance = new ComfortDB();

    private ComfortDB() {
        initComponents();
    }

    public static ComfortDB getInstance() {
        return instance;
    }

    @Override
    public void loadProperties() {
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream("comfortdb.properties");
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
    public void correctInitialState() {
        if (Manager.MODE != 1) return;
        dbClient.context().deleteDB("comfort", "delete database");
        dbClient.context().createDB("comfort");
    }

    @Override
    public void save(Person p) {
        dbClient.save(createJsonComforts(p));
    }

    @Override
    public ArrayList<Event> fetchData() {
        return null;
    }

    private JsonObject createJsonComforts(Person p) {
        JsonObject root = new JsonObject();
        root.addProperty("_id", p.getName());
        JsonArray comforts = new JsonArray();
        for (Pair<Integer, Double> comf : p.getComforts()) {
            JsonObject ob = new JsonObject();
            ob.addProperty("time", comf.getKey());
            ob.addProperty("value", comf.getValue());
            comforts.add(ob);
        }
        root.add("comforts", comforts);
        return root;
    }

}
