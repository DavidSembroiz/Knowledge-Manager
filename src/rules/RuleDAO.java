package rules;

public class RuleDAO {

	private String actuator;
	private String sensors;
	private String ruleName;
	
	public RuleDAO(String a, String s, String r) {
		actuator = a;
		sensors = s;
		ruleName = r;
	}

	public String getActuator() {
		return actuator;
	}

	public void setActuator(String actuator) {
		this.actuator = actuator;
	}

	public String getSensors() {
		return sensors;
	}

	public void setSensors(String sensors) {
		this.sensors = sensors;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	
}
