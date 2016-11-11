package entity;

public class Window {

    private int id;
    private State currentState;

    public enum State {
        OPEN, CLOSE
    }


    public Window(int id) {
        this.id = id;
        this.currentState = State.CLOSE;
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

}
