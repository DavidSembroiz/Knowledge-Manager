package data;

import building.Building;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


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
        dbClient.context().deleteDB("schedules", "delete database");
        dbClient.context().createDB("schedules");
    }

    @Override
    public void save(JsonObject b) {
        if (dbClient.contains(b.get("_id").getAsString())) {
            JsonObject ob = dbClient.find(JsonObject.class, b.get("_id").getAsString());
            Set<Map.Entry<String, JsonElement>> entries = b.get("data").getAsJsonObject().entrySet();
            String elementId = null;
            String state = null;
            int value = 0;
            for (Map.Entry e : entries) {
                elementId = e.getKey().toString();
                JsonObject newState = (JsonObject) e.getValue();
                Set<Map.Entry<String, JsonElement>> states = newState.entrySet();
                for (Map.Entry st : states) {
                    state = st.getKey().toString();
                    String aux = st.getValue().toString();
                    value = Integer.parseInt(aux.substring(1, aux.length() - 1));
                }
            }
            if (ob.get("data").getAsJsonObject().has(elementId)) {
                JsonObject element = ob.get("data").getAsJsonObject().get(elementId).getAsJsonObject();
                if (element.has(state)) {
                    JsonArray vals = element.get(state).getAsJsonArray();
                    vals.add(new JsonPrimitive(value));
                }
                else {
                    JsonArray vals = new JsonArray();
                    vals.add(new JsonPrimitive(value));
                    element.add(state, vals);
                }
            }
            else {
                ob.get("data").getAsJsonObject().add(elementId, b.get("data").getAsJsonObject().get(elementId));
                System.out.println("test");
            }
            dbClient.update(ob);
        }
        else {
            dbClient.save(b);
        }
    }
    public <T> JsonObject createJsonObject(T st, int current_time, String roomId, String elementId) {

        JsonArray vals = new JsonArray();
        vals.add(new JsonPrimitive(current_time));
        JsonObject actions = new JsonObject();
        actions.add(st.toString(), vals);

        JsonObject element = new JsonObject();
        element.add(elementId, actions);
        JsonObject root = new JsonObject();
        root.addProperty("_id", roomId);
        root.add("data", element);

        return root;
    }

    @Override
    public Building fetchData() {
        return null;
    }

}
