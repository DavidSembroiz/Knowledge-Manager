package behaviour;

import behaviour.PeopleManager.Action;


public class Event implements Comparable<Event> {

    private int step;
    private String name;
    private Action action;
    private String dest;
    private int next;
    private int duration;

    public Event(int step, String name, Action a, String dest, int next, int duration) {
        this.step = step;
        this.name = name;
        this.action = a;
        this.dest = dest;
        this.next = next;
        this.duration = duration;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(Event other) {
        return Integer.compare(step, other.getStep());
    }
}
