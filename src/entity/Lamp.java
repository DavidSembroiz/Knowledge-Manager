package entity;

public class Lamp {
	
	private State currentState;
	private double accPowerUsage;
	
	public enum State {
		OFF(0),
		ON(100);
		
		private final int cons;
		public int getConsumption() {return cons;}
		
		State(int cons) {
			this.cons = cons;
		}
	}
	
	
	public Lamp() {
		this.currentState = State.OFF;
	}


	public State getCurrentState() {
		return currentState;
	}


	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}


	public void addConsumption(double cons) {
		this.accPowerUsage += cons;
	}

    public double getCons() {
        return accPowerUsage;
    }

}
