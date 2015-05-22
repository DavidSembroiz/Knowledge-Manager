package iot;

public class Sensor {
	
	private String soID;
	private String type;
	private String value;
	
	
	public Sensor(String soID, String type) {
		this.soID = soID;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
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
