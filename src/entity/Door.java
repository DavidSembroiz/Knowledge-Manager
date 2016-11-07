package entity;

public class Door {

    private State currentState;

    public enum State {
        OPEN, CLOSE
    }


    public Door() {
        this.currentState = State.CLOSE;
    }


    public State getCurrentState() {
        return currentState;
    }


    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

}
