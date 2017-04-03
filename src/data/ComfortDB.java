package data;

import behaviour.Event;
import behaviour.Person;
import iot.Manager;

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
        if (Manager.MODE != 0) return;
        dbClient.context().deleteDB("comfort", "delete database");
        dbClient.context().createDB("comfort");
    }

    @Override
    public void save(Person p) {

    }

    @Override
    public ArrayList<Event> fetchData() {
        return null;
    }

}
