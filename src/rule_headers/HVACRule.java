package rule_headers;

import behaviour.Person;
import building.Room;
import domain.CustomFileWriter;
import entity.HVAC;
import entity.Window;
import iot.Manager;
import iot.Sensor;
import model.ModelManager;
import org.easyrules.core.BasicRule;

import java.util.ArrayList;

public class HVACRule extends BasicRule {

    protected int PREDICTION_THRESHOLD = 0;

    protected Room room;
    private ModelManager models;

    protected Sensor temperature;
    protected Sensor humidity;
    protected Window window;
    protected HVAC hvac;
    private CustomFileWriter writer;

    public HVACRule(Room r, HVAC h, Window w, Sensor temp, Sensor hum) {
        super("HVAC rule_headers #" + h.getId(), "Rule to manage HVAC", h.getId());
        models = ModelManager.getInstance();
        temperature = temp;
        humidity = hum;
        this.window = w;
        hvac = h;
        this.room = r;
        writer = new CustomFileWriter("./res/results/temperature.log");
    }

    protected void sampleTemperature() {
        String room = "upc/campusnord/d3001";
        if (this.room.getLocation().equals(room) && Manager.CURRENT_STEP%60 == 0) {
            writer.write(temperature.getValue());
        }
    }

    protected Integer getPredictionThreshold() { return PREDICTION_THRESHOLD; }

    private Double getPeopleTemperature() {
        double accTemp = 0;
        ArrayList<Person> people = room.getPeopleActing();
        for (Person p : people) accTemp += p.getParams().getTemperature();
        return people.size() > 0 ? (accTemp/people.size()) : 21;
    }

    protected boolean currentTemperatureOK() {
        double roomTemp = Double.parseDouble(temperature.getValue());
        double pplTemp = getPeopleTemperature();
        return Math.abs(pplTemp - roomTemp) < 0.5;
    }

    private boolean environmentalTemperatureOK() {

        Double temp = models.getCurrentEnvironmentalTemperature();
        Double hum = models.getCurrentEnvironmentalHumidity();

        /*
         * Summer
         */

        if (hum < 45 && temp < 23 && temp > 22) return true;
        else if (hum > 60 && temp < 22 && temp > 21) return true;
        else if (temp < 22.5 && temp > 21.5) return true;
        return false;

        /*
         * Winter

         if (hum < 45 && temp < 25.5 && temp > 20.5) return true;
         else if (hum > 60 && temp < 24 && temp > 20) return true;
         else if (temp < 24.7 && temp > 20.2) return true;
         return false;

         */
    }

    protected boolean temperatureOK() {
        return environmentalTemperatureOK() || currentTemperatureOK();
    }

    /**
     * Temperature moderation when HVAC is OFF
     */

    protected void moderateTemperature() {
        double roomTemp = Double.parseDouble(temperature.getValue());
        double environTemp = models.getCurrentEnvironmentalTemperature();
        double newTemp;
        double factor = 0.005;
        if (roomTemp == environTemp) return;
        if (roomTemp > environTemp) newTemp = roomTemp - (roomTemp - environTemp) * factor;
        else newTemp = roomTemp + (environTemp - roomTemp) * factor;
        temperature.setValue(Double.toString(newTemp));
    }

    /**
     * When HVAC is ON, temperature is adjusted at a 10 degree every 30 minutes ratio
     */

    protected void adjustTemperature() {
        double roomTemp = Double.parseDouble(temperature.getValue());
        double pplTemp = getPeopleTemperature();
        double newTemp = 0;
        double factor = 0.01;
        if (roomTemp < pplTemp) newTemp = roomTemp + (pplTemp - roomTemp) * factor;
        else if (pplTemp < roomTemp) newTemp = roomTemp - (roomTemp - pplTemp) * factor;
        temperature.setValue(Double.toString(newTemp));
    }

    /**
     * Temperature adjustment when HVAC is SUSPENDED
     */

    protected void suspendTemperature() {
        double roomTemp = Double.parseDouble(temperature.getValue());
        double environTemp = models.getCurrentEnvironmentalTemperature();
        double newTemp;
        double factor = 0.0025;
        if (roomTemp == environTemp) return;
        if (roomTemp > environTemp) newTemp = roomTemp - (roomTemp - environTemp) * factor;
        else newTemp = roomTemp + (environTemp - roomTemp) * factor;
        temperature.setValue(Double.toString(newTemp));
    }

    protected boolean reactivateFromSuspend() {
        double pplTemp = getPeopleTemperature();
        double roomTemp = Double.parseDouble(temperature.getValue());
        return Math.abs(pplTemp - roomTemp) > 1;
    }

    protected void saveAction() {
        room.addTimeToSchedule("hvac_" + hvac.getId(), Manager.CURRENT_STEP, hvac.getCurrentState().toString());
    }

}
