package behaviour;

import behaviour.PeopleManager.Action;


public class Event implements Comparable<Event> {

    private int step;
    private String name;
    private Action action;
    private String dest;
    private int next;
    private int duration;
    private int computerId;


    public Event(int step, String name, Action a, String dest, int next, int duration) {
        this.step = step;
        this.name = name;
        this.action = a;
        this.dest = dest;
        this.next = next;
        this.duration = duration;
        this.computerId = -1;
    }

    public int getStep() {
        return step;
    }

    public String getName() {
        return name;
    }

    Action getAction() {
        return action;
    }

    String getDest() {
        return dest;
    }

    int getNext() {
        return next;
    }

    int getDuration() {
        return duration;
    }

    @Override
    public int compareTo(Event other) {
        return Integer.compare(step, other.getStep());
    }

    public void addComputerId(int computerId) {
        this.computerId = computerId;
    }

    int getComputerId() {
        return computerId;
    }
}
