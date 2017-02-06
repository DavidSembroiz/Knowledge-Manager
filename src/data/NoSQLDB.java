package data;

import iot.Manager;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;


public abstract class NoSQLDB<Input, Output> {

    protected String DB;
    protected boolean CREATE_IF_NOT_EXIST;
    protected String PROTOCOL;
    protected String HOST;
    protected int PORT;

    protected CouchDbClient dbClient;

    abstract public void loadProperties();

    abstract public void save(Input o);

    abstract public Output fetchData();


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
