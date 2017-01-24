package data;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.lightcouch.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class EventsDB {

    private static EventsDB instance = new EventsDB();

    private EventsDB() {
        initComponents();
    }

    public static EventsDB getInstance() {
        return instance;
    }

    private String DB;
    private boolean CREATE_IF_NOT_EXIST;
    private String PROTOCOL;
    private String HOST;
    private int PORT;

    private CouchDbClient dbClient;

    private void initComponents() {
        loadProperties();
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName(DB)
                .setCreateDbIfNotExist(CREATE_IF_NOT_EXIST)
                .setProtocol(PROTOCOL)
                .setHost(HOST)
                .setPort(PORT);
        dbClient = new CouchDbClient(properties);
    }

    private void loadProperties() {
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream("couchdb.properties");
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

    public void save(Object o) {
        Response res = dbClient.save(o);
    }
}
