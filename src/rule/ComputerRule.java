package rule;

import behaviour.Person;
import building.Room;
import entity.Computer;
import iot.Sensor;
import org.easyrules.core.BasicRule;

public class ComputerRule extends BasicRule {

    protected int PREDICTION_THRESHOLD = 30;

    protected Room room;

    protected Sensor power;
    protected Computer comp;



    protected ComputerRule(Room r, Computer c, Sensor s) {
        super("Computer rule #" + c.getId(), "Rule to manage computer", c.getId());
        this.room = r;
        this.comp = c;
        this.power = s;
    }

    protected boolean isGuestComing() {
        Person guest = comp.getUsedBy();
        return guest != null &&
                guest.getNextActionSteps() < PREDICTION_THRESHOLD && room.getLocation().equals(guest.getLocation());
    }

    protected boolean guestReturned() {
        Person p = comp.getUsedBy();
        return p != null || room.getLocation().equals(p.getLocation());
    }

    protected boolean guestLeft() {
        Person p = comp.getUsedBy();
        return p == null || !room.getLocation().equals(p.getLocation());
    }
}
