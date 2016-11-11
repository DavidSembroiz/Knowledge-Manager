package entity;

public class Door {

    private int id;
    private State currentState;

    public enum State {
        OPEN, CLOSE
    }


    public Door(int id) {
        this.id = id;
        this.currentState = State.CLOSE;
    }


    public int getId() {
        return this.id;
    }

    public State getCurrentState() {
        return currentState;
    }


    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

}
