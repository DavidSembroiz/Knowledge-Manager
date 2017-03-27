package learn_rules;

import building.Room;
import entity.Computer;
import entity.Computer.State;
import iot.Manager;
import iot.Sensor;
import rule_headers.ComputerRule;

public class LearnComputer extends ComputerRule {


    public LearnComputer(Room r, Computer c, Sensor s) {
        super(r, c, s);
    }

    /*
        Checks if the computer was changed too soon, too late or at the necessary time:
            * 1 for too soon
            * 0 for correctly
            * -1 for too late
     */

    private int checkWhen() {
        int res = Manager.CURRENT_STEP - super.comp.getTimeChanged();
        if (res == 60) return 0;
        return res > 60 ? 1 : -1;

    }

	
	@Override
	public boolean evaluate() {
        Computer comp = super.comp;
		State st = comp.getCurrentState();

		if (st.equals(State.ON)) {
            int time = checkWhen();
            if (time != 0) adjustSchedule(time);
        }
        return false;
	}

    private void adjustSchedule(int time) {
        if (time == 1) {

        }
    }


    @Override
	public void execute() throws Exception {
	}

}
