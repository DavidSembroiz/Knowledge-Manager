package entity;

import iot.Manager;

public class HVAC {

    private int id;
	private State currentState;
    private double consumptionHistory[];

    /*
     * ON: 1500 W
     * SUSPEND: 200 W
     */

    public enum State {
		OFF(0),
		ON(1500),
		SUSPEND(200);
		
		private final int cons;
		public int getConsumption() {return cons;}
		
		State(int cons) {
			this.cons = cons;
		}
	}
	
	
	public HVAC(int id) {

        this.id = id;
        this.currentState = State.OFF;
        consumptionHistory = new double[24];
	}

    public int getId() {
        return id;
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
        this.consumptionHistory[Manager.CURRENT_STEP/360] += cons;
	}
}
