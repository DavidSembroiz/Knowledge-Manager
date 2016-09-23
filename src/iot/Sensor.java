package iot;

public class Sensor {
	
	
	private enum State {
		OFF (0),
		IDLE (5),
		ON (25),
		SLEEP (2);
		
		private final int cons;
		public int getConsumption() {return cons;}
		
		State(int cons) {
			this.cons = cons;
		}
	}
	
	private String soID;
	private String id;
	private String type;
	private String value;
	private String mote;
	private State currentState;
	
	
	/**
	 * Consumption values
	 */
	
	
	public Sensor(String soID, String type) {
		this.soID = soID;
		this.type = type;
		this.currentState = State.ON;
	}
	
	public Sensor(String id, String type, String val) {
		this.soID = "";
		this.id = id;
		this.type = type;
		this.value = val;
		this.mote = id.split("_")[0];
		this.currentState = State.ON;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public String getMote() {
		return mote;
	}

	public void setMote(String mote) {
		this.mote = mote;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		System.out.println(value);
		this.value = value;
	}

	public String getSoID() {
		return soID;
	}

	public void setSoID(String soID) {
		this.soID = soID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
}