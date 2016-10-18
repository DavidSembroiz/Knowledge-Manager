package entity;

public class HVAC {
	
	private State currentState;
	private double accPowerUsage;
	
	public enum State {
		OFF(0),
		ON(100),
		SUSPEND(50);
		
		private final int cons;
		public int getConsumption() {return cons;}
		
		State(int cons) {
			this.cons = cons;
		}
	}
	
	
	public HVAC() {
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

}
