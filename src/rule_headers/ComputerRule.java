package rule_headers;

import behaviour.Person;
import building.Room;
import entity.Computer;
import iot.Manager;
import iot.Sensor;
import org.easyrules.core.BasicRule;

public class ComputerRule extends BasicRule {

    protected Room room;

    protected Sensor power;
    protected Computer comp;



    public ComputerRule(Room r, Computer c, Sensor s) {
        super("Computer rule_headers #" + c.getId(), "Rule to manage computer", c.getId());
        this.room = r;
        this.comp = c;
        this.power = s;
    }

    protected boolean isGuestComing(int threshold) {
        Person guest = comp.getUsedBy();
        return guest != null && guest.getNextActionSteps() < threshold
                && room.getLocation().equals(guest.getLocation());
    }

    protected boolean guestReturned() {
        Person p = comp.getUsedBy();
        return p != null && room.getLocation().equals(p.getLocation());
    }

    protected boolean guestLeft() {
        Person p = comp.getUsedBy();
        return p == null || !room.getLocation().equals(p.getLocation());
    }

    protected void saveAction() {
        room.addTimeToSchedule("computer_" + comp.getId(), Manager.CURRENT_STEP, comp.getCurrentState().toString());
    }

}
