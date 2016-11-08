package entity;

public class Window {

    private State currentState;

    public enum State {
        OPEN, CLOSE
    }


    public Window() {
        this.currentState = State.CLOSE;
    }


    public State getCurrentState() {
        return currentState;
    }


    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

}
