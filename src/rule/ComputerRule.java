package rule;

import behaviour.Person;
import building.Room;
import com.google.gson.JsonObject;
import data.SchedulesDB;
import entity.Computer;
import iot.Manager;
import iot.Sensor;
import org.easyrules.core.BasicRule;

class ComputerRule extends BasicRule {

    protected Room room;

    private Sensor power;
    Computer comp;



    ComputerRule(Room r, Computer c, Sensor s) {
        super("Computer rule #" + c.getId(), "Rule to manage computer", c.getId());
        this.room = r;
        this.comp = c;
        this.power = s;
    }

    boolean isGuestComing() {
        int PREDICTION_THRESHOLD = 30;
        Person guest = comp.getUsedBy();
        return guest != null && guest.getNextActionSteps() < PREDICTION_THRESHOLD
                && room.getLocation().equals(guest.getLocation());
    }

    boolean guestReturned() {
        Person p = comp.getUsedBy();
        return p != null && room.getLocation().equals(p.getLocation());
    }

    boolean guestLeft() {
        Person p = comp.getUsedBy();
        return p == null || !room.getLocation().equals(p.getLocation());
    }

    void saveAction() {
        JsonObject root = SchedulesDB.getInstance().createJsonObject(comp.getCurrentState(),
                                                                        Manager.CURRENT_STEP,
                                                                        room.getLocation(),
                                                                        "computer_" + comp.getId());
        SchedulesDB.getInstance().save(root);
    }

}
