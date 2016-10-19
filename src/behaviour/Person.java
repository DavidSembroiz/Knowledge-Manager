package behaviour;

import behaviour.PeopleManager.Action;
import behaviour.PeopleManager.State;
import domain.Debugger;

public class Person {
	
	private String name;
	private State currentState;
	private Action currentAction;
	private String location;
	private UserProfile profile;
	private UserParams params;
	private int nextActionSteps;
	private int remainingSteps;
	private boolean acting;
	
	public Person(String name, UserProfile prof, UserParams param) {
		this.currentAction = Action.MOVE;
		this.currentState = State.OUTSIDE;
		this.name = name;
		this.profile = prof;
		this.params = param;
		this.nextActionSteps = -1;
		this.remainingSteps = -1;
		this.location = "";
		acting = false;
	}
	
	public void assignState(State st) {
		this.currentState = st;
	}

	public int getRemainingSteps() {
		return remainingSteps;
	}

	public void setRemainingSteps(int remainingSteps) {
		this.remainingSteps = remainingSteps;
	}
	
	public void decreaseRemainingSteps() {
		--remainingSteps;
	}

	public int getNextActionSteps() {
		return nextActionSteps;
	}

	public void setNextActionSteps(int nextActionSteps) {
		this.nextActionSteps = nextActionSteps;
	}
	
	public void decreaseNextActionSteps() {
		--nextActionSteps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public UserProfile getProfile() {
		return profile;
	}

	public void setProfile(UserProfile profile) {
		this.profile = profile;
	}

	public boolean isActing() {
		return acting;
	}

	public void setActing(boolean acting) {
		this.acting = acting;
	}

	public Action getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
	}

	public UserParams getParams() {
		return params;
	}

	public void setParams(UserParams params) {
		this.params = params;
	}

	public void assignAction(Action a, String dest, int next, int duration) {
		this.currentAction = a;
		this.location = dest;
		this.nextActionSteps = next;
		this.remainingSteps = duration;
		if (Debugger.isEnabled()) Debugger.log("Action " + a.toString() +
											   " assigned to Person " + this.getName() +
											   " next " + next +
											   " duration " + duration);
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void changeState() {
		if (Debugger.isEnabled()) Debugger.log("Executing action...");
		if (currentAction.equals(Action.MOVE)) {
			if (Debugger.isEnabled()) Debugger.log("Person " + this.getName() +
					   " changed from " + currentState.toString() +
					   " to " + State.ROOM.toString());
			currentState = State.ROOM;
			
		}
		else if (currentAction.equals(Action.ENTER)) {
			if (Debugger.isEnabled()) Debugger.log("Person " + this.getName() +
					   " changed from " + currentState.toString() +
					   " to " + State.INSIDE.toString());
			currentState = State.INSIDE;
		}
		else if (currentAction.equals(Action.EXIT)) {
			if (Debugger.isEnabled()) Debugger.log("Person " + this.getName() +
					   " changed from " + currentState.toString() +
					   " to " + State.OUTSIDE.toString());
			currentState = State.OUTSIDE;
		}
		else if (currentAction.equals(Action.LUNCH)) {
			if (Debugger.isEnabled()) Debugger.log("Person " + this.getName() +
					   " changed from " + currentState.toString() +
					   " to " + State.SALON.toString());
			currentState = State.SALON;
		}
	}

	public boolean isInside() {
		if (currentState.equals(State.OUTSIDE)) return false;
		return true;
	}
	
	public boolean hadLunch() {
		return params.hadLunch();
	}

	public void setHadLunch(boolean b) {
		params.setHadLunch(b);
	}
}
