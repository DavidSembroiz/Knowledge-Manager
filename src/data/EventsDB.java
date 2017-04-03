package data;

import behaviour.Event;
import behaviour.PeopleManager;
import behaviour.Person;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iot.Manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;


public class EventsDB extends NoSQLDB<Person, ArrayList<Event>> {


    private static EventsDB instance = new EventsDB();

    private EventsDB() {
        initComponents();
    }

    public static EventsDB getInstance() {
        return instance;
    }

    @Override
    public void loadProperties() {
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream("eventsdb.properties");
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
        if (Manager.MODE != 0) return;
        dbClient.context().deleteDB("events", "delete database");
        dbClient.context().createDB("events");
    }

    @Override
    public void save(Person p) {
        JsonObject ob;
        if (dbClient.contains(p.getName())) {
            ob = dbClient.find(JsonObject.class, p.getName());
            JsonArray events = ob.getAsJsonArray("events");
            events.add(createJsonObject(p));
            dbClient.update(ob);
        }
        else {
            ob = new JsonObject();
            ob.addProperty("_id", p.getName());
            ob.addProperty("_rev", (String) null);
            ob.add("events", new JsonArray());
            JsonArray events = ob.getAsJsonArray("events");
            events.add(createJsonObject(p));
            dbClient.save(ob);
        }
    }

    @Override
    public ArrayList<Event> fetchData() {
        ArrayList<Event> res = new ArrayList<>();
        List<JsonObject> all_docs = dbClient.view("_all_docs").includeDocs(true).query(JsonObject.class);
        for (JsonObject doc : all_docs) {
            String name = doc.get("_id").getAsString();
            JsonArray events = doc.get("events").getAsJsonArray();
            for (JsonElement e : events) {
                JsonObject ob = e.getAsJsonObject();
                int step = ob.get("step").getAsInt();
                PeopleManager.Action a = PeopleManager.Action.valueOf(ob.get("action").getAsString());
                String dest = ob.get("dest").getAsString();
                int next = ob.get("next").getAsInt();
                int duration = ob.get("duration").getAsInt();
                Event event = new Event(step, name, a, dest, next, duration);
                if (ob.has("assignments")) {
                    JsonObject assigns = ob.get("assignments").getAsJsonObject();
                    event.addComputerId(assigns.get("computer").getAsInt());
                }
                res.add(event);
            }
        }
        res.sort(Comparator.comparingInt(Event::getStep));
        return res;
    }

    private JsonObject createJsonObject(Person p) {
        JsonObject ev = new JsonObject();
        ev.addProperty("step", Manager.CURRENT_STEP);
        ev.addProperty("action", p.getCurrentAction().toString());
        ev.addProperty("dest", p.getLocation());
        ev.addProperty("next", p.getNextActionSteps());
        ev.addProperty("duration", p.getRemainingSteps());
        int computerId = p.getParams().getComputerId();
        if (computerId > -1) {
            JsonObject c = new JsonObject();
            c.addProperty("computer", p.getParams().getComputerId());
            ev.add("assignments", c);
        }
        return ev;
    }
}
