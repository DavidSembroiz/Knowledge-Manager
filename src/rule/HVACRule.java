package rule;

import behaviour.Person;
import building.Room;
import entity.HVAC;
import entity.Window;
import iot.Sensor;
import model.ModelManager;
import org.easyrules.core.BasicRule;

import java.util.ArrayList;

class HVACRule extends BasicRule {

    int PREDICTION_THRESHOLD = 60;

    Room room;
    private ModelManager models;

    Sensor temperature;
    private Sensor humidity;
    Window window;
    HVAC hvac;

    HVACRule(Room r, HVAC h, Window w, Sensor temp, Sensor hum) {
        super("HVAC rule #" + h.getId(), "Rule to manage HVAC", h.getId());
        models = ModelManager.getInstance();
        temperature = temp;
        humidity = hum;
        this.window = w;
        hvac = h;
        this.room = r;
    }

    private Double getPeopleTemperature() {
        double accTemp = 0;
        ArrayList<Person> people = room.getPeopleActing();
        for (Person p : people) accTemp += p.getParams().getTemperature();
        return people.size() > 0 ? (accTemp/people.size()) : 21;
    }

    boolean currentTemperatureOK() {
        double pplTemp = getPeopleTemperature();
        double roomTemp = Double.parseDouble(temperature.getValue());
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

    boolean temperatureOK() {
        return environmentalTemperatureOK() || currentTemperatureOK();
    }

    /**
     * Temperature moderation when HVAC is OFF
     */

    void moderateTemperature() {
        double roomTemp = Double.parseDouble(temperature.getValue());
        double environTemp = models.getCurrentEnvironmentalTemperature();
        double newTemp;
        if (roomTemp == environTemp) return;
        if (roomTemp > environTemp) newTemp = roomTemp - (roomTemp - environTemp) * 0.005;
        else newTemp = roomTemp + (environTemp - roomTemp) * 0.005;
        temperature.setValue(Double.toString(newTemp));
    }

    /**
     * When HVAC is ON, temperature is adjusted at a 10 degree every 30 minutes ratio
     */

    void adjustTemperature() {
        double roomTemp = Double.parseDouble(temperature.getValue());
        double pplTemp = getPeopleTemperature();
        double newTemp = 0;
        if (roomTemp < pplTemp) newTemp = roomTemp + (pplTemp - roomTemp) * 0.01;
        else if (pplTemp < roomTemp) newTemp = roomTemp - (roomTemp - pplTemp) * 0.01;
        temperature.setValue(Double.toString(newTemp));
    }

    /**
     * Temperature adjustment when HVAC is SUSPENDED
     */

    void suspendTemperature() {
        double roomTemp = Double.parseDouble(temperature.getValue());
        double environTemp = models.getCurrentEnvironmentalTemperature();
        double newTemp;
        if (roomTemp == environTemp) return;
        if (roomTemp > environTemp) newTemp = roomTemp - (roomTemp - environTemp) * 0.0025;
        else newTemp = roomTemp + (environTemp - roomTemp) * 0.0025;
        temperature.setValue(Double.toString(newTemp));
    }

    boolean reactivateFromSuspend() {
        double pplTemp = getPeopleTemperature();
        double roomTemp = Double.parseDouble(temperature.getValue());
        return Math.abs(pplTemp - roomTemp) > 3;
    }


}
