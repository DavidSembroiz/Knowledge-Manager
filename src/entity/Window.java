package entity;

public class Window {

    private int id;
    private State currentState;
    private boolean assigned;



    public enum State {
        OPEN, CLOSE
    }


    public Window(int id) {
        this.id = id;
        this.currentState = State.CLOSE;
        this.assigned = false;
    }

    public int getId() {
        return id;
    }

    public State getCurrentState() {
        return currentState;
    }


    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public boolean isOpen() {
        return getCurrentState().equals(State.OPEN);
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }
}
