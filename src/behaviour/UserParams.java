package behaviour;

public class UserParams {
	
	private double temperature;
	private double light;
	private boolean hadLunch;
    private boolean hadEntered;
	
	UserParams(double t, double l) {
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

	boolean hadLunch() {
		return hadLunch;
	}

	void setHadLunch() {
		this.hadLunch = true;
	}

    boolean hadEntered() {
        return hadEntered;
    }

    void setHadEntered() {
        this.hadEntered = true;
    }

}
