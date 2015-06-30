package domain;

import domain.Utils;

public class Register {
	
	
	private int[] consumption; 
	
	private final int COMPUTER_CONSUMPTION = 100;
	private final int LIGHT_CONSUMPTION = 15;
	private final int HVAC_CONSUMPTION = 800;
	
	private int numComputers;
	private int numLights;
	private int numHvacs;
	
	public Register() {
		consumption = new int[Utils.STEPS];
		numComputers = 0;
		numLights = 0;
		numHvacs = 0;
	}
	
	public Register(int numComputers, int numLights, int numHvacs) {
		consumption = new int[Utils.STEPS];
		this.numComputers = numComputers;
		this.numLights = numLights;
		this.numHvacs = numHvacs;
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
	
	public int computeConsumption(int s) {
		int cons = numComputers * COMPUTER_CONSUMPTION +
				   numLights * LIGHT_CONSUMPTION +
				   numHvacs * HVAC_CONSUMPTION;
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
