package rules;

import building.Room;
import entity.Window;
import entity.Window.State;
import iot.Sensor;
import org.easyrules.annotation.Action;
import org.easyrules.annotation.Condition;
import org.easyrules.annotation.Rule;

@Rule(name = "Window Management Rule")
public class WindowRule {

    private int PREDICTION_THRESHOLD = 10;

    private Room room;

    private Sensor electro;
    private Window window;


    public WindowRule(Room r, Window d, Sensor s) {
        this.room = r;
        this.window = d;
        this.electro = s;
    }

	
	@Condition
	public boolean checkWindow() {
		State st = window.getCurrentState();
        if (st.equals(State.OPEN)) {
        }
        return false;
	}
	
	@Action(order = 1)
	public void switchOffLight() throws Exception {
		System.out.println("Window triggered");
	}
}
