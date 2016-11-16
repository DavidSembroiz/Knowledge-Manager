package entity;

import behaviour.Person;
import iot.Manager;

public class Computer {

    private int id;
    private Person usedBy;
	private State currentState;
	private double accPowerUsage;
    private double consumptionHistory[];

    /*
     * ON: 350 W
     * SUSPEND: 20 W
     */


    public enum State {
		OFF(0),
		ON(350),
		SUSPEND(20);
		
		private final int cons;
		public int getConsumption() {return cons;}
		
		State(int cons) {
			this.cons = cons;
		}
	}
	
	
	public Computer(int id) {

        this.id = id;
        this.currentState = State.OFF;
        consumptionHistory = new double[24];
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

        this.accPowerUsage += cons;
        this.consumptionHistory[Manager.CURRENT_STEP/360] += cons;
	}

	public double getCons() {
        return accPowerUsage;
    }

}
