package entity;

import iot.Manager;

public class Lamp {

    private int id;
	private State currentState;
	private double accPowerUsage;
    private double consumptionHistory[];

    public int getId() {
        return id;
    }

    /*
     * ON: 200 W
     */

    public enum State {
        ON(200),
		OFF(0);

		
		private final int cons;
		public int getConsumption() {return cons;}
		
		State(int cons) {
			this.cons = cons;
		}
	}
	
	
	public Lamp(int id) {
        this.id = id;
        this.currentState = State.OFF;
        consumptionHistory = new double[24];
	}

    public double getHourlyConsumption(int i) {
        return consumptionHistory[i];
    }

	public State getCurrentState() {
		return currentState;
	}


	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}


	public void addConsumption(double cons) {

        this.accPowerUsage += cons;
        this.consumptionHistory[Manager.CURRENT_STEP/360] += cons;
	}

    public double getCons() {
        return accPowerUsage;
    }

}
