package data;

import building.Building;
import com.google.gson.JsonObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Sembroiz on 02/02/17.
 */
public class BuildingsDB extends NoSQLDB<Building, Building> {
    @Override
    public void loadProperties() {
        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream("buildingsdb.properties");
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
    public void save(Building b) {
    }

    @Override
    public ArrayList<Building> fetchData() {
        return null;
    }

    @Override
    public JsonObject createJsonObject(Building o) {
        return null;
    }
}
