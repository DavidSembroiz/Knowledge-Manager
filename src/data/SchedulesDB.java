package data;

import building.Building;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class SchedulesDB extends NoSQLDB<JsonObject, Building> {

    private static SchedulesDB instance = new SchedulesDB();

    private SchedulesDB() {
        initComponents();
    }

    public static SchedulesDB getInstance() {
        return instance;
    }


    @Override
    public void loadProperties() {
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream("schedulesdb.properties");
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

    }

    @Override
    public void save(JsonObject b) {
        if (dbClient.contains(b.get("_id").getAsString())) {
            JsonObject ob = dbClient.find(JsonObject.class, b.get("_id").getAsString());
            JsonArray elements = ob.get("elements").getAsJsonArray();
            for (JsonElement o : elements) {
                // TODO add event to database
            }
            dbClient.update(ob);
        }
        else {
            dbClient.save(b);
        }
    }
    public <T> JsonObject createJsonObject(T st, int current_time, String roomId, String elementId) {
        JsonObject element = new JsonObject();
        element.addProperty("id", elementId);

        JsonObject actions = new JsonObject();
        JsonArray vals = new JsonArray();
        vals.add(new JsonPrimitive(current_time));
        actions.add(st.toString(), vals);
        element.add("actions", actions);
        JsonArray elements = new JsonArray();
        elements.add(element);
        JsonObject root = new JsonObject();
        root.addProperty("_id", roomId);

        root.add("elements", elements);
        return root;
    }

    @Override
    public Building fetchData() {
        return null;
    }

}
