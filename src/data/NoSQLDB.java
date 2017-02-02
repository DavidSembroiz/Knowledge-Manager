package data;

import com.google.gson.JsonObject;
import iot.Manager;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.util.ArrayList;


public abstract class NoSQLDB<Input, Output> {

    protected String DB;
    protected boolean CREATE_IF_NOT_EXIST;
    protected String PROTOCOL;
    protected String HOST;
    protected int PORT;

    protected CouchDbClient dbClient;

    abstract public void loadProperties();

    abstract public void save(Input o);

    abstract public ArrayList<Output> fetchData();

    abstract public JsonObject createJsonObject(Input o);


    public void initComponents() {
        loadProperties();
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName(DB)
                .setCreateDbIfNotExist(CREATE_IF_NOT_EXIST)
                .setProtocol(PROTOCOL)
                .setHost(HOST)
                .setPort(PORT);
        dbClient = new CouchDbClient(properties);
        if (Manager.MODE == 0) {
            dbClient.context().deleteDB(DB, "delete database");
            dbClient = new CouchDbClient(properties);
        }
    }

    final public void shutdown() {
        dbClient.shutdown();
    }

}
