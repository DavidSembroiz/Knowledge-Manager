package building;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Random;

public class BuildingGenerator {

    private enum ROOM_TYPE {
        OFFICE, MEETING_ROOM, CLASSROOM
    }

    private enum SINGLE_ENTITY {
        LAMP, HVAC, DOOR, WINDOW
    }

    private enum MULTIPLE_ENTITY {
        COMPUTER
    }

    private enum SINGLE_SENSOR {
        LIGHT, PRESENCE, POWER, AIRQUALITY, TEMPERATURE, HUMIDITY, LUMINOSITY, ELECTROMAGNETIC
    }

    private enum MULTIPLE_SENSOR {
        XM1000(SINGLE_SENSOR.TEMPERATURE, SINGLE_SENSOR.HUMIDITY, SINGLE_SENSOR.LUMINOSITY);

        private final SINGLE_SENSOR[] vals;

        MULTIPLE_SENSOR(SINGLE_SENSOR... vals) {
            this.vals = vals;
        }

        public SINGLE_SENSOR[] getSingleSensors() {
            return vals;
        }

    }

    private int SINGLE_ENT_SIZE;
    private int MULTIPLE_ENT_SIZE;
    private int SINGLE_SENSORS_SIZE;
    private int MULTIPLE_SENSORS_SIZE;

    private int total_rooms, office_rooms, meeting_rooms, class_rooms;
    private String building;

    private Random rand;


    public BuildingGenerator(String building, int offices, int meetings, int classes) {
        this.SINGLE_ENT_SIZE = SINGLE_ENTITY.values().length;
        this.MULTIPLE_ENT_SIZE = MULTIPLE_ENTITY.values().length;
        this.SINGLE_SENSORS_SIZE = SINGLE_SENSOR.values().length;
        this.MULTIPLE_SENSORS_SIZE = MULTIPLE_SENSOR.values().length;
        this.building = building;
        this.office_rooms = offices;
        this.meeting_rooms = meetings;
        this.class_rooms = classes;
        total_rooms = office_rooms + meeting_rooms + class_rooms;
        this.rand = new Random();
    }

    public JsonObject generateBuilding() {

        final String _MAIN_ID = "_id";
        final String _ID = "id";
        final String _SIZE = "size";
        final String _TYPE = "type";
        final String _QUANTITY = "quantity";
        final String _ENTITIES = "entities";
        final String _ROOMS = "rooms";
        final String _SENSORS = "sensors";
        final String _MODE = "mode";
        final String _MULTIPLE_MODE = "multiple";
        final String _SINGLE_MODE = "single";
        final String _MOTES = "motes";

        JsonObject root = new JsonObject();
        root.addProperty(_MAIN_ID, building);

        JsonArray roomsList = new JsonArray();
        JsonObject r;
        JsonArray ents, sens;

        for (int i = 0; i < total_rooms; ++i) {
            r = new JsonObject();

            /*
             * Generate Room Identifier
             */

            r.addProperty(_ID, generateRoomId(i));

            /*
             * Generate Room Type
             */
            String type = generateRoomType(i);
            r.addProperty(_TYPE, type);

            /*
             * Generate Room Size
             */

            r.addProperty(_SIZE, generateRoomSize());

            /*
             * Generate Single Entities
             */

            ents = new JsonArray();
            int e = SINGLE_ENT_SIZE;
            int start = rand.nextInt(SINGLE_ENT_SIZE);
            JsonObject entroot;
            for (int j = 0; j < e; ++j) {
                entroot = new JsonObject();
                String ent = SINGLE_ENTITY.values()[start].toString().toLowerCase();
                start = (start + 1) % SINGLE_ENT_SIZE;
                entroot.addProperty(_TYPE, ent);
                entroot.addProperty(_QUANTITY, Integer.toString(1));
                ents.add(entroot);
            }

            /*
             * Generate Multiple Entities
             */

            e = MULTIPLE_ENT_SIZE;
            start = rand.nextInt(MULTIPLE_ENT_SIZE);
            for (int j = 0; j < e; ++j) {
                int qtt = getMultipleEntityQtt(type);;
                if (qtt > 0) {
                    entroot = new JsonObject();
                    String ent = MULTIPLE_ENTITY.values()[start].toString().toLowerCase();
                    start = (start + 1) % MULTIPLE_ENT_SIZE;
                    entroot.addProperty(_TYPE, ent);
                    entroot.addProperty(_QUANTITY, Integer.toString(qtt));
                    ents.add(entroot);
                }
            }
            r.add(_ENTITIES, ents);

            /*
             * Generate Sensors
             */

            sens = new JsonArray();

            /*
             * Generate Multiple Sensors
             */

            e = nextIntFullRange(MULTIPLE_SENSORS_SIZE);
            start = rand.nextInt(MULTIPLE_SENSORS_SIZE);
            JsonObject sensroot;
            for (int j = 0; j < e; ++j) {
                sensroot = new JsonObject();
                MULTIPLE_SENSOR sen = MULTIPLE_SENSOR.values()[start];
                start = (start + 1) % MULTIPLE_SENSORS_SIZE;
                int qtt = nextIntFullRange(4);
                if (qtt > 0) {
                    sensroot.addProperty(_MODE, _MULTIPLE_MODE);
                    sensroot.addProperty(_TYPE, sen.toString());
                    sensroot.addProperty(_QUANTITY, Integer.toString(qtt));
                    SINGLE_SENSOR[] single_sens = sen.getSingleSensors();
                    JsonArray motes = new JsonArray();
                    for (SINGLE_SENSOR single_sen : single_sens) {
                        JsonObject mote = new JsonObject();
                        mote.addProperty(_TYPE, single_sen.toString().toLowerCase());
                        mote.addProperty(_QUANTITY, Integer.toString(1));
                        motes.add(mote);
                    }
                    sensroot.add(_MOTES, motes);
                }
                if (sensroot.entrySet().size() != 0) sens.add(sensroot);
            }

            /*
             * Generate Single Sensors
             */

            e = SINGLE_SENSORS_SIZE;
            start = rand.nextInt(SINGLE_SENSORS_SIZE);
            for (int j = 0; j < e; ++j) {
                int qtt = 20 + nextIntFullRange(10);
                if (qtt > 0) {
                    sensroot = new JsonObject();
                    String sen = SINGLE_SENSOR.values()[start].toString().toLowerCase();
                    start = (start + 1) % SINGLE_SENSORS_SIZE;
                    sensroot.addProperty(_MODE, _SINGLE_MODE);
                    sensroot.addProperty(_TYPE, sen);
                    sensroot.addProperty(_QUANTITY, Integer.toString(qtt));
                    sens.add(sensroot);
                }
            }

            r.add(_SENSORS, sens);

            roomsList.add(r);
            root.add(_ROOMS, roomsList);
        }

        return root;
    }

    private int getMultipleEntityQtt(String type) {
        if (type.equals(ROOM_TYPE.OFFICE.toString())) return 3;
        else if (type.equals(ROOM_TYPE.CLASSROOM.toString())) return 20;
        else if (type.equals(ROOM_TYPE.MEETING_ROOM.toString())) return 2;
        return 0;
    }

    private String generateRoomType(int i) {
        if (i < office_rooms) return ROOM_TYPE.OFFICE.toString();
        else if (i < office_rooms + meeting_rooms) return ROOM_TYPE.MEETING_ROOM.toString();
        return ROOM_TYPE.CLASSROOM.toString();
    }

    private int nextIntFullRange(int n) {
        return rand.nextInt(n) + 1;
    }

    private String generateRoomSize() {
        return Integer.toString(rand.nextInt(200));
    }

    private String generateRoomId(int i) {

        return building + normalizeRoomId(i);
    }

    private String normalizeRoomId(int i) {
        if (i < 10) return "00" + i;
        if (i < 100) return "0" + i;
        return Integer.toString(i);
    }
}
