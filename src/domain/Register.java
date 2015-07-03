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
	
	private final int COMPUTER_CONSUMPTION = 100;
	private final int COMPUTER_SUSPENDED_CONSUMPTION = 25;
	private final int LIGHT_CONSUMPTION = 15;
	private final int HVAC_CONSUMPTION = 800;
	private final int HVAC_MAINTAIN_CONSUMPTION = 500;
	
	private final int COMPUTERS_PER_ROOM = 2;
	private final int LIGHTS_PER_ROOM = 5;
	private final int HVACS_PER_ROOM = 1;
	
	private int numComputers;
	private int numSuspComputers;
	private int numLights;
	private int numHvacs;
	private int numMaintHvacs;
	
	private void initComponents() {
		consumption = new int[Utils.STEPS];
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
		return cons;
	}
	
	public void printConsumption() {
		for (int i = 0; i < consumption.length; ++i) {
			printStepConsumption();
		}
	}
	
	public void printStepConsumption() {
		System.out.println("Current consumption: " + consumption[Utils.CURRENT_STEP] + " Watts");
	}

	
}
