package entity;

import behaviour.Person;
import iot.Manager;

public class Computer {

    private int id;
    private Person usedBy;
	private State currentState;
    private double consumptionHistory[];

    /*
     * ON: 350 W
     * SUSPEND: 50 W
     */


    public enum State {
		OFF(0),
		ON(350),
		SUSPEND(50);
		
		private final int cons;
		public int getConsumption() {return cons;}
		
		State(int cons) {
			this.cons = cons;
		}
	}
	
	
	public Computer(int id) {

        this.id = id;
        this.currentState = State.OFF;
        consumptionHistory = new double[Manager.CONSUMPTION_RESOLUTION];
	}

    public double getHourlyConsumption(int i) {
        return consumptionHistory[i];
    }

    public int getId() {
        return id;
    }

    public void setUsedBy(Person p) {
        this.usedBy = p;
    }

    public Person getUsedBy() {
        return usedBy;
    }

	public State getCurrentState() {
		return currentState;
	}


	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}


	public void addConsumption(double cons) {
        this.consumptionHistory[Manager.CURRENT_STEP/(360/(Manager.CONSUMPTION_RESOLUTION/24))] += cons;
	}
}
