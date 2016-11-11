package rules;

import building.Room;
import entity.Window;
import entity.Window.State;
import iot.Sensor;
import org.easyrules.annotation.Rule;
import org.easyrules.core.BasicRule;

@Rule(name = "Window Management Rule")
public class WindowRule extends BasicRule {

    private int PREDICTION_THRESHOLD = 10;

    private Room room;

    private Sensor electro;
    private Window window;


    public WindowRule(Room r, Window d, Sensor s) {
        this.room = r;
        this.window = d;
        this.electro = s;
    }

	
	@Override
	public boolean evaluate() {
		State st = window.getCurrentState();
        if (st.equals(State.OPEN)) {
        }
        return false;
	}
	
	@Override
	public void execute() throws Exception {
		System.out.println("Window triggered");
	}
}
