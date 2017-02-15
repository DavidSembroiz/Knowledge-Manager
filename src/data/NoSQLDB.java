package data;

import iot.Manager;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;


public abstract class NoSQLDB<Input, Output> {

    String DB;
    boolean CREATE_IF_NOT_EXIST;
    String PROTOCOL;
    String HOST;
    int PORT;

    CouchDbClient dbClient;

    abstract public void loadProperties();

    abstract public void correctInitialState();

    abstract public void save(Input o);

    abstract public Output fetchData();


    void initComponents() {
        loadProperties();
        CouchDbProperties properties = new CouchDbProperties()
                .setDbName(DB)
                .setCreateDbIfNotExist(CREATE_IF_NOT_EXIST)
                .setProtocol(PROTOCOL)
                .setHost(HOST)
                .setPort(PORT);
        dbClient = new CouchDbClient(properties);
        if (Manager.MODE == 0) correctInitialState();
    }

    final public void shutdown() {
        dbClient.shutdown();
    }

}
