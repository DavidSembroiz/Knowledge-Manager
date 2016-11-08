package rules;

import behaviour.Person;
import building.Room;
import domain.Debugger;
import entity.HVAC;
import entity.HVAC.State;
import iot.Manager;
import iot.Sensor;
import models.Weather;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;

import java.util.ArrayList;

@Rule(name = "HVAC Management Rule")
public class HVACRule {

    private int PREDICTION_THRESHOLD = 60;
	
	private Room room;
	private Weather models;
	
	private Sensor temperature;
	private Sensor humidity;
	private HVAC hvac;
	
	public HVACRule(Room r, HVAC h, Sensor temp, Sensor hum) {
		models = Weather.getInstance();
		temperature = temp;
		humidity = hum;
		hvac = h;
		this.room = r;
	}
	
	private Double getPeopleTemperature() {
		double accTemp = 0;
		ArrayList<Person> people = room.getPeopleActing();
		for (Person p : people) accTemp += p.getParams().getTemperature();
		return people.size() > 0 ? (accTemp/people.size()) : -1.0;
	}
	
	private boolean currentTemperatureOK() {
		double pplTemp = getPeopleTemperature();
		double roomTemp = Double.parseDouble(temperature.getValue());
		if (Math.abs(pplTemp - roomTemp) < 0.5) return true;
		return false;
	}
	
	private boolean environmentalTemperatureOK() {
		
		Double temp = models.getCurrentEnvironmentalTemperature();
		Double hum = models.getCurrentEnvironmentalHumidity();
		
		/**
		 * Summer
		 */
		
		if (hum < 45 && temp < 23 && temp > 22) return true;
		else if (hum > 60 && temp < 22 && temp > 21) return true;
		else if (temp < 22.5 && temp > 21.5) return true;
		return false;
		
		/**
		 * Winter
		
		if (hum < 45 && temp < 25.5 && temp > 20.5) return true;
		else if (hum > 60 && temp < 24 && temp > 20) return true;
		else if (temp < 24.7 && temp > 20.2) return true;
		return false;
		
		*/
	}

	private boolean temperatureOK() {
        return environmentalTemperatureOK() || currentTemperatureOK();
    }

	
	private void moderateTemperature() {
		double roomTemp = Double.parseDouble(temperature.getValue());
		double environTemp = models.getCurrentEnvironmentalTemperature();
		double newTemp;
		if (roomTemp == environTemp) return;
		if (roomTemp > environTemp) newTemp = roomTemp - (roomTemp - environTemp) * 0.005;
		else newTemp = roomTemp + (environTemp - roomTemp) * 0.005;
        if (Manager.CURRENT_STEP%50 == 0 && Debugger.isEnabled()) Debugger.log(newTemp + " ºC in room " + room.getLocation() + " with HVAC OFF");
		temperature.setValue(Double.toString(newTemp));
	}

    /**
     * When HVAC is ON, temperature is adjusted at a 10 degree every 30 minutes ratio
     */

	private void adjustTemperature() {
        double roomTemp = Double.parseDouble(temperature.getValue());
        double pplTemp = getPeopleTemperature();
        double newTemp = 0;
        if (roomTemp < pplTemp) newTemp = roomTemp + (pplTemp - roomTemp) * 0.01;
        else if (pplTemp < roomTemp) newTemp = roomTemp - (roomTemp - pplTemp) * 0.01;
        if (Manager.CURRENT_STEP%50 == 0 && Debugger.isEnabled()) Debugger.log(newTemp + " ºC in room " + room.getLocation() + " with HVAC ON");
        temperature.setValue(Double.toString(newTemp));
    }

    private void suspendTemperature() {
        if (Manager.CURRENT_STEP%50 == 0 && Debugger.isEnabled())
            Debugger.log(temperature.getValue() + " ºC in room " + room.getLocation() + " with HVAC Suspended");
    }
	
	
	@Condition
	public boolean checkConditions() {
		State st = hvac.getCurrentState();
		
		if (st.equals(State.OFF)) {
            moderateTemperature();
			if ((room.arePeopleInside() || room.arePeopleComing(PREDICTION_THRESHOLD))
                    && !temperatureOK()) return true;
		}
		
		if (st.equals(State.ON)) {
            adjustTemperature();
			if (currentTemperatureOK()) return true;
			else if (room.isEmpty() && !room.arePeopleComing(PREDICTION_THRESHOLD)) return true;
		}

		if (st.equals(State.SUSPEND)) {
            suspendTemperature();
            if (room.isEmpty() && !room.arePeopleComing(PREDICTION_THRESHOLD)) return true;
        }
		return false;
	}
	
	
	
	@Action(order = 1)
	public void apply() throws Exception {
		State st = hvac.getCurrentState();
		if (st.equals(State.OFF)) {
            if (Debugger.isEnabled()) Debugger.log("HVAC switched ON in room " + room.getLocation());
            hvac.setCurrentState(State.ON);
        }
		else if (st.equals(State.ON)) {
			if (room.isEmpty()) {
                if (Debugger.isEnabled()) Debugger.log("HVAC switched OFF in room " + room.getLocation());
                hvac.setCurrentState(State.OFF);
            }
			else {
                if (Debugger.isEnabled()) Debugger.log("HVAC SUSPENDED in room " + room.getLocation());
                hvac.setCurrentState(State.SUSPEND);
            }
		}
		else if (st.equals(State.SUSPEND)) {
            if (Debugger.isEnabled()) Debugger.log("HVAC switched from SUSPENDED to OFF in room " + room.getLocation());
            hvac.setCurrentState(State.OFF);
        }
	}
}
