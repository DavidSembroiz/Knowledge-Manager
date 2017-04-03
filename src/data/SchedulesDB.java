package data;

import iot.Manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;


public class SchedulesDB extends NoSQLDB<Schedule, List<Schedule>> {

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
        if (Manager.MODE != 1) return;
        dbClient.context().deleteDB("schedules", "delete database");
        dbClient.context().createDB("schedules");
    }

    @Override
    public void save(Schedule s) {
        if (dbClient.contains(s.get_id())) {
            s.set_rev(dbClient.find(Schedule.class, s.get_id()).get_rev());
            dbClient.update(s);
        }
        else dbClient.save(s);
    }


    @Override
    public List<Schedule> fetchData() {
        return dbClient.view("_all_docs").includeDocs(true).query(Schedule.class);
    }

}
