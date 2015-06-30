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
	private final int LIGHT_CONSUMPTION = 15;
	private final int HVAC_CONSUMPTION = 800;
	
	private final int COMPUTERS_PER_ROOM = 2;
	private final int LIGHTS_PER_ROOM = 5;
	private final int HVACS_PER_ROOM = 1;
	
	private int numComputers;
	private int numLights;
	private int numHvacs;
	
	private void initComponents() {
		consumption = new int[Utils.STEPS];
		this.numComputers = 0;
		this.numLights = 0;
		this.numHvacs = 0;
	}
	
	
	/*public Register(int numComputers, int numLights, int numHvacs) {
		consumption = new int[Utils.STEPS];
		this.numComputers = numComputers;
		this.numLights = numLights;
		this.numHvacs = numHvacs;
	}*/

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
	
	public void switchComputerOff() {
		this.numComputers--;
	}
	
	public void switchComputerOn() {
		this.numComputers++;
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
	
	public int computeConsumption(int s) {
		int cons = numComputers * COMPUTER_CONSUMPTION * COMPUTERS_PER_ROOM +
				   numLights * LIGHT_CONSUMPTION * LIGHTS_PER_ROOM+
				   numHvacs * HVAC_CONSUMPTION * HVACS_PER_ROOM;
		consumption[s] = cons;
		return cons;
	}
	
	public void printConsumption() {
		for (int i = 0; i < consumption.length; ++i) {
			printStepConsumption(i);
		}
	}
	
	public void printStepConsumption(int s) {
		System.out.println(consumption[s]);
	}
}
