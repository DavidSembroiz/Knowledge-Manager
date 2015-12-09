package domain;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Properties;

import domain.Utils;

public class Register {
	
	private Properties prop;
	
	private static Register instance = new Register();
	
	private Register() {
		loadProperties();
		initComponents();
	}
	
	public static Register getInstance() {
		return instance;
	}
	
	private int[] consumption;
	private int totalConsumption;
	
	/**
	 * Consumption of every element being tracked
	 */
	
	private int COMPUTER_CONSUMPTION;
	private int COMPUTER_SUSPENDED_CONSUMPTION;
	private int LIGHT_CONSUMPTION;
	private int HVAC_CONSUMPTION;
	private int HVAC_MAINTAIN_CONSUMPTION;
	
	/**
	 * Number of elements per ROOM
	 */
	
	private int COMPUTERS_PER_ROOM;
	private int LIGHTS_PER_ROOM;
	private int HVACS_PER_ROOM;
	
	/**
	 * Consumption of IoT elements
	 */
	private int MODE;
	private int SENSORS_PER_ROOM;
	private int NUM_GATEWAYS;
	private double SENSOR_CONSUMPTION;
	private double GATEWAY_CONSUMPTION;
	
	
	private int numComputers;
	private int numSuspComputers;
	private int numLights;
	private int numHvacs;
	private int numMaintHvacs;
	private int numRooms;
	
	private void loadProperties() {
		prop = new Properties();
		try {
			InputStream is = new FileInputStream("model.properties");
			prop.load(is);
			COMPUTER_CONSUMPTION = Integer.parseInt(prop.getProperty("computer_consumption"));
			COMPUTER_SUSPENDED_CONSUMPTION = Integer.parseInt(prop.getProperty("computer_suspended_consumption"));
			LIGHT_CONSUMPTION = Integer.parseInt(prop.getProperty("light_consumption"));
			HVAC_CONSUMPTION = Integer.parseInt(prop.getProperty("hvac_consumption"));
			HVAC_MAINTAIN_CONSUMPTION = Integer.parseInt(prop.getProperty("hvac_maintain_consumption"));
			
			COMPUTERS_PER_ROOM = Integer.parseInt(prop.getProperty("computers_per_room"));
			LIGHTS_PER_ROOM = Integer.parseInt(prop.getProperty("lights_per_room"));
			HVACS_PER_ROOM = Integer.parseInt(prop.getProperty("hvacs_per_room"));
			
			SENSORS_PER_ROOM = Integer.parseInt(prop.getProperty("sensors_per_room"));
			NUM_GATEWAYS = Integer.parseInt(prop.getProperty("num_gateways"));
			SENSOR_CONSUMPTION = Double.parseDouble(prop.getProperty("sensor_consumption"));
			GATEWAY_CONSUMPTION = Double.parseDouble(prop.getProperty("gateway_consumption"));
			
			is = new FileInputStream("manager.properties");
			prop.load(is);
			MODE = Integer.parseInt(prop.getProperty("mode"));
			numRooms = Integer.parseInt(prop.getProperty("professor_num_rooms")) +
					   Integer.parseInt(prop.getProperty("student_num_rooms")) +
					   Integer.parseInt(prop.getProperty("pas_num_rooms"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initComponents() {
		consumption = new int[Utils.STEPS];
		totalConsumption = 0;
		this.numComputers = 0;
		this.numSuspComputers = 0;
		this.numLights = 0;
		this.numHvacs = 0;
		this.numMaintHvacs = 0;
	}

	public int getNumComputers() {
		return numComputers;
	}

	public void setNumComputers(int numComputers) {
		this.numComputers = numComputers;
	}

	public int getNumLights() {
		return numLights;
	}

	public void setNumLights(int numLights) {
		this.numLights = numLights;
	}
	
	public int getNumMaintHvacs() {
		return numMaintHvacs;
	}

	public int getNumHvacs() {
		return numHvacs;
	}

	public void setNumHvacs(int numHvacs) {
		this.numHvacs = numHvacs;
	}
	
	public void setMaintainHvacFromOn() {
		this.numHvacs--;
		this.numMaintHvacs++;
	}
	
	public void setMaintainHvacFromOff() {
		this.numMaintHvacs++;
	}
	
	public void switchOffMaintHvac() {
		this.numMaintHvacs--;
	}
	
	public void switchComputerOff() {
		this.numComputers--;
	}
	
	public void switchComputerOn() {
		this.numComputers++;
	}
	
	public void switchSuspComputerOff() {
		this.numSuspComputers--;
	}
	
	public void enableSuspendedComputer() {
		this.numSuspComputers--;
		this.numComputers++;
	}
	
	public void putComputerInSuspension() {
		this.numSuspComputers++;
		this.numComputers--;
	}
	
	public void switchSuspComputerOn() {
		this.numSuspComputers++;
	}
	
	public void switchLightOff() {
		this.numLights--;
	}
	
	public void switchLightOn() {
		this.numLights++;
	}
	
	public void switchHvacOff() {
		this.numHvacs--;
	}
	
	public void switchHvacOn() {
		this.numHvacs++;
	}
	
	public int computeConsumption() {
		int cons = numComputers * COMPUTER_CONSUMPTION * COMPUTERS_PER_ROOM +
				   numSuspComputers * COMPUTER_SUSPENDED_CONSUMPTION * COMPUTERS_PER_ROOM + 
				   numLights * LIGHT_CONSUMPTION * LIGHTS_PER_ROOM +
				   numHvacs * HVAC_CONSUMPTION * HVACS_PER_ROOM + 
				   numMaintHvacs * HVAC_MAINTAIN_CONSUMPTION * HVACS_PER_ROOM;
		if (MODE == 1 || MODE == 2) cons += computeSensorsConsumption();
		consumption[Utils.CURRENT_STEP] = cons;
		totalConsumption += cons;
		return cons;
	}
	
	
	
	public int getSensorsPerRoom() {
		return SENSORS_PER_ROOM;
	}

	
	public double computeSensorsConsumption() {
		double cons = numRooms * SENSORS_PER_ROOM * SENSOR_CONSUMPTION +
				      GATEWAY_CONSUMPTION * NUM_GATEWAYS;
		return cons;
	}
	
	public void writeConsumptionToFile() {
		try(PrintWriter wr = new PrintWriter(new BufferedWriter(new FileWriter("res/cons.txt")))) {
			DecimalFormat df = new DecimalFormat("#.###");
			for (int i = 0; i < consumption.length; ++i) {
				wr.println(df.format((consumption[i])/1000.0));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Since the consumption is added every step (10 seconds), the
	 * result must be tuned in order to correctly count Kwh
	 */
	
	public void printTotalConsumption() {
		
		double kwh = totalConsumption / 360.0;
		
		System.out.println("Total consumption: " + kwh);
	}
}
