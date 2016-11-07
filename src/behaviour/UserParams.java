package behaviour;

public class UserParams {
	
	private double temperature;
	private double light;
	private boolean hadLunch;
    private boolean hadEntered;
	
	public UserParams(double t, double l) {
		temperature = t;
		light = l;
		this.hadLunch = false;
        this.hadEntered = false;
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

    public boolean hadEntered() {
        return hadEntered;
    }

    public void setHadEntered(boolean hadEntered) {
        this.hadEntered = hadEntered;
    }

}
