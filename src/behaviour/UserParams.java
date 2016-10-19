package behaviour;

public class UserParams {
	
	private double temperature;
	private double light;
	private boolean hadLunch;
	
	public UserParams(double t, double l) {
		temperature = t;
		light = l;
		this.hadLunch = false;
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

	public boolean hadLunch() {
		return hadLunch;
	}

	public void setHadLunch(boolean hadLunch) {
		this.hadLunch = hadLunch;
	}

}
