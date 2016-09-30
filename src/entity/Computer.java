package entity;

public class Computer {
	
	private State currentState;
	
	private enum State {
		OFF(0),
		ON(100),
		SUSPEND(50);
		
		private final int cons;
		public int getConsumption() {return cons;}
		
		State(int cons) {
			this.cons = cons;
		}
	}
	
	
	public Computer() {
		
	}

}
