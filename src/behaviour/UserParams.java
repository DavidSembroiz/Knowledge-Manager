package behaviour;

public class UserParams {
	
	private double temperature;
	private double light;
	
	public UserParams(double t, double l) {
		temperature = t;
		light = l;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public double getLight() {
		return light;
	}

	public void setLight(double light) {
		this.light = light;
	}
	
	

}
