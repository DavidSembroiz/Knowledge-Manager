package domain;

import domain.Utils;

public class Register {
	
	private static Register instance = new Register();
	
	private Register() {
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
	
	private final int COMPUTER_CONSUMPTION = 100;
	private final int COMPUTER_SUSPENDED_CONSUMPTION = 25;
	private final int LIGHT_CONSUMPTION = 15;
	private final int HVAC_CONSUMPTION = 800;
	private final int HVAC_MAINTAIN_CONSUMPTION = 500;
	
	/**
	 * Number of elements per ROOM
	 */
	
	private final int COMPUTERS_PER_ROOM = 2;
	private final int LIGHTS_PER_ROOM = 5;
	private final int HVACS_PER_ROOM = 1;
	
	/**
	 * Consumption of IoT elements
	 */
	
	private final int SENSORS_PER_ROOM = 4;
	private final int NUM_GATEWAYS = 10;
	private final double SENSOR_CONSUMPTION = 0.05;
	private final double GATEWAY_CONSUMPTION = 10;
	
	
	private int numComputers;
	private int numSuspComputers;
	private int numLights;
	private int numHvacs;
	private int numMaintHvacs;
	
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
		consumption[Utils.CURRENT_STEP] = cons;
		totalConsumption += cons;
		return cons;
	}
	
	public int getSensorsPerRoom() {
		return SENSORS_PER_ROOM;
	}
	
	/**
	 * Currently all the rooms have Computer. Therefore numComputers = numRooms
	 * 
	 */
	
	public double computeSensorsConsumption() {
		double cons = numComputers * SENSORS_PER_ROOM * SENSOR_CONSUMPTION +
				      GATEWAY_CONSUMPTION * NUM_GATEWAYS;
		return cons;
	}
	
	public void printConsumption() {
		for (int i = 0; i < consumption.length; ++i) {
			System.out.println("Step " + i + " " + consumption[i] + " Watts");
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
