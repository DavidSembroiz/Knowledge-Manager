package rule;

import building.Room;
import domain.Debugger;
import entity.Window;
import entity.Window.State;
import iot.Sensor;
import org.easyrules.core.BasicRule;

class WindowRule extends BasicRule {

    private Room room;

    private Sensor airquality;
    private Window window;


    WindowRule(Room r, Window w, Sensor s) {
        super("Window rule #" + w.getId(), "Rule to manage Windows", w.getId());
        this.room = r;
        this.window = w;
        this.airquality = s;
    }

    private boolean properAirQuality() {
        Double val = Double.parseDouble(airquality.getValue());
        return val < 4;
    }

	
	@Override
	public boolean evaluate() {
        State st = window.getCurrentState();
        if (st.equals(State.OPEN)) {
            if (properAirQuality()) return true;
        }
        else if (st.equals(State.CLOSE)) {
            if (!properAirQuality()) return true;
        }
        return false;
	}

    @Override
	public void execute() throws Exception {
		State st = window.getCurrentState();
		if (st.equals(State.OPEN)) {
            if (Debugger.isEnabled()) Debugger.log("Window CLOSED in room " + room.getLocation());
            window.setCurrentState(State.CLOSE);
        }
        else if (st.equals(State.CLOSE)) {
            if (Debugger.isEnabled()) Debugger.log("Window OPEN in room " + room.getLocation());
            window.setCurrentState(State.OPEN);
        }
	}
}
